package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;
import ar.edu.itba.paw.models.exception.VerificationException;
import ar.edu.itba.paw.models.exception.notFound.UserNotFoundException;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.tablesInformation.EventTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.OfferingTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.UserTableInfo.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
public class UserJpaDaoTest {

    private static final String NAME1 = "testuser";
    private static final String PASSWORD1 = "testpassword";
    private static final String EMAIL1 = "test@example.com";
    private static final String PHONE1 = "1234567890";
    private static final String COUNTRY1 = "UNITED STATES";
    private static final boolean isProvider = true;
    private static final String verificationToken = "token";

    private static final String NAME2 = "testuser2";
    private static final String PASSWORD2 = "testpassword2";
    private static final String EMAIL2 = "test2@example.com";
    private static final String PHONE2 = "9876543210";
    private static final String COUNTRY2 = "Argentina";

    private static final boolean notProvider = false;


    @Autowired
    private UserDao userDao;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    @Mock
    private ImageDao imageDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        jdbcTemplate = new JdbcTemplate(ds);

        em.createNativeQuery("TRUNCATE TABLE "+USER_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+OFFERING_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+EVENT_TABLE).executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE offerings_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE events_id_seq RESTART WITH 1").executeUpdate();
    }

    @Test
    @Rollback
    public void testCreate() throws DuplicateEmailException {
        when(imageDao.saveImage(any(byte[].class))).thenReturn(new Image(1, new byte[1]));

        Optional<User> userOptional = userDao.create(NAME1, PASSWORD1, EMAIL1, isProvider, EmailLanguages.getDefaultLanguage());

        em.flush();

        assertTrue(userOptional.isPresent());
        User user = userOptional.get();
        assertEquals(NAME1, user.getUsername());
        assertEquals(PASSWORD1, user.getPassword());
        assertEquals(EMAIL1, user.getEmail());
        assertEquals(isProvider, user.isProvider());
    }

    @Test(expected = DuplicateEmailException.class)
    public void testCreateDuplicateEmail() throws DuplicateEmailException {
        when(imageDao.saveImage(any(byte[].class))).thenReturn(new Image(1, new byte[1]));

        User user = new User(PASSWORD1, NAME1, EMAIL1,  isProvider, EmailLanguages.getDefaultLanguage());

        em.persist(user);

        userDao.create(NAME2, PASSWORD2, EMAIL1, notProvider, EmailLanguages.getDefaultLanguage());
    }


    @Test
    public void testUpdate(){
        User user = new User(PASSWORD1, NAME1, EMAIL1, isProvider, EmailLanguages.getDefaultLanguage());
        em.persist(user);

        userDao.update(user, NAME2,null, "description2", EmailLanguages.getDefaultLanguage());

        em.flush();

        assertEquals(NAME2, user.getUsername());
        assertEquals("description2", user.getDescription());

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USER_TABLE, USER_USERNAME+" = '" + NAME2 + "'"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USER_TABLE, USER_DESCRIPTION+" = 'description2'"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateInvalidUser(){
        User user = new User(PASSWORD1, NAME1, EMAIL1,  isProvider, EmailLanguages.getDefaultLanguage());
        em.persist(user);

        userDao.update(null, NAME2,null, "description2", EmailLanguages.getDefaultLanguage());

    }

    @Test
    public void testUpdateWithoutPicture(){
        User user = new User(PASSWORD1, NAME1, EMAIL1, isProvider, EmailLanguages.getDefaultLanguage());
        em.persist(user);

        userDao.update(user, NAME2,  "description2",  EmailLanguages.getDefaultLanguage());

        em.flush();

        assertEquals(NAME2, user.getUsername());
        assertEquals("description2", user.getDescription());

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USER_TABLE, USER_USERNAME+" = '" + NAME2 + "'"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USER_TABLE, USER_DESCRIPTION+" = 'description2'"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateNoPictureInvalidUser(){
        User user = new User(PASSWORD1, NAME1, EMAIL1,  isProvider, EmailLanguages.getDefaultLanguage());
        em.persist(user);

        userDao.update(null, NAME2,  "description2",  EmailLanguages.getDefaultLanguage());

    }


    @Test
    public void testVerifyUser()  {
        User user = new User(PASSWORD1, NAME1, EMAIL1,   isProvider, EmailLanguages.getDefaultLanguage());
        em.persist(user);
        userDao.verifyUser(user);
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, USER_TABLE, USER_VERIFIED+"= true"));
    }

    @Test(expected = UserNotFoundException.class)
    public void testVerifyUserInvalidUser() {
        User user = new User(PASSWORD1, NAME2, EMAIL2,   isProvider, EmailLanguages.getDefaultLanguage());
        userDao.verifyUser(user);
    }
    @Test
    public void testChangePassword(){
        User user = new User(PASSWORD1, NAME1, EMAIL1,   isProvider, EmailLanguages.getDefaultLanguage());
        em.persist(user);

        userDao.changePassword(EMAIL1, PASSWORD2);
        em.flush();
        assertEquals(PASSWORD2, jdbcTemplate.queryForObject("SELECT "+USER_PASSWORD+" FROM "+USER_TABLE+" WHERE "+USER_USERNAME+" = ?", new Object[]{NAME1}, String.class));
    }

    @Test(expected = UserNotFoundException.class)
    public void testChangePasswordInvalidUser(){
        userDao.changePassword(EMAIL1, PASSWORD2);
    }


    @Test
    public void testFindById(){
        User user = new User(PASSWORD1, NAME1, EMAIL1,   isProvider, EmailLanguages.getDefaultLanguage());
        em.persist(user);

        User user1 = userDao.findById(user.getId()).orElse(null);

        assertNotNull(user1);
        assertEquals(user.getId(), user1.getId());
        assertEquals(NAME1, user1.getUsername());
        assertEquals(PASSWORD1, user1.getPassword());
    }

    @Test
    public void testFindByIdInvalidId(){
        User user = userDao.findById(-1).orElse(null);

        assertNull(user);
    }

    @Test
    public void testFindByEmail(){
        User user = new User(PASSWORD1, NAME1, EMAIL1,   isProvider, EmailLanguages.getDefaultLanguage());
        em.persist(user);

        User user1 = userDao.findByEmail(EMAIL1).get();

        assertEquals(NAME1, user1.getUsername());
        assertEquals(PASSWORD1, user1.getPassword());
    }

    @Test
    public void testFindByEmailInvalidEmail(){
        User user = userDao.findByEmail("invalidEmail").orElse(null);

        assertNull(user);
    }

    @Test
    public void testGetUserOccupiedDates(){
        User user1 = new User(PASSWORD1, NAME1, EMAIL1,   isProvider, EmailLanguages.getDefaultLanguage());
        User user2 = new User(PASSWORD2, NAME2, EMAIL2,   isProvider, EmailLanguages.getDefaultLanguage());
        em.persist(user1);
        em.persist(user2);
        Offering offering = new Offering("name1", user1, OfferingCategory.PHOTOGRAPHY, "description1", 10, 20, PriceType.PER_HOUR, 10, District.getDistrictByName("Recoleta"));

        em.persist(offering);

        Event event1=new Event(user2,"name1", "description1", Date.from(LocalDate.of(2024,6,28).atStartOfDay().toInstant(ZoneOffset.UTC)), 10, District.getDistrictByName("Recoleta"));
        Event event2=new Event(user2,"name2", "description2", Date.from(LocalDate.of(2024,6,29).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Colegiales"));
        Event event3=new Event(user2,"name3", "description3", Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Flores"));

        em.persist(event1);
        em.persist(event2);
        em.persist(event3);

        EventOfferingRelation eventOfferingRelation1 = new EventOfferingRelation(event1, offering, OfferingStatus.ACCEPTED);
        EventOfferingRelation eventOfferingRelation2 = new EventOfferingRelation(event2, offering, OfferingStatus.ACCEPTED);
        EventOfferingRelation eventOfferingRelation3 = new EventOfferingRelation(event3, offering, OfferingStatus.ACCEPTED);

        em.persist(eventOfferingRelation1);
        em.persist(eventOfferingRelation2);
        em.persist(eventOfferingRelation3);

        RangeFilter rangeFilter = new RangeFilter();
        rangeFilter.start(Date.from(LocalDate.of(2024,6,25).atStartOfDay().toInstant(ZoneOffset.UTC)));
        rangeFilter.end(Date.from(LocalDate.of(2024,7,1).atStartOfDay().toInstant(ZoneOffset.UTC)));

        List<Date> occupiedDates = userDao.getUserOccupiedDates(user1, rangeFilter);

        assertEquals(3, occupiedDates.size());
        assertTrue(occupiedDates.contains(Date.from(LocalDate.of(2024,6,28).atStartOfDay().toInstant(ZoneOffset.UTC))));
        assertTrue(occupiedDates.contains(Date.from(LocalDate.of(2024,6,29).atStartOfDay().toInstant(ZoneOffset.UTC))));
        assertTrue(occupiedDates.contains(Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC))));
    }

    @Test
    public void testGetUserOccupiedDatesNoDates(){
        User user1 = new User(PASSWORD1, NAME1, EMAIL1, isProvider, EmailLanguages.getDefaultLanguage());

        List<Date> occupiedDates = userDao.getUserOccupiedDates(user1, new RangeFilter());

        assertEquals(0, occupiedDates.size());

    }



    @Test
    public void testUpdateVerificationToken(){
        User user = new User(PASSWORD1, NAME1, EMAIL1,   isProvider, EmailLanguages.getDefaultLanguage());
        em.persist(user);
        userDao.updateVerificationToken(user, "newToken");
        em.flush();
        assertEquals("newToken", jdbcTemplate.queryForObject("SELECT "+USER_VERIFICATION_TOKEN+" FROM "+USER_TABLE+" WHERE "+USER_USERNAME+" = ?", new Object[]{NAME1}, String.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateVerificationTokenInvalidUser(){

        userDao.updateVerificationToken(null, verificationToken);
    }

    @Test
    public void testUpdateResetPasswordToken(){
        User user = new User(PASSWORD1, NAME1, EMAIL1, isProvider, EmailLanguages.getDefaultLanguage());
        em.persist(user);

        userDao.updateResetPasswordToken(user, "newToken");

        em.flush();

        assertEquals("newToken", jdbcTemplate.queryForObject("SELECT "+USER_RESET_PASSWORD_TOKEN+" FROM "+USER_TABLE+" WHERE "+USER_USERNAME+" = ?", new Object[]{NAME1}, String.class));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateResetPasswordTokenInvalidUser(){
        userDao.updateResetPasswordToken(null, "newToken");
    }

    @Test
    public void testResetPassword() throws VerificationException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        jdbcTemplate.execute("INSERT INTO "+USER_TABLE+" ("+USER_ID + ", "+USER_USERNAME+", "+USER_PASSWORD+", "+USER_EMAIL+", "+USER_IS_PROVIDER+", "+USER_VERIFICATION_TOKEN+", "+ USER_VERIFICATION_TOKEN_CREATED_AT + ", "+USER_REGISTERED+", "+USER_CREATED_AT+", "+USER_VERIFIED+", "+USER_DESCRIPTION+", "+USER_RESET_PASSWORD_TOKEN+", "+USER_RESET_PASSWORD_TOKEN_CREATED_AT+") VALUES " +
            "(nextval('users_id_seq'), '"+NAME1+"', '"+PASSWORD1+"', '"+EMAIL1+"', "+isProvider+", '"+verificationToken+"' , '" + dateFormat.format(new Date()) + "', true, '2023-10-02 22:38:41.725212', false, 'description', 'token', '" + dateFormat.format(new Date()) + "')");

        userDao.resetPassword(1, "newPassword");

        em.flush();

        assertEquals("newPassword", jdbcTemplate.queryForObject("SELECT "+USER_PASSWORD+" FROM "+USER_TABLE+" WHERE "+USER_USERNAME+" = ?", new Object[]{NAME1}, String.class));
    }

    @Test
    public void testSetProviderTrue(){
        User user = new User(PASSWORD1, NAME1, EMAIL1, false, EmailLanguages.getDefaultLanguage());
        em.persist(user);

        userDao.setProvider(user.getId(),true);
        em.flush();

        assertEquals(true, jdbcTemplate.queryForObject("SELECT "+USER_IS_PROVIDER+" FROM "+USER_TABLE+" WHERE "+USER_USERNAME+" = ?", new Object[]{NAME1}, Boolean.class));
    }

    @Test
    public void testSetProviderFalse(){
        User user = new User(PASSWORD1, NAME1, EMAIL1, true, EmailLanguages.getDefaultLanguage());
        em.persist(user);

        userDao.setProvider(user.getId(),false);
        em.flush();

        assertEquals(false, jdbcTemplate.queryForObject("SELECT "+USER_IS_PROVIDER+" FROM "+USER_TABLE+" WHERE "+USER_USERNAME+" = ?", new Object[]{NAME1}, Boolean.class));
    }


}
