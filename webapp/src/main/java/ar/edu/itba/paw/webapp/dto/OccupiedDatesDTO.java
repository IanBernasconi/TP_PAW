package ar.edu.itba.paw.webapp.dto;

import java.util.Date;
import java.util.List;

public class OccupiedDatesDTO {

    private List<Date> occupiedDates;

    public OccupiedDatesDTO() {
    }

    public OccupiedDatesDTO(List<Date> occupiedDates) {
        this.occupiedDates = occupiedDates;
    }

    public List<Date> getOccupiedDates() {
        return occupiedDates;
    }

    public void setOccupiedDates(List<Date> occupiedDates) {
        this.occupiedDates = occupiedDates;
    }

}
