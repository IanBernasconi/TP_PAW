package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Repository
public class ImageJpaDao implements ImageDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Image saveImage(byte[] image) {
        Image newImage = new Image(image);
        em.persist(newImage);
        return newImage;
    }

    @Override
    @Transactional
    public List<Image> saveImages(byte[][] images) {
        if(images == null || images.length == 0) {
            return Collections.emptyList();
        }
        List<Image> createdImages = new ArrayList<>();
        for (byte[] image : images) {
            createdImages.add(saveImage(image));
        }
        return createdImages;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Image> getImage(long id) {
        return Optional.ofNullable(em.find(Image.class, id));
    }

    @Override
    @Transactional
    public void deleteImage(long id) {
        Image image = em.find(Image.class, id);
        if(image != null) {
            em.remove(image);
        } else {
            throw new NoSuchElementException("Image with id " + id + " not found");
        }
    }

    @Override
    @Transactional
    public void deleteImages(List<Long> ids) {
        if(ids == null || ids.isEmpty()) {
            return;
        }

        for (Long id : ids) {
            deleteImage(id);
        }
    }
}
