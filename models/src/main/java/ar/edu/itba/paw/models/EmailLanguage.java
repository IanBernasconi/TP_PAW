package ar.edu.itba.paw.models;

import java.util.Objects;

public class EmailLanguage {

    private final String language;

    public EmailLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailLanguage that = (EmailLanguage) o;
        return Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(language);
    }
}
