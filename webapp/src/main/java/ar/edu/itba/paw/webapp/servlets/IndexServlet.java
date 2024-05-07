package ar.edu.itba.paw.webapp.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class IndexServlet extends HttpServlet {

    private static final List<Locale> IMPLEMENTED_LANGUAGES = Collections.unmodifiableList(Arrays.asList(Locale.ENGLISH, new Locale("es")));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String language = getMatchingLanguage(req);
        Path file = Paths.get(getServletContext().getRealPath("/static/" + language), "index.html");
        resp.setContentType("text/html");
        Files.copy(file, resp.getOutputStream());
    }

    private String getMatchingLanguage(HttpServletRequest req) {
        String fileLanguage = "en";
        String languageRange = req.getHeader("Accept-Language");
        if (languageRange == null || languageRange.isEmpty()) {
            return fileLanguage;
        }
        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(languageRange);
        Locale locale = Locale.lookup(list, IMPLEMENTED_LANGUAGES);
        if (locale == null) {
            return fileLanguage;
        }

        return locale.getLanguage();
    }
}