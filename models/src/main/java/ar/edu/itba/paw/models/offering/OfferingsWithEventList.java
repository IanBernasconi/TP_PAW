package ar.edu.itba.paw.models.offering;

import ar.edu.itba.paw.models.Conversation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OfferingsWithEventList {

    private final List<Offering> offerings;

    public OfferingsWithEventList(List<Offering> offerings) {
        this.offerings = offerings;
    }

    public List<Offering> getOfferings() {
        return offerings;
    }

    public OfferingsWithEventList getOfferingsWithOnlyDoneEvents() {
        offerings.forEach(offering -> {
            offering.getMappedRelationInfo().getEvents().remove(OfferingStatus.PENDING);
            offering.getMappedRelationInfo().getEvents().remove(OfferingStatus.ACCEPTED);
        });
        return new OfferingsWithEventList(offerings.stream().filter(offering -> offering.getMappedRelationInfo().hasDoneEvents()).collect(Collectors.toList()));
    }

    public OfferingsWithEventList getOfferingsWithCurrentEvents() {
        return new OfferingsWithEventList(offerings.stream().filter(offering -> offering.getMappedRelationInfo().hasAcceptedEvents()
                || offering.getMappedRelationInfo().hasPendingEvents()).collect(Collectors.toList()));
    }


    public int getProviderUnreadMessagesCount() {
        return offerings.stream().flatMap(offering ->
                        offering.getMappedRelationInfo()
                                .getChatConversationsByEventId()
                                .values().stream().filter(Objects::nonNull))
                .mapToInt(Conversation::getProviderUnreadMessagesCount)
                .sum();
    }

    public Offering getOfferingByConversationId(long conversationId) {
        return offerings.stream().filter(
                offering -> offering.getMappedRelationInfo().getChatConversationsByEventId().values().stream().filter(Objects::nonNull).
                        anyMatch(conversation -> conversation.getRelation().getRelationId() == conversationId)).findFirst().orElse(null);

    }
}
