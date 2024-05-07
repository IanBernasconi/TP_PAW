package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.persistence.ImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageDao imageDao;
    @Override
    public long saveImage(byte[] image) {
        return imageDao.saveImage(image).getId();
    }

    @Override
    public Optional<Image> getImage(long id) {
        return imageDao.getImage(id);
    }

//    @Override
//    public void deleteImage(long id) {
//        imageDao.deleteImage(id);
//    }
}
