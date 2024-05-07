package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.validation.UriValidation;

import java.net.URI;

public class ImageDTO implements UriValidation {

    private URI self;

    public ImageDTO() {}

    public static ImageDTO fromImage(final URI uri) {
        final ImageDTO dto = new ImageDTO();
        dto.self = uri;
        return dto;
    }

    @Override
    public boolean isValidUri(URI uri) {
        return isValidUri(uri, "images");
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public static Long getImageId(final URI image) {
        if (image == null || image.toString().isEmpty()) {
            return null;
        }
        final String[] parts = image.toString().split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }
}
