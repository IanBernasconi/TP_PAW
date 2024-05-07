package ar.edu.itba.paw.models.events;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventsList {

    private final List<Event> events;

    public EventsList(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public EventsList getEventsWithOffering(long offeringId) {
        return new EventsList(events.stream()
                .filter(event -> event != null && event.getOfferings() != null && event.getOfferings().stream()
                        .anyMatch(offering -> offering != null && offering.getId() == offeringId))
                .collect(Collectors.toList()));
    }

    public EventsList getEventsWithoutOffering(long offeringId) {
        return new EventsList(events.stream()
                .filter(event -> event != null && event.getOfferings() != null && event.getOfferings().stream()
                        .noneMatch(offering -> offering != null && offering.getId() == offeringId))
                .collect(Collectors.toList()));
    }

    public Optional<Event> findEventById(long eventId) {
        return events.stream().filter(event -> event.getId() == eventId).findFirst();
    }

}
