package ar.edu.itba.paw.models;

import java.util.Date;

public class RangeFilter {

    private Date start = null;
    private Date end = null;

    public RangeFilter start(Date start) {
        this.start = start;
        return this;
    }

    public RangeFilter end(Date end) {
        this.end = end;
        return this;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }


}
