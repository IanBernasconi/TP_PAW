package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageService {

    long saveImage(byte[] image);

    Optional<Image> getImage(long id);

}
