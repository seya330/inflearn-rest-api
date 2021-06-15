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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

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
      EntityModel<Errors> errorResource = getErrorsEntityModel(errors);
      return ResponseEntity.badRequest().body(errorResource);
    }
    eventValidator.validate(eventDto, errors);
    if (errors.hasErrors()) {
      EntityModel<Errors> errorResource = getErrorsEntityModel(errors);
      return ResponseEntity.badRequest().body(errorResource);
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

  @GetMapping
  public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler assembler) {
    Page<Event> page = this.eventRepository.findAll(pageable);
    PagedModel<Event> pagedResource = assembler.toModel(page, e -> {
      EntityModel<Event> entityModel = EntityModel.of((Event)e);
      entityModel.add(linkTo(EventController.class).slash(((Event) e).getId()).withSelfRel());
      return entityModel;
    });
    pagedResource.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
    return ResponseEntity.ok(pagedResource);
  }

  private EntityModel<Errors> getErrorsEntityModel(Errors errors) {
    EntityModel<Errors> errorResource = EntityModel.of(errors);
    errorResource.add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    return errorResource;
  }
}
