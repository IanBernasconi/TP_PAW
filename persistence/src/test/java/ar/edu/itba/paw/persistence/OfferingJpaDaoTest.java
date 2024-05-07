package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.eventOfferingRelation.Review;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.exception.notFound.OfferingNotFoundException;
import ar.edu.itba.paw.models.offering.Like;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;
import ar.edu.itba.paw.models.offering.OfferingFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.persistence.tablesInformation.EventOfferingTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.EventTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.ImageTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.OfferingTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.UserTableInfo.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
public class OfferingJpaDaoTest {


    @Autowired
    private OfferingDao offeringDao;

    @Mock
    private UserDao userDao;

    @Mock
    private ImageDao imageDao;

    private JdbcTemplate jdbcTemplate;


    @Autowired
    private DataSource ds;



    @PersistenceContext
    private EntityManager em;

    private User user1;
    private User user2;
    private User user3;

    private Offering offering1;
    private Offering offering2;
    private Offering offering3;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        jdbcTemplate = new JdbcTemplate(ds);
        em.createNativeQuery("TRUNCATE TABLE "+USER_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+EVENT_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+EVENT_OFFERING_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+OFFERING_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+LIKED_OFFERING_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+IMAGE_TABLE).executeUpdate();

        em.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE offerings_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE images_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE events_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE events_offerings_relation_id_seq RESTART WITH 1").executeUpdate();

