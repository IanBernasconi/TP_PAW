package ar.edu.itba.paw.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmailLanguages {

    public static final List<EmailLanguage> LANGUAGES = populateLanguages();

    public static final List<EmailLanguage> IMPLEMENTED_LANGUAGES = new ArrayList<EmailLanguage>() {{
        add(new EmailLanguage("en"));
        add(new EmailLanguage("es"));
    }};

    private EmailLanguages() {}

    private static List<EmailLanguage> populateLanguages() {
        String[] languages = Locale.getISOLanguages();
        List<EmailLanguage> allLanguages = new ArrayList<>();
        for(String language : languages) {
            allLanguages.add(new EmailLanguage(language));
        }
        return allLanguages;
    }

    public static EmailLanguage fromString(String language) {
        return LANGUAGES.stream().filter(l -> l.getLanguage().equals(language.substring(0,2))).findFirst().orElse(IMPLEMENTED_LANGUAGES.get(0));
    }

    public static boolean isImplemented(String language) {
        if (language == null || language.length() < 2) {
            return false;
        }
        return IMPLEMENTED_LANGUAGES.stream().anyMatch(l -> l.getLanguage().equals(language.substring(0, 2)));
    }

    public static EmailLanguage getDefaultLanguage() {
        return new EmailLanguage("en");
    }

}
