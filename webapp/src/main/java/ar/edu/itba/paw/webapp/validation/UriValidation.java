package ar.edu.itba.paw.webapp.validation;

import java.net.URI;

public interface UriValidation {

    boolean isValidUri(final URI uri);

    default boolean isValidUri(URI uri, String pathRegex) {
        if (uri == null || uri.equals(URI.create(""))) {
            return true;
        }
        final String regex = ".*/api/" + pathRegex + "/(\\d+)$";
        return uri.getPath().matches(regex);
    }

}
