package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.exception.notFound.RelationNotFoundException;
import ar.edu.itba.paw.services.EventOfferingService;
import ar.edu.itba.paw.services.EventService;
import ar.edu.itba.paw.services.OfferingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
public class SecurityAccessFunctions {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventOfferingService eventOfferingService;

    @Autowired
    private OfferingService offeringService;


    public boolean hasUserAccess(Authentication authentication, Long id) {
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser") || id == null) {
            return false;
        }
        return id == ((PawAuthUser) authentication.getPrincipal()).getId();
    }

    public boolean isEventOwner(Authentication authentication, Long id) {
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser") || id == null) {
            return false;
        }
        return eventService.isOwner(id.intValue(), ((PawAuthUser) authentication.getPrincipal()).getId());
    }


    public boolean hasEventAccess(Authentication authentication, Long id) {
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser") || id == null) {
            return false;
        }
        final Optional<Event> maybeEvent = eventService.getById(id.intValue());
        return maybeEvent.filter(event -> isEventOwner(authentication, id) || event
                        .getOfferings().stream().anyMatch(offering -> offering.getOwner().getId() == ((PawAuthUser) authentication.getPrincipal()).getId()))
                .isPresent();
    }

    public boolean hasRelationAccess(Authentication authentication, Long id) {
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser")) {
            return false;
        }
        return eventOfferingService.userIsInRelation(id.intValue(), ((PawAuthUser) authentication.getPrincipal()).getId());
    }

    public boolean isOfferingOwner(Authentication authentication, Long id) {
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser") || id == null) {
            return false;
        }
        return offeringService.isOwner(id.intValue(), ((PawAuthUser) authentication.getPrincipal()).getId());
    }

    public boolean isResetPasswordTokenValid(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser")) {
            return false;
        }

        final String token = ((PawAuthUser) authentication.getPrincipal()).getToken();
        if (token == null) {
            return false;
        }
        return token.equals(((PawAuthUser) authentication.getPrincipal()).getUser().getResetPasswordToken());
    }

    public boolean isVerificationTokenValid(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser")) {
            return false;
        }

        final String token = ((PawAuthUser) authentication.getPrincipal()).getToken();
        if (token == null) {
            return false;
        }
        return token.equals(((PawAuthUser) authentication.getPrincipal()).getUser().getVerificationToken());
    }

    public boolean canReviewRelation(Authentication authentication, Long relationId) {
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser")) {
            return false;
        }
        EventOfferingRelation relation = eventOfferingService.getRelationById(relationId).orElseThrow(() -> new RelationNotFoundException(relationId));
        long userId = ((PawAuthUser) authentication.getPrincipal()).getId();

        return relation.getOffering().getOwner().getId() != userId && relation.getEvent().getUser().getId() == userId;
    }

    public boolean hasOfferingLikeAccess(Authentication authentication, Long id) {
        if (authentication == null || authentication.getPrincipal() == null || authentication.getPrincipal().equals("anonymousUser") || id == null) {
            return false;
        }
        return !isOfferingOwner(authentication, id);
    }

}
