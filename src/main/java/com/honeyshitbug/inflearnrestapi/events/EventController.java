package com.honeyshitbug.inflearnrestapi.events;

import com.honeyshitbug.inflearnrestapi.index.IndexController;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

  private final EventRepository eventRepository;

  private final ModelMapper modelMapper;

  private final EventValidator eventValidator;

  @PostMapping
  public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
    if (errors.hasErrors()) {
      return badRequest(errors);
    }
    eventValidator.validate(eventDto, errors);
    if (errors.hasErrors()) {
      return badRequest(errors);
    }

    Event event = modelMapper.map(eventDto, Event.class);
    event.update();
    Event newEvent = eventRepository.save(event);

    EntityModel<Event> eventResource = EntityModel.of(event);
    WebMvcLinkBuilder selfLinkBuilder =
        linkTo(EventController.class)
            .slash(newEvent.getId());

    eventResource.add(linkTo(EventController.class).withRel("query-events"));
    eventResource.add(selfLinkBuilder.withRel("update-event"));
    eventResource.add(selfLinkBuilder.withSelfRel());
    eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
    return ResponseEntity
        .created(selfLinkBuilder.toUri())
        .body(eventResource);
  }

  private ResponseEntity badRequest(Errors errors) {
    EntityModel<Errors> errorResource = getErrorsEntityModel(errors);
    return ResponseEntity.badRequest().body(errorResource);
  }

  @GetMapping
  public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler assembler) {
    Page<Event> page = this.eventRepository.findAll(pageable);
    PagedModel<Event> pagedResource = assembler.toModel(page, e -> {
      EntityModel<Event> entityModel = EntityModel.of((Event) e);
      entityModel.add(linkTo(EventController.class).slash(((Event) e).getId()).withSelfRel());
      return entityModel;
    });
    pagedResource.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
    return ResponseEntity.ok(pagedResource);
  }

  @GetMapping("/{id}")
  public ResponseEntity getEvent(@PathVariable Integer id) {
    Optional<Event> optionalEvent = this.eventRepository.findById(id);
    if (optionalEvent.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Event event = optionalEvent.get();
    EntityModel<Event> eventResource = getEventModel(event);
    eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
    return ResponseEntity.ok(eventResource);
  }

  @PutMapping("/{id}")
  public ResponseEntity updateEvent(
      @PathVariable Integer id,
      @RequestBody @Valid EventDto eventDto,
      Errors errors) {
    Optional<Event> optionalEvent = this.eventRepository.findById(id);
    if (optionalEvent.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    if (errors.hasErrors()) {
      return badRequest(errors);
    }
    this.eventValidator.validate(eventDto, errors);
    if (errors.hasErrors()) {
      return badRequest(errors);
    }

    Event existingEvent = optionalEvent.get();
    modelMapper.map(eventDto, existingEvent);
    Event event = eventRepository.save(existingEvent);
    EntityModel<Event> eventResource = getEventModel(event);
    eventResource.add(Link.of("/docs/index.html#resources-events-update").withRel("profile"));
    return ResponseEntity.ok(eventResource);
  }

  private EntityModel<Event> getEventModel(Event event) {
    EntityModel<Event> model = EntityModel.of(event);
    model.add(linkTo(EventController.class).slash((event.getId())).withSelfRel());
    return model;
  }

  private EntityModel<Errors> getErrorsEntityModel(Errors errors) {
    EntityModel<Errors> errorResource = EntityModel.of(errors);
    errorResource.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    return errorResource;
  }
}
