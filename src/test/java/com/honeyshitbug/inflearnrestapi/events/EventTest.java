package com.honeyshitbug.inflearnrestapi.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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

  @ParameterizedTest
  @MethodSource
  void testFree(int basePrice, int maxPrice, boolean isFree) {
    //Given
    Event event = Event.builder()
        .basePrice(basePrice)
        .maxPrice(maxPrice)
        .build();

    //When
    event.update();

    //Then
    assertThat(event.isFree()).isEqualTo(isFree);
  }

  private static Object[] testFree() {
    return new Object[][]{
        new Object[]{0, 0, true},
        new Object[]{100, 0, false},
        new Object[]{0, 100, false},
        new Object[]{100, 200, false}
    };
  }

  @ParameterizedTest
  @MethodSource
  void testOffline(String location, boolean isOffline) {
    //Given
    Event event = Event.builder()
        .location(location)
        .build();

    //When
    event.update();

    //Then
    assertThat(event.isOffline()).isEqualTo(isOffline);
  }

  private static Object[] testOffline() {
    return new Object[][]{
        new Object[]{"강남", true},
        new Object[]{null, false},
        new Object[]{"            ", false}
    };
  }
}