package ar.edu.itba.paw.models.offering;

import ar.edu.itba.paw.models.PriceType;

import java.util.Arrays;
import java.util.List;

public enum OfferingCategory {

    ALL("all", PriceType.OTHER),
    PHOTOGRAPHY("photography", PriceType.PER_HOUR),
    VIDEO("video", PriceType.PER_HOUR),
    MUSIC("music", PriceType.PER_HOUR),
    CATERING("catering", PriceType.PER_PERSON),
    DECORATION("decoration", PriceType.PER_EVENT),
    FLOWERS("flowers", PriceType.PER_EVENT),
    VENUE("venue", PriceType.PER_EVENT),
    OTHER("other", PriceType.OTHER);

    private final String name;
    private final PriceType priceType;

    OfferingCategory(String name, PriceType priceType) {
        this.name = name;
        this.priceType = priceType;
    }

    public String getName() {
        return name;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public static OfferingCategory fromString(String text) {
        for (OfferingCategory b : OfferingCategory.values()) {
            if (b.name.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }


    public static List<OfferingCategory> getCategories(){
        return Arrays.asList(Arrays.stream(OfferingCategory.values()).filter(category -> !category.equals(ALL)).toArray(OfferingCategory[]::new));
    }
}
