package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.Image;

import java.util.List;
import java.util.Optional;

public interface ImageDao {

        Image saveImage(byte[] image);

        List<Image> saveImages(byte[][] images);

        Optional<Image> getImage(long id);

        void deleteImage(long id);

        void deleteImages(List<Long> ids);
}
