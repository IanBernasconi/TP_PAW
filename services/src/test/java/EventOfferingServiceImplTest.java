import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.EmailLanguages;
import ar.edu.itba.paw.models.PriceType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;
import ar.edu.itba.paw.persistence.EventOfferingDao;
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
public class EventOfferingServiceImplTest {

    @InjectMocks
    private final EventOfferingServiceImpl eventOfferingService = new EventOfferingServiceImpl();

    @Mock
    private EventOfferingDao mockDao;

    @Mock
    private EmailService mockEmailService;

    @Mock
    private EventService mockEventService;

    @Mock
    private OfferingService mockOfferingService;

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



    @Test
    public void testChangeOfferingStatusToPending(){
        EventOfferingRelation mockEventOfferingRelation = mock(EventOfferingRelation.class);

        when(mockDao.getRelation(anyLong())).thenReturn(Optional.of(mockEventOfferingRelation));
        when(mockDao.changeEventOfferingStatus(any(),any())).thenReturn(OfferingStatus.PENDING);
        when(mockEventOfferingRelation.getOffering()).thenReturn(offering1);
        when(mockEventOfferingRelation.getEvent()).thenReturn(event1);
        when(mockMessageSource.getMessage(anyString(),any(),any())).thenReturn("");
        doNothing().when(mockEmailService).buildAndSendEmailForContactProvider(anyString(),anyString(),any(),anyString(),any(),anyString(),anyString(),anyString(),anyInt(),anyString());

        eventOfferingService.changeOfferingStatus(1,OfferingStatus.PENDING,"","");

        verify(mockDao,times(1)).changeEventOfferingStatus(mockEventOfferingRelation,OfferingStatus.PENDING);
        verify(mockMessageService,times(1)).addMessage(mockEventOfferingRelation,event1.getUser().getId(),"");
        verify(mockEmailService,times(1)).buildAndSendEmailForContactProvider(anyString(),anyString(),any(),anyString(),any(),anyString(),anyString(),anyString(),anyInt(),anyString());

    }

    @Test
    public void testChangeOfferingStatusToAccepted(){
        EventOfferingRelation mockEventOfferingRelation = mock(EventOfferingRelation.class);

        when(mockDao.getRelation(anyLong())).thenReturn(Optional.of(mockEventOfferingRelation));
        when(mockDao.changeEventOfferingStatus(any(),any())).thenReturn(OfferingStatus.ACCEPTED);
        when(mockEventOfferingRelation.getOffering()).thenReturn(offering1);
        when(mockEventOfferingRelation.getEvent()).thenReturn(event1);
        doNothing().when(mockEmailService).buildAndSendEmailForAnswerEventOfferingRequest(anyString(),anyString(),any(),any(),anyString());
        eventOfferingService.changeOfferingStatus(1,OfferingStatus.ACCEPTED,"","");

        verify(mockDao,times(1)).changeEventOfferingStatus(mockEventOfferingRelation,OfferingStatus.ACCEPTED);
        verify(mockEmailService,times(1)).buildAndSendEmailForAnswerEventOfferingRequest(anyString(),anyString(),any(),any(),anyString());
    }


   /* @Test
    public void testGetAcceptedRelationsIdsByDate() {
        Offering mockOffering = mock(Offering.class);
        Event event1 = new Event(user1,"name1", "description1",Date.from(LocalDate.of(2024,6,28).atStartOfDay().toInstant(ZoneOffset.UTC)), 10, District.getDistrictByName("Recoleta"));
        Event event2 = new Event(user1,"name2", "description2", Date.from(LocalDate.of(2024,6,29).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Colegiales"));
        Event event3 = new Event(user1,"name3", "description3", Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Flores"));

        when(mockOfferingService.getOfferingsByUser(anyLong(), 0, 10)).thenReturn(Collections.singletonList(mockOffering));
        when(mockOffering.getRelations()).thenReturn(Arrays.asList(
                new EventOfferingRelation(event1, mockOffering, OfferingStatus.PENDING),
                new EventOfferingRelation(event2, mockOffering, OfferingStatus.ACCEPTED),
                new EventOfferingRelation(event3, mockOffering, OfferingStatus.ACCEPTED)
        ));

        Map<Date,List<Long>> acceptedRelationsIdsByDate = eventOfferingService.getAcceptedRelationsIdsByDate(user1.getId());

        assertEquals(2, acceptedRelationsIdsByDate.size());
        assertEquals(1, acceptedRelationsIdsByDate.get(event2.getDate()).size());
        assertEquals(1, acceptedRelationsIdsByDate.get(event3.getDate()).size());
        assertEquals(event2.getId(), (long) acceptedRelationsIdsByDate.get(event2.getDate()).get(0));
        assertEquals(event3.getId(), (long) acceptedRelationsIdsByDate.get(event3.getDate()).get(0));
    }

    @Test
    public void testGetAcceptedRelationsIdsByDateNoOfferings() {
        when(mockOfferingService.getOfferingsByUser(anyLong(), 0, 10)).thenReturn(Collections.emptyList());
        Map<Date,List<Long>> acceptedRelationsIdsByDate = eventOfferingService.getAcceptedRelationsIdsByDate(user1.getId());

        assertEquals(0, acceptedRelationsIdsByDate.size());
    }

    @Test
    public void testGetPendingRelationsIdsByDate() {
        Offering mockOffering = mock(Offering.class);
        Event event1 = new Event(user1,"name1", "description1",Date.from(LocalDate.of(2024,6,28).atStartOfDay().toInstant(ZoneOffset.UTC)), 10, District.getDistrictByName("Recoleta"));
        Event event2 = new Event(user1,"name2", "description2", Date.from(LocalDate.of(2024,6,29).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Colegiales"));
        Event event3 = new Event(user1,"name3", "description3", Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Flores"));

        when(mockOfferingService.getOfferingsByUser(anyLong(), 0, 10)).thenReturn(Collections.singletonList(mockOffering));
        when(mockOffering.getRelations()).thenReturn(Arrays.asList(
                new EventOfferingRelation(event1, mockOffering, OfferingStatus.PENDING),
                new EventOfferingRelation(event2, mockOffering, OfferingStatus.ACCEPTED),
                new EventOfferingRelation(event3, mockOffering, OfferingStatus.ACCEPTED)
        ));

        Map<Date,List<Long>> pendingRelationsIdsByDate = eventOfferingService.getPendingRelationsIdsByDate(user1.getId());

        assertEquals(1, pendingRelationsIdsByDate.size());
        assertEquals(1, pendingRelationsIdsByDate.get(event1.getDate()).size());
        assertEquals(event1.getId(), (long) pendingRelationsIdsByDate.get(event1.getDate()).get(0));
    }

    @Test
    public void testGetPendingRelationsIdsByDateNoOfferings() {
        when(mockOfferingService.getOfferingsByUser(anyLong(),0, 10)).thenReturn(Collections.emptyList());
        Map<Date,List<Long>> pendingRelationsIdsByDate = eventOfferingService.getPendingRelationsIdsByDate(user1.getId());

        assertEquals(0, pendingRelationsIdsByDate.size());
    }*/

}
