package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.exception.notFound.ImageNotFoundException;
import ar.edu.itba.paw.services.ImageService;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("images")
@Component
public class ImageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);


    @Autowired
    private ImageService imageService;

    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes(value = {MediaType.MULTIPART_FORM_DATA})
    public Response uploadImage(@FormDataParam("image") byte[] image) {
        final long imageId = imageService.saveImage(image);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(imageId)).build()).build();
    }


    @GET
    @Path("/{id}")
    @Produces(value = {"image/*"})
    public Response getImage(@PathParam("id") final long imageId) {
        Image image = imageService.getImage(imageId).orElseThrow(() -> new ImageNotFoundException(imageId));

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(31536000);
        cacheControl.getCacheExtension().put("immutable", "");

        return Response.ok(image.getImage()).cacheControl(cacheControl).build();
    }

}
