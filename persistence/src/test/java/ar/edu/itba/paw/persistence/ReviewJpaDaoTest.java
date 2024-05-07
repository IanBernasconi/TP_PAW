package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.EmailLanguages;
import ar.edu.itba.paw.models.PriceType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.eventOfferingRelation.Review;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static ar.edu.itba.paw.persistence.tablesInformation.EventOfferingTableInfo.EVENT_OFFERING_TABLE;
import static ar.edu.itba.paw.persistence.tablesInformation.OfferingTableInfo.OFFERING_TABLE;
import static ar.edu.itba.paw.persistence.tablesInformation.EventTableInfo.EVENT_TABLE;
import static ar.edu.itba.paw.persistence.tablesInformation.ReviewTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.UserTableInfo.USER_TABLE;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
public class ReviewJpaDaoTest {

    @Autowired
    private ReviewDao reviewDao;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;


    private Offering offering1;

    private EventOfferingRelation eventOfferingRelation1;
    private EventOfferingRelation eventOfferingRelation2;
    private EventOfferingRelation eventOfferingRelation3;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);

        em.createNativeQuery("TRUNCATE TABLE "+USER_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+EVENT_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+OFFERING_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+EVENT_OFFERING_TABLE).executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE events_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE offerings_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE events_offerings_relation_id_seq RESTART WITH 1").executeUpdate();

        User user1 = new User("testpassword", "testuser1", "testemail1@test.com", true, EmailLanguages.getDefaultLanguage());
        User user2 = new User("testpassword", "testuser2", "testemail2@test.com", true, EmailLanguages.getDefaultLanguage());
        Event event1 = new Event(user2, "name1", "description1", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 10, District.getDistrictByName("Recoleta"));
        Event event2 = new Event(user1, "name2", "description2", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Flores"));
        offering1 = new Offering("name1", user1, OfferingCategory.PHOTOGRAPHY, "description1", 10, 20, PriceType.PER_HOUR, 10, District.getDistrictByName("Recoleta"));
        Offering offering2 = new Offering("name2", user2, OfferingCategory.PHOTOGRAPHY, "description1", 10, 20, PriceType.PER_HOUR, 10, District.getDistrictByName("Flores"));
        eventOfferingRelation1 = new EventOfferingRelation(event1, offering1, OfferingStatus.PENDING);
        eventOfferingRelation2 = new EventOfferingRelation(event2, offering1, OfferingStatus.PENDING);
        eventOfferingRelation3 = new EventOfferingRelation(event1, offering2, OfferingStatus.PENDING);


        em.persist(user1);
        em.persist(user2);
        em.persist(event1);
        em.persist(event2);
        em.persist(offering1);
        em.persist(offering2);
        em.persist(eventOfferingRelation1);
        em.persist(eventOfferingRelation2);
        em.persist(eventOfferingRelation3);


    }

    @Test
    public void testCreateReview(){
        Review review = reviewDao.createReview(1, 5, "content");
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, REVIEW_TABLE, REVIEW_RELATION_ID+" = "+review.getRelationId()+" AND "+REVIEW_RATING+" = 5 AND "+REVIEW_CONTENT+" = 'content'"));
    }

    @Test(expected = Exception.class)
    public void testCreateReviewInvalidRelationId(){
        reviewDao.createReview(-1,  5, "content");
    }

    @Test
    public void testGetReviewsByOfferingId(){
        Review review1 = new Review("content1", 5, eventOfferingRelation1);
        Review review2 = new Review("content2", 5, eventOfferingRelation2);

        em.persist(review1);
        em.persist(review2);

        List<Review> reviews = reviewDao.getReviews(null, offering1.getId(), 0, 2);

        assertEquals(2, reviews.size());
        assertEquals(1, reviews.get(0).getRelation().getRelationId());
        assertEquals(2, reviews.get(1).getRelation().getRelationId());
        assertEquals("content1", reviews.get(0).getReview());
        assertEquals("content2", reviews.get(1).getReview());
    }


    @Test
    public void testGetReviewsByOfferingIdInvalidId() {
        Review review1 = new Review("content1", 5, eventOfferingRelation1);
        Review review2 = new Review("content2", 5, eventOfferingRelation2);

        em.persist(review1);
        em.persist(review2);

        List<Review> reviews = reviewDao.getReviews(null,-1L, 1, 2);

        assertEquals(0, reviews.size());
    }

    @Test
    public void testGetReviewsCountByOfferingId(){
        Review review1 = new Review("content1", 5, eventOfferingRelation1);
        Review review2 = new Review("content2", 5, eventOfferingRelation2);
        Review review3 = new Review("content3", 5, eventOfferingRelation3);

        em.persist(review1);
        em.persist(review2);
        em.persist(review3);

        int count = reviewDao.getReviewsCount(null,2L);
        assertEquals(1, count);
    }

    @Test
    public void testGetReviewsCountByOfferingIdInvalidId(){
        Review review1 = new Review("content1", 5, eventOfferingRelation1);
        Review review2 = new Review("content2", 5, eventOfferingRelation2);
        Review review3 = new Review("content3", 5, eventOfferingRelation3);

        em.persist(review1);
        em.persist(review2);
        em.persist(review3);

        int count = reviewDao.getReviewsCount(null,-1L);

        assertEquals(0, count);
    }

    @Test
    public void testGetReviewById(){
        Review review1 = new Review("content1", 5, eventOfferingRelation1);
        em.persist(review1);

        Optional<Review> review = reviewDao.getReviewById(review1.getRelationId());

        assertTrue(review.isPresent());
        assertEquals(review1.getRelationId(), review.get().getRelationId());
        assertEquals(review1.getReview(), review.get().getReview());
        assertEquals(review1.getRating(), review.get().getRating());
    }

    @Test
    public void testGetReviewByIdInvalidId(){
        Optional<Review> review = reviewDao.getReviewById(-1);

        assertFalse(review.isPresent());
    }
}
