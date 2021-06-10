package com.honeyshitbug.inflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class EventControllerTests {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  EventRepository eventRepository;

  @Test
  void createEvent() throws Exception {
    Event event = Event.builder()
        .name("Spring")
        .description("Rest Api Development with Spring")
        .beginEnrollmentDateTime(LocalDateTime.of(2021, 06, 7, 11, 11))
        .closeEnrollmentDateTime(LocalDateTime.of(2021, 06, 8, 11, 11))
        .beginEventDateTime(LocalDateTime.of(2021, 06, 9, 11, 11))
        .endEventDateTime(LocalDateTime.of(2021, 06, 10, 11, 11))
        .basePrice(100)
        .maxPrice(200)
        .limitOfEnrollment(100)
        .location("강남역 D2 스타트업 팩토리")
        .build();
    event.setId(10);
    Mockito.when(eventRepository.save(event)).thenReturn(event);

    mockMvc.perform(post("/api/events")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaTypes.HAL_JSON)
        .content(objectMapper.writeValueAsString(event)))
        .andExpect(status().isCreated())
        .andDo(print())
        .andExpect(jsonPath("id").exists())
        .andExpect(jsonPath("id").exists())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE));
  }
}
