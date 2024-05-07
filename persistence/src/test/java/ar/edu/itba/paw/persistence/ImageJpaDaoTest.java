package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
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

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.persistence.tablesInformation.ImageTableInfo.*;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
public class ImageJpaDaoTest {

    @Autowired
    private ImageDao imageDao;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testSaveImage(){
        byte[] image = new byte[1];
        image[0] = 1;

        long id = imageDao.saveImage(image).getId();
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, IMAGE_TABLE, IMAGE_ID+"= " + id));
    }

    @Test
    public void testSaveImages(){
        byte[] image1 = new byte[1];
        image1[0] = 1;
        byte[] image2 = new byte[1];
        image2[0] = 2;
        byte[] image3 = new byte[1];
        image3[0] = 3;

        byte[][] images = new byte[3][1];
        images[0] = image1;
        images[1] = image2;
        images[2] = image3;

        List<Long> ids = imageDao.saveImages(images).stream().map(Image::getId).collect(java.util.stream.Collectors.toList());
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, IMAGE_TABLE, IMAGE_ID+" = " + ids.get(0)));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, IMAGE_TABLE, IMAGE_ID+" = " + ids.get(1)));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, IMAGE_TABLE, IMAGE_ID+" = " + ids.get(2)));
    }

    @Test
    public void testSaveNullImages(){
        List<Long> ids = imageDao.saveImages(null).stream().map(Image::getId).collect(java.util.stream.Collectors.toList());

        assertEquals(0, ids.size());
    }

    @Test
    public void testSaveEmptyImages(){
        byte[][] images = new byte[0][0];

        List<Long> ids = imageDao.saveImages(images).stream().map(Image::getId).collect(Collectors.toList());

        assertEquals(0, ids.size());
    }

    @Test
    public void testGetImage(){
        byte[] imageBytes = new byte[1];
        imageBytes[0] = 1;

        Image image = new Image(imageBytes);

        em.persist(image);

        Optional<Image> result = imageDao.getImage(image.getId());

        assertTrue(result.isPresent());
        assertEquals(imageBytes[0], result.get().getImage()[0]);
    }

    @Test
    public void testGetNonExistentImage(){
        Image result = imageDao.getImage(1).orElse(null);

        assertNull(result);
    }

    @Test
    public void testDeleteImage(){
        byte[] imageBytes = new byte[1];
        imageBytes[0] = 1;

        Image image = new Image(imageBytes);

        em.persist(image);
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, IMAGE_TABLE));

        imageDao.deleteImage(image.getId());
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, IMAGE_TABLE));
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeleteNonExistentImage(){
        imageDao.deleteImage(1);
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeleteImageWithInvalidId(){
        imageDao.deleteImage(-1);
    }

    @Test
    public void testDeleteImages(){
        byte[] image1Bytes = new byte[1];
        image1Bytes[0] = 1;
        byte[] image2Bytes = new byte[1];
        image2Bytes[0] = 2;
        byte[] image3Bytes = new byte[1];
        image3Bytes[0] = 3;

        byte[][] imagesBytes = new byte[3][1];
        imagesBytes[0] = image1Bytes;
        imagesBytes[1] = image2Bytes;
        imagesBytes[2] = image3Bytes;

        Image image1 = new Image(image1Bytes);
        Image image2 = new Image(image2Bytes);
        Image image3 = new Image(image3Bytes);

        em.persist(image1);
        em.persist(image2);
        em.persist(image3);
        em.flush();
        assertEquals(3, JdbcTestUtils.countRowsInTable(jdbcTemplate, IMAGE_TABLE));

        imageDao.deleteImages(Arrays.asList(image1.getId(), image2.getId(), image3.getId()));
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, IMAGE_TABLE, IMAGE_ID+"= " + 1));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, IMAGE_TABLE, IMAGE_ID+"= " + 2));
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, IMAGE_TABLE, IMAGE_ID+"= " + 3));
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeleteImagesNoImages(){
        imageDao.deleteImages(Arrays.asList(1L, 2L, 3L));
    }
}
