package ar.edu.itba.paw.webapp.config;

import org.glassfish.jersey.server.validation.ValidationConfig;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.util.Locale;

@Provider
public class ValidationContextResolver implements ContextResolver<ValidationConfig> {

    @Override
    public ValidationConfig getContext(Class<?> type) {
        final ValidationConfig config = new ValidationConfig();
        config.messageInterpolator(new LocalizedMessageInterpolator());
        return config;
    }

    private static class LocalizedMessageInterpolator implements MessageInterpolator {

        private final MessageInterpolator defaultInterpolator;

        public LocalizedMessageInterpolator() {
            defaultInterpolator = Validation.byDefaultProvider().configure().getDefaultMessageInterpolator();
        }

        @Override
        public String interpolate(String messageTemplate, Context context) {
            return defaultInterpolator.interpolate(messageTemplate, context, LocaleContextHolder.getLocale());
        }

        @Override
        public String interpolate(String messageTemplate, Context context, Locale locale) {
            return defaultInterpolator.interpolate(messageTemplate, context, LocaleContextHolder.getLocale());
        }
    }
}