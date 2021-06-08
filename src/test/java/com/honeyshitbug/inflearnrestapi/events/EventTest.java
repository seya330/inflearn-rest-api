package com.honeyshitbug.inflearnrestapi.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

  @Test
  void builder() {
    Event event = Event.builder()
        .name("Inflearn Spring REST Api")
        .description("REST API development with Spring")
        .build();
    assertThat(event).isNotNull();
  }

  @Test
  void javaBean() {
    //Given
    final String name = "Event";
    final String description = "Spring";

    //When
    Event event = new Event();
    event.setName(name);
    event.setDescription(description);

    //Then
    assertThat(event.getName()).isEqualTo(name);
    assertThat(event.getDescription()).isEqualTo(description);
  }
}