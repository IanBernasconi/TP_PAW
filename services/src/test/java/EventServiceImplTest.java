import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.EmailLanguages;
import ar.edu.itba.paw.models.PriceType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.eventOfferingRelation.ChatMessage;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.events.Guest;
import ar.edu.itba.paw.models.events.GuestStatus;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;
import ar.edu.itba.paw.persistence.EventDao;
import ar.edu.itba.paw.services.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceImplTest {

    @InjectMocks
    private final EventService eventOfferingService = new EventServiceImpl();

    @Mock
    private EventDao mockDao;

    @Mock
    private EmailService mockEmailService;

    @Mock
    private MessageService mockMessageService;

    @Mock
    private MessageSource mockMessageSource;

    private static final User user1= new User("testpassword", "testuser1", "testemail1@test.com", true,EmailLanguages.getDefaultLanguage());
    private static final User user2 = new User("testpassword", "testuser2", "testemail2@test.com", true, EmailLanguages.getDefaultLanguage());
    private static final Event event1=new Event(user2,"name1", "description1",Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 10, District.getDistrictByName("Recoleta"));
    private static final Event event2=new Event(user1,"name2", "description2", Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Colegiales"));
    private static final Event event3=new Event(user1,"name3", "description3", Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Flores"));
    private static final Offering offering1 = new Offering("name1",user1, OfferingCategory.PHOTOGRAPHY, "description1", 10, 20,PriceType.PER_HOUR, 10,District.getDistrictByName("Recoleta"));
    private static final Offering offering2 = new Offering("name2",user2, OfferingCategory.PHOTOGRAPHY, "description2", 10, 20,PriceType.PER_HOUR, 10,District.getDistrictByName("Colegiales"));


    @Test
    public void testUpdateMyEvent(){
        Event mockEvent = mock(Event.class);
        when(mockEvent.getEventOfferingRelations()).thenReturn(Arrays.asList(new EventOfferingRelation(mockEvent,offering1,OfferingStatus.ACCEPTED),new EventOfferingRelation(mockEvent,offering2,OfferingStatus.ACCEPTED)));
        when(mockEvent.getDate()).thenReturn(Date.from(LocalDate.of(2025,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)));
        when(mockEvent.getNumberOfGuests()).thenReturn(20);
        when(mockEvent.getDistrict()).thenReturn(District.getDistrictByName("Palermo"));
        when(mockEvent.getUser()).thenReturn(user1);
        when(mockMessageSource.getMessage(any(),any(),any())).thenReturn("test");
        when(mockMessageService.addMessage(any(),anyLong(),any())).thenReturn(mock(ChatMessage.class));
        doNothing().when(mockEmailService).buildAndSendModifyEventEmailToProvider(any(),any(),any(),any(),any(),any(),any());

        eventOfferingService.updateMyEvent(mockEvent,"new name","new description",20,Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)),District.getDistrictByName("Recoleta"),"");

        verify(mockDao,times(1)).update(any(),any(),any(),anyInt(),any(),any());
        verify(mockEmailService,times(1)).buildAndSendModifyEventEmailToProvider(any(),any(),any(),any(),any(),any(),any());
        verify(mockMessageService,times(2)).addMessage(any(),anyLong(),any());

    }

    @Test
    public void testUpdateMyEventNoOfferings(){
        Event mockEvent = mock(Event.class);

        eventOfferingService.updateMyEvent(mockEvent,"new name","new description",20,Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)),District.getDistrictByName("Recoleta"),"");

        verify(mockDao,times(1)).update(any(),any(),any(),anyInt(),any(),any());
        verify(mockEmailService,times(0)).buildAndSendModifyEventEmailToProvider(any(),any(),any(),any(),any(),any(),any());

    }

    @Test
    public void testAnswerGuestInvitation(){
        Guest mockGuest = mock(Guest.class);

        when(mockDao.getGuestById(anyLong())).thenReturn(Optional.ofNullable(mockGuest));
        when(mockGuest.getStatus()).thenReturn(GuestStatus.PENDING);
        when(mockGuest.getToken()).thenReturn("token");

        eventOfferingService.answerGuestInvitation(1L,1L,GuestStatus.ACCEPTED,"token");

        verify(mockDao,times(1)).updateGuestStatus(anyLong(),anyLong(),any());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnswerGuestInvitationInvalidStatus(){
        Guest mockGuest = mock(Guest.class);

        when(mockDao.getGuestById(anyLong())).thenReturn(Optional.ofNullable(mockGuest));
        when(mockGuest.getStatus()).thenReturn(GuestStatus.ACCEPTED);

        eventOfferingService.answerGuestInvitation(1L,1L,GuestStatus.ACCEPTED,"token");

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnswerGuestInvitationInvalidToken(){
        Guest mockGuest = mock(Guest.class);

        when(mockDao.getGuestById(anyLong())).thenReturn(Optional.ofNullable(mockGuest));
        when(mockGuest.getStatus()).thenReturn(GuestStatus.PENDING);
        when(mockGuest.getToken()).thenReturn("token");

        eventOfferingService.answerGuestInvitation(1L,1L,GuestStatus.ACCEPTED,"invalidtoken");

    }

    @Test(expected = org.hibernate.ObjectNotFoundException.class)
    public void testAnswerGuestInvitationNotFound(){
        when(mockDao.getGuestById(anyLong())).thenReturn(Optional.empty());

        eventOfferingService.answerGuestInvitation(1L,1L,GuestStatus.ACCEPTED,"token");

    }

    @Test
    public void testDeleteEvent(){
        Event mockEvent = mock(Event.class);
        when(mockEvent.getEventOfferingRelations()).thenReturn(Arrays.asList(new EventOfferingRelation(mockEvent,offering1,OfferingStatus.ACCEPTED),new EventOfferingRelation(mockEvent,offering2,OfferingStatus.ACCEPTED)));
        when(mockEvent.getId()).thenReturn(1L);
        doNothing().when(mockEmailService).buildAndSendRemoveOfferingFromEventEmailToProvider(anyString(),anyString(),any(),anyString(),anyString(),any(),anyString());
        when(mockEvent.getName()).thenReturn(event1.getName());
        when(mockEvent.getDate()).thenReturn(event1.getDate());

        eventOfferingService.deleteEvent(mockEvent,"");

        verify(mockDao,times(1)).delete(anyLong());
        verify(mockEmailService,times(2)).buildAndSendRemoveOfferingFromEventEmailToProvider(anyString(),anyString(),any(),anyString(),anyString(),any(),anyString());
    }


    @Test
    public void testDeleteEventNoEventOfferingRelations(){
        Event mockEvent = mock(Event.class);
        when(mockEvent.getEventOfferingRelations()).thenReturn(new ArrayList<>());
        when(mockEvent.getId()).thenReturn(1L);

        eventOfferingService.deleteEvent(mockEvent,"");

        verify(mockDao,times(1)).delete(anyLong());
        verify(mockEmailService,times(0)).buildAndSendRemoveOfferingFromEventEmailToProvider(anyString(),anyString(),any(),anyString(),anyString(),any(),anyString());
    }


}
