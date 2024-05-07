package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.EmailLanguage;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;

import java.util.Date;

public interface EmailService {


    void buildAndSendRemoveOfferingFromEventEmailToProvider(String providerEmail, String eventName, Date eventDate, String offeringName, String providerName, EmailLanguage language, String detailsUrl);

    void buildAndSendRemoveOfferingFromEventEmailToOrganizer(String organizerEmail, String eventName, Date eventDate, String offeringName, String organizerName, EmailLanguage language, String detailsUrl);

    void buildAndSendVerificationEmail(String email, String userName, String detailsUrl, EmailLanguage language);

    void buildAndSendResetPasswordEmail(String email, String userName, String detailsUrl, EmailLanguage language);

    void buildAndSendEmailForAnswerEventOfferingRequest(String eventCreatorEmail, String providerName, EmailLanguage language, OfferingStatus newStatus, String detailsUrl);

    void buildAndSendEmailForContactProvider(String emailTo, String ownerName, String message, String detailsUrl, EmailLanguage language, String eventCreatorName, String eventName, String eventDate, int numberOfGuests, String offeringName);

    void buildAndSendModifyEventEmailToProvider(String providerEmail, String eventName, String offeringName, String providerName, EmailLanguage language, String detailsUrl, String emailContent);

    void buildAndSendInviteEmail(String email, String name, String username, Date date, District district,String description, EmailLanguage language, String acceptUrl, String rejectUrl, String detailsUrl);
}
