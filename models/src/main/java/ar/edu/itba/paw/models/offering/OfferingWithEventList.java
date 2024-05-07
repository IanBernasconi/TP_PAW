package ar.edu.itba.paw.models.offering;

import ar.edu.itba.paw.models.Conversation;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.events.Event;

import java.util.*;
import java.util.stream.Collectors;

public class OfferingWithEventList {

    private final EnumMap<OfferingStatus, List<Event>> events;
    private final Map<Long, Conversation> chatConversationsByEventId;
    private final Map<Long, OfferingStatus> relationStatusByEventId;

    public OfferingWithEventList(List<EventOfferingRelation> relations) {
        this.events = new EnumMap<>(OfferingStatus.class);
        this.chatConversationsByEventId = new HashMap<>();
        this.relationStatusByEventId = new HashMap<>();
        if (relations != null) {
            for (EventOfferingRelation relation : relations) {
                if (!events.containsKey(relation.getStatus())) {
                    events.put(relation.getStatus(), new ArrayList<>());
                }
                events.get(relation.getStatus()).add(relation.getEvent());
                chatConversationsByEventId.put(relation.getEvent().getId(), relation.getConversation());
                relationStatusByEventId.put(relation.getEvent().getId(), relation.getStatus());
            }
        }
    }

    public Map<OfferingStatus, List<Event>> getEvents() {
        return events;
    }

    public List<Event> getAcceptedEvents() {
        return events.getOrDefault(OfferingStatus.ACCEPTED, new ArrayList<>());
    }

    public List<Event> getPendingEvents() {
        return events.getOrDefault(OfferingStatus.PENDING, new ArrayList<>()).stream().filter(event -> event.getDate().after(new Date())).collect(Collectors.toList());
    }

    public List<Event> getDoneEvents() {
        return events.getOrDefault(OfferingStatus.DONE, new ArrayList<>());
    }

    public OfferingStatus getEventStatus(long eventId) {
        return relationStatusByEventId.getOrDefault(eventId, OfferingStatus.NEW);
    }

    public List<Event> getPossibleEvents() {
        List<Event> possibleEvents = new ArrayList<>();
        possibleEvents.addAll(getAcceptedEvents());
        possibleEvents.addAll(getPendingEvents());
        return possibleEvents;
    }

    public boolean hasPendingEvents() {
        return !events.getOrDefault(OfferingStatus.PENDING, new ArrayList<>()).isEmpty();
    }

    public boolean hasAcceptedEvents() {
        return !events.getOrDefault(OfferingStatus.ACCEPTED, new ArrayList<>()).isEmpty();
    }

    public boolean hasDoneEvents() {
        return !events.getOrDefault(OfferingStatus.DONE, new ArrayList<>()).isEmpty();
    }

    public Map<Long, Conversation> getChatConversationsByEventId() {
        return chatConversationsByEventId;
    }

    public Event getEventByConversationId(long conversationId) {
        for (Map.Entry<Long, Conversation> entry : chatConversationsByEventId.entrySet()) {
            if(entry != null && entry.getValue() != null && entry.getValue().getRelation() != null){
                if (entry.getValue().getRelation().getRelationId() == conversationId) {
                    return events.values().stream().flatMap(Collection::stream).filter(event -> event.getId() == entry.getKey()).findFirst().orElse(null);
                }
            }
        }
        return null;
    }

    public Event getPendingEventByConversation(long conversationId){
        for (Map.Entry<Long, Conversation> entry : chatConversationsByEventId.entrySet()) {
            if(entry != null && entry.getValue() != null && entry.getValue().getRelation() != null) {
                if (entry.getValue().getRelation().getRelationId() == conversationId) {
                    return events.getOrDefault(OfferingStatus.PENDING, new ArrayList<>()).stream().filter(event -> event.getId() == entry.getKey()).findFirst().orElse(null);
                }
            }
        }
        return null;
    }


}
