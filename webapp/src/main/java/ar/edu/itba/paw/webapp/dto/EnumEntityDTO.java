package ar.edu.itba.paw.webapp.dto;

public class EnumEntityDTO {

    private String name;

    private String value;

    public EnumEntityDTO() {}

    public EnumEntityDTO(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
