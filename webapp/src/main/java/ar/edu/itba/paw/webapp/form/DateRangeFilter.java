package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.RangeFilter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.ws.rs.QueryParam;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateRangeFilter {

    @QueryParam("from")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String start;

    @QueryParam("to")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String end;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public RangeFilter addRange(RangeFilter filter) throws DateTimeParseException {
        if (start != null) {
            ZonedDateTime startLocalDateTime = ZonedDateTime.parse(start);
            filter.start(new Date(startLocalDateTime.toInstant().toEpochMilli()));
        }

        if (end != null) {
            ZonedDateTime endLocalDateTime = ZonedDateTime.parse(end);
            filter.end(new Date(endLocalDateTime.toInstant().toEpochMilli()));
        }
        return filter;
    }
}
