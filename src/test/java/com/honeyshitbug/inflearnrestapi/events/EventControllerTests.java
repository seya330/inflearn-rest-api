package com.honeyshitbug.inflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeyshitbug.inflearnrestapi.common.RestDocsConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTests {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  @TestDescription("정상적으로 이벤트를 생성하는 메소드")
  void createEvent() throws Exception {
    EventDto event = EventDto.builder()
        .name("Spring")
        .description("Rest Api Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2021, 6, 7, 11, 11))
        .closeEnrollmentDateTime(LocalDateTime.of(2021, 6, 8, 11, 11))
        .beginEventDateTime(LocalDateTime.of(2021, 6, 9, 11, 11))
        .endEventDateTime(LocalDateTime.of(2021, 6, 10, 11, 11))
        .basePrice(100)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타트업 팩토리")
        .build();

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaTypes.HAL_JSON)
        .content(objectMapper.writeValueAsString(event)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andExpect(jsonPath("id").exists())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        .andExpect(jsonPath("id").value(Matchers.not(100)))
        .andExpect(jsonPath("free").value(false))
        .andExpect(jsonPath("offline").value(true))
        .andExpect(jsonPath("eventStatus").value("DRAFT"))
        .andExpect(jsonPath("_links.self").exists())
        .andExpect(jsonPath("_links.query-events").exists())
        .andExpect(jsonPath("_links.update-event").exists())
        .andDo(document("create-event",
            links(
                halLinks(),
                linkWithRel("self").description("link to self"),
                linkWithRel("query-events").description("link to query event"),
                linkWithRel("update-event").description("link to update event")
            ),
            requestHeaders(
                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
            ),
            requestFields(
                fieldWithPath("name").description("name of new event"),
                fieldWithPath("description").description("description of new Event"),
                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of enrollment"),
                fieldWithPath("closeEnrollmentDateTime").description("date time of close of enrollment"),
                fieldWithPath("beginEventDateTime").description("date time of begin of event"),
                fieldWithPath("endEventDateTime").description("date time of end of event"),
                fieldWithPath("location").description("location"),
                fieldWithPath("basePrice").description("base price"),
                fieldWithPath("maxPrice").description("max price"),
                fieldWithPath("limitOfEnrollment").description("enrollment limit")
            ),
            responseHeaders(
                headerWithName(HttpHeaders.LOCATION).description("location"),
                headerWithName(HttpHeaders.CONTENT_TYPE).description("contentType")
            ),
            responseFields(
                fieldWithPath("id").description(""),
                fieldWithPath("name").description("name of new event"),
                fieldWithPath("description").description("description of new Event"),
                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of enrollment"),
                fieldWithPath("closeEnrollmentDateTime").description("date time of close of enrollment"),
                fieldWithPath("beginEventDateTime").description("date time of begin of event"),
                fieldWithPath("endEventDateTime").description("date time of end of event"),
                fieldWithPath("location").description("location"),
                fieldWithPath("basePrice").description("base price"),
                fieldWithPath("maxPrice").description("max price"),
                fieldWithPath("limitOfEnrollment").description("enrollment limit"),
                fieldWithPath("offline").description("is offline"),
                fieldWithPath("free").description("is free"),
                fieldWithPath("eventStatus").description("event status"),
                fieldWithPath("_links.self.href").description("link to another state"),
                fieldWithPath("_links.query-events.href").description("link to another state"),
                fieldWithPath("_links.update-event.href").description("link to another state")
            )
        ))
    ;
  }

  @Test
  @TestDescription("입력받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
  void createEvent_badRequest() throws Exception {
    Event event = Event.builder()
        .name("Spring")
        .description("Rest Api Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2021, 6, 7, 11, 11))
        .closeEnrollmentDateTime(LocalDateTime.of(2021, 6, 8, 11, 11))
        .beginEventDateTime(LocalDateTime.of(2021, 6, 9, 11, 11))
        .endEventDateTime(LocalDateTime.of(2021, 6, 10, 11, 11))
        .basePrice(100)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타트업 팩토리")
        .free(true)
        .build();

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaTypes.HAL_JSON)
        .content(objectMapper.writeValueAsString(event)))
        .andDo(print())
        .andExpect(status().isBadRequest())
    ;
  }

  @Test
  @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
  void createEvent_badRequest_empty_input() throws Exception {
    EventDto eventDto = EventDto.builder().build();

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(eventDto)))
        .andExpect(status().isBadRequest())
    ;
  }

  @Test
  @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
  void createEvent_badRequest_wrong_input() throws Exception {
    EventDto eventDto = EventDto.builder()
        .name("Spring")
        .description("Rest Api Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2021, 6, 7, 11, 11))
        .closeEnrollmentDateTime(LocalDateTime.of(2021, 5, 8, 11, 11))
        .beginEventDateTime(LocalDateTime.of(2021, 4, 9, 11, 11))
        .endEventDateTime(LocalDateTime.of(2021, 3, 10, 11, 11))
        .basePrice(10000)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타트업 팩토리")
        .build();

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(eventDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$[0].objectName").exists())
        .andExpect(jsonPath("$[0].defaultMessage").exists())
        .andExpect(jsonPath("$[0].code").exists());
  }
}
