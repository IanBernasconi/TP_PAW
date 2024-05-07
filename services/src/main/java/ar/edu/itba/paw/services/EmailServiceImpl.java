package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.EmailLanguage;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService{

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    public static final String LANGUAGE_KEY = "language";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MessageSource messageSource;

    private void sendEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set recipient, subject, and other properties
            helper.setTo(to);
            helper.setSubject(subject);


            // Process the Thymeleaf template with the provided model
            Context context = new Context();
            context.setVariables(templateModel);
            String emailContent = templateEngine.process("/" + templateName, context);

            // Set the HTML content of the email
            helper.setText(emailContent, true);

            // Send the email
            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            LOGGER.error("Error sending email to {}", to, e);
        }
    }

    @Async
    @Override
    public void buildAndSendRemoveOfferingFromEventEmailToProvider(String providerEmail, String eventName, Date eventDate, String offeringName, String providerName, EmailLanguage language, String detailsUrl) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = simpleDateFormat.format(eventDate);
        buildAndSendRemoveOfferingFromEventEmail(providerEmail, eventName, formattedDate, offeringName, providerName, language, detailsUrl, true);
    }

    @Async
    @Override
    public void buildAndSendRemoveOfferingFromEventEmailToOrganizer(String organizerEmail, String eventName, Date eventDate, String offeringName, String organizerName, EmailLanguage language, String detailsUrl) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = simpleDateFormat.format(eventDate);
        buildAndSendRemoveOfferingFromEventEmail(organizerEmail, eventName, formattedDate, offeringName, organizerName, language, detailsUrl, false);
    }

    @Async
    @Override
    public void buildAndSendVerificationEmail(String email, String userName, String detailsUrl, EmailLanguage language) {
        Locale localeWithUserLanguage = new Locale(language.getLanguage());

        Map<String,Object> model = buildEmailMap(language, "email.registration.title", "email.registration.greeting", userName == null? email : userName, "email.registration.content", "email.registration.button", detailsUrl);


        String emailSubject = messageSource.getMessage("email.registration.subject", null, localeWithUserLanguage);
        String emailSubtitle = messageSource.getMessage("email.registration.subtitle", null, localeWithUserLanguage);

        model.put("subtitle", emailSubtitle);
        model.put("detailsUrl", detailsUrl);

        sendEmail(email, emailSubject, "verification-email-template", model);
    }

    @Async
    @Override
    public void buildAndSendResetPasswordEmail(String email, String userName, String detailsUrl, EmailLanguage language) {
        Locale localeWithUserLanguage = new Locale(language.getLanguage());

        Map<String,Object> model = buildEmailMap(language, "email.resetPassword.title", "email.resetPassword.greeting", userName == null? email : userName, "email.resetPassword.content", "email.resetPassword.button", detailsUrl);

        String emailSubject = messageSource.getMessage("email.resetPassword.subject", null, localeWithUserLanguage);
        String emailFooter = messageSource.getMessage("email.resetPassword.footer", null, localeWithUserLanguage);

        model.put("account", email);
        model.put("footer", emailFooter);
        sendEmail(email, emailSubject, "reset-password-email", model);
    }
    @Async
    @Override
    public void buildAndSendEmailForAnswerEventOfferingRequest(String eventCreatorEmail, String providerName, EmailLanguage emailLanguage, OfferingStatus newStatus, String detailsUrl){
        Locale localeWithEventCreatorLanguage = Locale.forLanguageTag(emailLanguage.getLanguage());

        String emailSubjectKey;
        String emailSubtitleKey;
        String emailContentKey;
        String emailButtonKey;

        String emailTitle = messageSource.getMessage("email.answer-offering-request.title",null, localeWithEventCreatorLanguage);

        switch (newStatus) {
            case ACCEPTED:
                emailSubjectKey = "email.accepted-offering.subject";
                emailSubtitleKey = "email.accepted-offering.subtitle";
                emailContentKey = "email.accepted-offering.content";
                emailButtonKey = "email.accepted-offering.button";
                break;
            case REJECTED:
                emailSubjectKey = "email.rejected-offering.subject";
                emailSubtitleKey = "email.rejected-offering.subtitle";
                emailContentKey = "email.rejected-offering.content";
                emailButtonKey = "email.rejected-offering.button";
                break;
            default:
                throw new IllegalArgumentException("Invalid EventOfferingAnswer");
        }

        String emailSubject = messageSource.getMessage(emailSubjectKey, new Object[]{providerName}, localeWithEventCreatorLanguage);
        String emailSubtitle = messageSource.getMessage(emailSubtitleKey, null, localeWithEventCreatorLanguage);
        String emailContent = messageSource.getMessage(emailContentKey, new Object[]{providerName}, localeWithEventCreatorLanguage);
        String emailButtonMessage = messageSource.getMessage(emailButtonKey, null, localeWithEventCreatorLanguage);


        Map<String,Object> model = buildModel(detailsUrl, emailTitle, emailSubtitle, emailContent, emailButtonMessage);
        sendEmail(eventCreatorEmail, emailSubject, "answer-event-offering-request-email", model);
    }

    @Async
    @Override
    public void buildAndSendEmailForContactProvider(String ownerEmail,String ownerName, String message, String detailsUrl, EmailLanguage language, String eventCreatorName, String eventName, String eventDate, int numberOfGuests, String offeringName){
        Locale localeWithProviderLanguage = new Locale(language.getLanguage());

        String emailSubject = messageSource.getMessage("email.contact-provider.subject", new Object[]{offeringName}, localeWithProviderLanguage);
        String emailTitle = messageSource.getMessage("email.contact-provider.title",null, localeWithProviderLanguage);
        String emailSubtitle = messageSource.getMessage("email.contact-provider.subtitle", new Object[]{eventCreatorName}, localeWithProviderLanguage);

        String emailButtonMessage = messageSource.getMessage("email.contact-provider.button", null, localeWithProviderLanguage);

        Map<String, Object> model = buildModel(detailsUrl, emailTitle, emailSubtitle, message, emailButtonMessage);
        sendEmail(ownerEmail, emailSubject, "contact-provider-email", model);
    }

    @Async
    @Override
    public void buildAndSendModifyEventEmailToProvider(String providerEmail, String eventName, String offeringName, String providerName, EmailLanguage language, String detailsUrl, String emailContent) {
        Locale localeWithProviderLanguage = new Locale(language.getLanguage());


        String emailSubject = messageSource.getMessage("email.modifyEvent.subject", null, localeWithProviderLanguage);
        String emailTitle = messageSource.getMessage("email.modifyEvent.title", null, localeWithProviderLanguage);
        String emailGreeting = messageSource.getMessage("email.modifyEvent.greeting", new Object[]{providerName}, localeWithProviderLanguage);
        String emailSubtitle = messageSource.getMessage("email.modifyEvent.subtitle", new Object[]{offeringName,eventName}, localeWithProviderLanguage);
        String emailButtonMessage = messageSource.getMessage("email.modifyEvent.button", null, localeWithProviderLanguage);

        Map<String, Object> model = buildModel(detailsUrl, emailTitle, emailSubtitle, emailContent, emailButtonMessage);
        model.put("greeting", emailGreeting);


        sendEmail(providerEmail, emailSubject, "modify-event-email", model);

    }

    @Async
    @Override
    public void buildAndSendInviteEmail(String email, String name,String username, Date date, District district,String description, EmailLanguage language, String acceptUrl, String rejectUrl, String detailsUrl) {
        Locale localeWithUserLanguage = new Locale(language.getLanguage());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = simpleDateFormat.format(date);

        String emailSubject = messageSource.getMessage("email.invite.subject", null, localeWithUserLanguage);
        String emailTitle = messageSource.getMessage("email.invite.title",null, localeWithUserLanguage);
        String emailSubtitle = messageSource.getMessage("email.invite.subtitle", new Object[]{name, formattedDate}, localeWithUserLanguage);
        String emailContent = messageSource.getMessage("email.invite.content", null, localeWithUserLanguage);
        String descriptionMessage = messageSource.getMessage("email.invite.description", new Object[]{description}, localeWithUserLanguage);
        String acceptButtonMessage = messageSource.getMessage("email.invite.acceptButton", null, localeWithUserLanguage);
        String rejectButtonMessage = messageSource.getMessage("email.invite.rejectButton", null, localeWithUserLanguage);

        Map<String, Object> model = buildModel(detailsUrl, emailTitle, emailSubtitle, emailContent, acceptButtonMessage);
        model.put("rejectButtonMessage", rejectButtonMessage);
        model.put("rejectUrl", rejectUrl);
        model.put("acceptUrl", acceptUrl);
        model.put("description", descriptionMessage);



        sendEmail(email, emailSubject, "invite-email", model);

    }

    private Map<String, Object> buildModel(String detailsUrl, String emailTitle, String emailSubtitle, String emailContent, String emailButtonMessage) {
        Map<String,Object> model = new HashMap<>();
        model.put("title", emailTitle);
        model.put("subtitle", emailSubtitle);
        model.put("content", emailContent);
        model.put("buttonMessage", emailButtonMessage);
        model.put("detailsUrl", detailsUrl);
        return model;
    }

    private Map<String, Object> buildEmailMap(EmailLanguage language, String emailTitleMessage, String emailGreetingMessage, String emailGreetingParam, String emailContentMessage, String emailButtonMessage, String detailsUrl){
        Locale localeWithUserLanguage = new Locale(language.getLanguage());

        String emailTitle = messageSource.getMessage(emailTitleMessage,null, localeWithUserLanguage);
        String emailGreeting = messageSource.getMessage(emailGreetingMessage, new Object[]{emailGreetingParam}, localeWithUserLanguage);
        String emailContent = messageSource.getMessage(emailContentMessage, null, localeWithUserLanguage);
        String emailButton = messageSource.getMessage(emailButtonMessage, null, localeWithUserLanguage);

        Map<String,Object> model = new HashMap<>();
        model.put("title", emailTitle);
        model.put("content", emailContent);
        model.put("greeting", emailGreeting);
        model.put("buttonMessage", emailButton);
        model.put("detailsUrl", detailsUrl);
        return model;
    }

    private void buildAndSendRemoveOfferingFromEventEmail(String email, String eventName, String eventDate, String offeringName, String name, EmailLanguage language, String detailsUrl, boolean isProvider) {
        Locale localeWithLanguage = new Locale(language.getLanguage());
        String emailSubject;
        if(isProvider) {
            emailSubject = messageSource.getMessage("email.removeOfferingFromEvent.subject.provider", null, localeWithLanguage);
        }else{
            emailSubject = messageSource.getMessage("email.removeOfferingFromEvent.subject.organizer", null, localeWithLanguage);
        }
        String emailTitle = messageSource.getMessage("email.removeOfferingFromEvent.title", null, localeWithLanguage);
        String emailGreeting = messageSource.getMessage("email.removeOfferingFromEvent.greeting", new Object[]{name}, localeWithLanguage);
        String emailContent;
        if(isProvider) {
            emailContent = messageSource.getMessage("email.removeOfferingFromEvent.content.provider", new Object[]{offeringName, eventName, eventDate}, localeWithLanguage);
        }else{
            emailContent = messageSource.getMessage("email.removeOfferingFromEvent.content.organizer", new Object[]{offeringName, eventName, eventDate}, localeWithLanguage);
        }
        String emailButtonMessage = messageSource.getMessage("email.removeOfferingFromEvent.button", null, localeWithLanguage);

        Map<String, Object> model = new HashMap<>();
        model.put("title", emailTitle);
        model.put("greeting", emailGreeting);
        model.put("content", emailContent);
        model.put("buttonMessage", emailButtonMessage);
        model.put("detailsUrl", detailsUrl);
        sendEmail(email, emailSubject, "remove-offering-from-event-email", model);

    }



}
