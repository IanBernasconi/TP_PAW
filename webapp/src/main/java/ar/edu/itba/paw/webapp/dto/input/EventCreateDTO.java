package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.validation.SpecifiedDistrict;
import ar.edu.itba.paw.webapp.validation.UriValid;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.Date;

public class EventCreateDTO {

    @NotNull
    @NotEmpty
    @Size(min = 4, max = 50)
    private String name;

    private String description;


    @NotNull(message = "{NotNull.EventDTO.date}")
    @Future
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date date;

    @NotNull
    @Min(1)
    private int numberOfGuests;

    @NotNull
    @SpecifiedDistrict
    private District district;

    @NotNull
    @UriValid(type = UserDTO.class)
    private URI owner;


    public EventCreateDTO() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public URI getOwner() {
        return owner;
    }

    public void setOwner(URI owner) {
        this.owner = owner;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

}