        user1= new User("testpassword", "testuser1", "testemail1@test.com",  true, EmailLanguages.getDefaultLanguage());
        user2=new User("testpassword", "testuser2", "testemail2@test.com", true, EmailLanguages.getDefaultLanguage());
        user3= new User("testpassword", "testuser3", "testemail3@test.com", true, EmailLanguages.getDefaultLanguage());
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
    }

    @Test
    public void testCreateOffering() {
        when(userDao.findById(1)).thenReturn(Optional.of(user1));
        when(userDao.findById(2)).thenReturn(Optional.empty());

        Offering offering = offeringDao.create(user1.getId(), "offeringname", OfferingCategory.VIDEO, "description", 1,2, PriceType.PER_HOUR, 1, District.getDistrictByName("Flores"), null);
        em.flush();
        assertNotNull(offering);
        assertEquals(1, offering.getId());
        assertEquals("offeringname", offering.getName());
        assertEquals(OfferingCategory.VIDEO, offering.getOfferingCategory());
        assertEquals("description", offering.getDescription());
        assertEquals(1, offering.getMinPrice(), 0.001);
        assertEquals(2, offering.getMaxPrice(), 0.001);
        assertEquals(PriceType.PER_HOUR, offering.getPriceType());
        assertEquals(1, offering.getMaxGuests());
        assertEquals(District.getDistrictByName("Flores"), offering.getDistrict());
        assertEquals(user1.getId(), offering.getOwner().getId());
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFERING_TABLE));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_TABLE, OFFERING_NAME+"= 'offeringname' AND "+OFFERING_CATEGORY+" = 'VIDEO' AND "+OFFERING_DESCRIPTION+" = 'description' AND "+OFFERING_MIN_PRICE+" = 1 AND "+OFFERING_MAX_PRICE+" = 2 AND "+OFFERING_PRICE_TYPE+" = 'PER_HOUR' AND "+OFFERING_MAX_GUESTS+" = 1 AND "+OFFERING_DISTRICT+" = 'FLORES' AND "+OFFERING_USER_ID+" = 1"));
    }

    @Test(expected = Exception.class)
    public void testCreateOfferingInvalidUserId() {
        offeringDao.create(-1, "name", OfferingCategory.VIDEO, "description", 1,2, PriceType.PER_HOUR, 1, District.getDistrictByName("Flores"), null);
    }

    @Test
    public void testUpdateOffering(){
        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Recoleta"));
        em.persist(offering1);

        offeringDao.update(offering1.getId(), "name2", "description2", 2, District.getDistrictByName("Palermo"), OfferingCategory.VENUE, 2, 3, PriceType.PER_EVENT);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFERING_TABLE));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_TABLE, OFFERING_NAME+"= 'name2'"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_TABLE, OFFERING_DESCRIPTION+"= 'description2'"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_TABLE, OFFERING_MAX_GUESTS+"= 2"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_TABLE, OFFERING_DISTRICT+"= 'PALERMO'"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_TABLE, OFFERING_CATEGORY+" = 'VENUE'"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_TABLE, OFFERING_MIN_PRICE+" = 2"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_TABLE, OFFERING_MAX_PRICE+" = 3"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_TABLE, OFFERING_PRICE_TYPE+"= 'PER_EVENT'"));
    }

    @Test(expected = OfferingNotFoundException.class)
    public void testUpdateOfferingInvalidOfferingId() {
        offeringDao.update(-1, "name2", "description2", 2, District.getDistrictByName("Palermo"), OfferingCategory.VENUE, 2, 3, PriceType.PER_EVENT);
    }

    @Test
    public void testUpdateOfferingImages(){
        Image image1 = new Image("image1".getBytes());
        Image image2 = new Image("image2".getBytes());
        Image image3 = new Image("image3".getBytes());

        when(imageDao.saveImages(any())).thenAnswer(invocation -> {
            em.persist(image1);
            em.persist(image2);
            em.persist(image3);
            return Arrays.asList(1, 2, 3);
        });
        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description",  1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Recoleta"));
        em.persist(offering1);

        List<Long> images = new ArrayList<>();
        images.add(1L);
        images.add(2L);
        images.add(3L);

        offeringDao.updateImages(offering1.getId(), images, null);

        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFERING_TABLE));
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFERING_IMAGE_TABLE));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_IMAGE_TABLE, OFFERING_IMAGE_OFFERING_ID+" = " + offering1.getId() + " AND "+OFFERING_IMAGE_IMAGE_ID+" = 1"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_IMAGE_TABLE, OFFERING_IMAGE_OFFERING_ID+" = " + offering1.getId() + " AND "+OFFERING_IMAGE_IMAGE_ID+" = 2"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_IMAGE_TABLE, OFFERING_IMAGE_OFFERING_ID+" = " + offering1.getId() + " AND "+OFFERING_IMAGE_IMAGE_ID+" = 3"));
    }

    @Test
    public void testUpdateOfferingImagesNoImages(){
        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Recoleta"));
        em.persist(offering1);
        em.flush();
        offeringDao.updateImages(offering1.getId(), null, null);

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFERING_TABLE));
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFERING_IMAGE_TABLE));
    }


    @Test
    public void testGetByIdOfferingExist() {
        // Test data
        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Recoleta"));
        em.persist(offering1);
        em.flush();
        // Test
        Optional<Offering> result = offeringDao.getById(offering1.getId());

        // Assertions
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(offering1.getId(), result.get().getId());
        assertEquals("name", result.get().getName());
        assertEquals(OfferingCategory.VIDEO, result.get().getOfferingCategory());
        assertEquals("description", result.get().getDescription());
        assertEquals(1, result.get().getMinPrice(), 0.001);
        assertEquals(2, result.get().getMaxPrice(), 0.001);
        assertEquals(PriceType.PER_HOUR, result.get().getPriceType());
        assertEquals(1, result.get().getMaxGuests());
        assertEquals(District.getDistrictByName("Recoleta"), result.get().getDistrict());

    }

    @Test
    public void testGetByIdOfferingDoesntExist() {

        Optional<Offering> maybeOffering = offeringDao.getById(1);

        assertFalse(maybeOffering.isPresent());
    }

    @Test
    public void testGetFilteredOfferings() {
        // Test data
        offering1 = new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Constitución"));
        offering2 = new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Constitución"));
        offering3 = new Offering("name", user1, OfferingCategory.PHOTOGRAPHY, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Nuñez"));

        em.persist(offering1);
        em.persist(offering2);
        em.persist(offering3);

        OfferingFilter filter = new OfferingFilter()
                .category(OfferingCategory.VIDEO)
                .districts(Collections.singletonList(District.getDistrictByName("Constitución")))
                .maxPrice(2)
                .minPrice(1)
                .attendees(1);

        // Test
        List<Offering> result = offeringDao.getFilteredOfferings(filter, 0, 10);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size());

        Set<Long> expectedOfferingIds = new HashSet<>(Arrays.asList(offering1.getId(), offering2.getId()));

        Set<Long> resultOfferingIds = result.stream().map(Offering::getId).collect(Collectors.toSet());

        assertTrue(resultOfferingIds.containsAll(expectedOfferingIds));

        assertFalse(resultOfferingIds.contains(offering3.getId()));

    }

    @Test
    public void testGetFilteredOfferingsNoOfferings() {

        // Test data
        offering1 = new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Agronomía"));
        offering2 = new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Agronomía"));
        offering3 = new Offering("name", user1, OfferingCategory.PHOTOGRAPHY, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Boedo"));

        em.persist(offering1);
        em.persist(offering2);
        em.persist(offering3);

        OfferingFilter filter = new OfferingFilter()
                .category(OfferingCategory.FLOWERS);

        // Test
        List<Offering> result = offeringDao.getFilteredOfferings(filter, 1, 10);

        // Assertions
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testGetFilteredOfferingsInvalidFilter(){


        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Agronomía"));
        offering2 = new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Agronomía"));
        offering3 = new Offering("name", user1, OfferingCategory.PHOTOGRAPHY, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Boedo"));

        em.persist(offering1);
        em.persist(offering2);
        em.persist(offering3);

        OfferingFilter filter = new OfferingFilter()
                .maxPrice(-2)
                .minPrice(123)
                .attendees(-1)
                .category(OfferingCategory.VIDEO);

        List<Offering> result = offeringDao.getFilteredOfferings(filter, 1, 10);

        assertNotNull(result);
        assertEquals(0, result.size());

    }

    @Test
    public void testGetFilteredOfferingsCount(){
        // Test data

        offering1 = new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Agronomía"));
        offering2 = new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 2, District.getDistrictByName("Agronomía"));
        offering3 = new Offering("name", user1, OfferingCategory.PHOTOGRAPHY, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Boedo"));

        em.persist(offering1);
        em.persist(offering2);
        em.persist(offering3);

        OfferingFilter filter = new OfferingFilter()
                .category(OfferingCategory.VIDEO)
                .districts(Collections.singletonList(District.getDistrictByName("Agronomía")))
                .maxPrice(3)
                .minPrice(1)
                .attendees(2);

        int result = offeringDao.getFilteredOfferingsCount(filter);

        assertEquals(1, result);


    }

    @Test
    public void testGetFilteredOfferingsCountNullFilter(){
        int result = offeringDao.getFilteredOfferingsCount(null);

        assertEquals(0, result);

    }

    @Test
    public void testSetLikeTrue(){
        // Test data

        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Recoleta"));
        em.persist(offering1);
        boolean result = offeringDao.setLike(offering1,user2,true);

        em.flush();
        assertTrue(result);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, LIKED_OFFERING_TABLE));
    }

    @Test
    public void testSetLikeTrueAlreadyLiked(){
        // Test data

        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 2, District.getDistrictByName("Recoleta"));
        em.persist(offering1);
        em.persist(new Like(user2, offering1));
        em.flush();

        boolean result = offeringDao.setLike(offering1, user2,true);

        em.flush();
        assertTrue(result);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, LIKED_OFFERING_TABLE));
    }
    @Test
    public void testSetLikeFalse(){
        // Test data

        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Recoleta"));
        offering1.setLikes(1);
        em.persist(offering1);
        em.persist(new Like(user2, offering1));
        em.flush();

        boolean result = offeringDao.setLike(offering1, user2,false);

        em.flush();
        assertFalse(result);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, LIKED_OFFERING_TABLE));

    }

    @Test
    public void setLikeFalseNotLiked(){
        // Test data

        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 2, District.getDistrictByName("Recoleta"));
        em.persist(offering1);
        em.flush();

        boolean result = offeringDao.setLike(offering1, user2,false);

        em.flush();
        assertFalse(result);
        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, LIKED_OFFERING_TABLE));
    }

    @Test
    public void testUserLikedOfferingDidLike(){
        // Test data

        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR,2, District.getDistrictByName("Recoleta"));
        em.persist(offering1);
        em.persist(new Like(user2, offering1));
        em.flush();

        boolean result = offeringDao.userLikedOffering(user2.getId(), offering1.getId());

        assertTrue(result);

    }

    @Test
    public void testUserLikedOfferingDidNotLike(){
        // Test data

        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR,2, District.getDistrictByName("Recoleta"));
        em.persist(offering1);
        em.flush();

        boolean result = offeringDao.userLikedOffering(user2.getId(), offering1.getId());

        assertFalse(result);
    }

    @Test
    public void testIsOwner(){
        // Test data
        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Recoleta"));
        em.persist(offering1);

        boolean result = offeringDao.isOwner(offering1.getId(), user1.getId());

        assertTrue(result);
    }

    @Test
    public void testIsNotOwner(){
        // Test data
        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Recoleta"));
        em.persist(offering1);

        boolean result = offeringDao.isOwner(offering1.getId(), user2.getId());

        assertFalse(result);
    }

    @Test
    public void testIsOwnerInvalidOfferingId(){
        // Test data
        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Recoleta"));
        em.persist(offering1);

        boolean result = offeringDao.isOwner(-1, user2.getId());

        assertFalse(result);
    }

    @Test
    public void testDeleteOffering(){
        // Test data
        offering1= new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 1, District.getDistrictByName("Recoleta"));
        em.persist(offering1);

        offeringDao.deleteOffering(offering1.getId());

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, OFFERING_TABLE, OFFERING_ID+"= " + offering1.getId()));
    }

    @Test(expected = Exception.class)
    public void testDeleteOfferingInvalidOfferingId(){
        offeringDao.deleteOffering(-1);

        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, OFFERING_TABLE));
    }

    @Test
    public void testGetRecommendationsForOffering(){
        // Test data
        offering1 = new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 2, District.getDistrictByName("Agronomía"));
        offering2 = new Offering("name", user2, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 2, District.getDistrictByName("Agronomía"));
        offering3 = new Offering("name", user3, OfferingCategory.PHOTOGRAPHY, "description", 1, 2, PriceType.PER_HOUR, 2, District.getDistrictByName("Boedo"));
        Offering offering4 = new Offering("name", user3, OfferingCategory.PHOTOGRAPHY, "description", 1, 2, PriceType.PER_HOUR, 2, District.getDistrictByName("Boedo"));

        em.persist(offering1);
        em.persist(offering2);
        em.persist(offering3);
        em.persist(offering4);

        Event event1=new Event(user2,"name1", "description1", Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 10, District.getDistrictByName("Recoleta"));
        Event event2=new Event(user1,"name2", "description2", Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Colegiales"));

        em.persist(event1);
        em.persist(event2);

        em.persist(new Review("content", 5, new EventOfferingRelation(event1, offering1, OfferingStatus.DONE)));
        em.persist(new Review("content", 5, new EventOfferingRelation(event1, offering3, OfferingStatus.DONE)));

        em.flush();

        List<Offering> result = offeringDao.getRecommendationsForOffering(new OfferingFilter(), 1,1, offering1.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(offering3.getId(), result.get(0).getId());
    }

    @Test
    public void testGetRecommendationsForOfferingNoOfferings(){
        // Test data
        offering1 = new Offering("name", user1, OfferingCategory.VIDEO, "description", 1, 2, PriceType.PER_HOUR, 2, District.getDistrictByName("Agronomía"));

        em.persist(offering1);

        em.flush();

        List<Offering> result = offeringDao.getRecommendationsForOffering(new OfferingFilter(), 1,1, offering1.getId());

        assertNotNull(result);
        assertEquals(0, result.size());
    }


}
