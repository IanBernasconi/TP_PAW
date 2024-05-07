import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.EmailLanguages;
import ar.edu.itba.paw.models.PriceType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.exception.notFound.OfferingNotFoundException;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;
import ar.edu.itba.paw.persistence.EventOfferingDao;
import ar.edu.itba.paw.persistence.OfferingDao;
import ar.edu.itba.paw.services.EmailService;
import ar.edu.itba.paw.services.OfferingServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OfferingServiceImplTest {

    @InjectMocks
    private final OfferingServiceImpl offeringService = new OfferingServiceImpl();

    @Mock
    private OfferingDao mockDao;

    @Mock
    private EventOfferingDao mockEventOfferingDao;

    @Mock
    private EmailService mockEmailService;

    private static final User user1= new User("testpassword", "testuser1", "testemail1@test.com", true, EmailLanguages.getDefaultLanguage());
    private static final User user2 = new User("testpassword", "testuser2", "testemail2@test.com", true, EmailLanguages.getDefaultLanguage());
    private static final Event event1=new Event(user2,"name1", "description1", Date.from(LocalDate.of(2024,6,28).atStartOfDay().toInstant(ZoneOffset.UTC)), 10, District.getDistrictByName("Recoleta"));
    private static final Event event2=new Event(user1,"name2", "description2", Date.from(LocalDate.of(2024,6,29).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Colegiales"));
    private static final Event event3=new Event(user1,"name3", "description3", Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Flores"));
    private static final Offering offering1 = new Offering("name1",user1, OfferingCategory.PHOTOGRAPHY, "description1", 10, 20, PriceType.PER_HOUR, 10,District.getDistrictByName("Recoleta"));
    private static final Offering offering2 = new Offering("name2",user1, OfferingCategory.MUSIC, "description2",  10, 20, PriceType.PER_HOUR, 10,District.getDistrictByName("Colegiales"));
    private static final Offering offering3 = new Offering("name3",user1, OfferingCategory.CATERING, "description3", 10, 20, PriceType.PER_PERSON, 10,District.getDistrictByName("Flores"));
    private static final EventOfferingRelation eventOfferingRelation1 = new EventOfferingRelation(event1, offering1, OfferingStatus.ACCEPTED);
    private static final EventOfferingRelation eventOfferingRelation2 = new EventOfferingRelation(event2, offering1, OfferingStatus.ACCEPTED);
    private static final EventOfferingRelation eventOfferingRelation3 = new EventOfferingRelation(event3, offering1, OfferingStatus.PENDING);

    @Test
    public void testDeleteOffering(){
        Offering mockOffering = mock(Offering.class);

        when(mockDao.getById(anyLong())).thenReturn(Optional.of(mockOffering));
        when(mockOffering.getRelations()).thenReturn(Arrays.asList(eventOfferingRelation1, eventOfferingRelation2, eventOfferingRelation3));
        doNothing().when(mockEmailService).buildAndSendRemoveOfferingFromEventEmailToOrganizer(any(), any(), any(), any(), any(), any(), any());
        doNothing().when(mockDao).deleteOffering(anyLong());
        when(mockEventOfferingDao.changeEventOfferingStatus(any(),any())).thenReturn(OfferingStatus.REJECTED);

        offeringService.deleteOffering(offering1.getId(), "");

        verify(mockDao).deleteOffering(offering1.getId());
        verify(mockEmailService, times(3)).buildAndSendRemoveOfferingFromEventEmailToOrganizer(any(), any(), any(), any(), any(), any(), any());


    }

    @Test(expected = OfferingNotFoundException.class)
    public void testDeleteOfferingNoOffering() {
        offeringService.deleteOffering(offering1.getId(), "");
    }

    @Test
    public void testGetRecommendations(){
        Event mockEvent = mock(Event.class);

        EventOfferingRelation eventOfferingRelation1 = new EventOfferingRelation(mockEvent, offering1, OfferingStatus.ACCEPTED);
        EventOfferingRelation eventOfferingRelation2 = new EventOfferingRelation(mockEvent, offering2, OfferingStatus.ACCEPTED);
        EventOfferingRelation eventOfferingRelation3 = new EventOfferingRelation(mockEvent, offering3, OfferingStatus.ACCEPTED);

        Map<OfferingCategory,List<EventOfferingRelation>> relationsByOfferingCategory = new HashMap<>();
        relationsByOfferingCategory.put(OfferingCategory.PHOTOGRAPHY, Collections.singletonList(eventOfferingRelation1));
        relationsByOfferingCategory.put(OfferingCategory.MUSIC, Collections.singletonList(eventOfferingRelation2));
        relationsByOfferingCategory.put(OfferingCategory.CATERING, Collections.singletonList(eventOfferingRelation3));
        relationsByOfferingCategory.put(OfferingCategory.VENUE, Collections.emptyList());

        when(mockEvent.getRelationsByOfferingCategoryWithDefaults()).thenReturn(relationsByOfferingCategory);


        when(mockDao.getRecommendations(any(),anyInt(),any())).thenReturn(Collections.emptyList());


        offeringService.getRecommendationsForEvent(mockEvent);


        verify(mockDao).getRecommendations(eq(Collections.singletonList(OfferingCategory.VENUE)),anyInt(),any());


    }
}
