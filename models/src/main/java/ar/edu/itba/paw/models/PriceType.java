package ar.edu.itba.paw.models;

public enum PriceType {

    PER_EVENT("Per event"),
    PER_PERSON("Per person"),
    PER_DAY("Per day"),
    PER_ITEM("Per item"),
    PER_HOUR("Per hour"),
    OTHER("Other");

    private final String name;

    PriceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PriceType fromString(String name) {
        for (PriceType priceType : PriceType.values()) {
            if (priceType.toString().equalsIgnoreCase(name)) {
                return priceType;
            }
        }
        return null;
    }

}
