package ar.edu.itba.paw.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum District {
    ALL("All", "All", "All"),
    NOT_YET_DEFINED("Not Yet Defined", "Not Yet Defined", "Not Yet Defined"),
    AGRONOMIA("Agronomía", "CABA", "Argentina"),
    ALMAGRO("Almagro", "CABA", "Argentina"),
    BALVANERA("Balvanera", "CABA", "Argentina"),
    BARRACAS("Barracas", "CABA", "Argentina"),
    BELGRANO("Belgrano", "CABA", "Argentina"),
    BOEDO("Boedo", "CABA", "Argentina"),
    CABALLITO("Caballito", "CABA", "Argentina"),
    CHACARITA("Chacarita", "CABA", "Argentina"),
    COGHLAN("Coghlan", "CABA", "Argentina"),
    COLEGIALES("Colegiales", "CABA", "Argentina"),
    CONSTITUCION("Constitución", "CABA", "Argentina"),
    FLORES("Flores", "CABA", "Argentina"),
    FLORESTA("Floresta", "CABA", "Argentina"),
    LA_BOCA("La Boca", "CABA", "Argentina"),
    LA_PATERNAL("La Paternal", "CABA", "Argentina"),
    LINIERS("Liniers", "CABA", "Argentina"),
    MATADEROS("Mataderos", "CABA", "Argentina"),
    MONTE_CASTRO("Monte Castro", "CABA", "Argentina"),
    MONSERRAT("Monserrat", "CABA", "Argentina"),
    NUEVA_POMPEYA("Nueva Pompeya", "CABA", "Argentina"),
    NUNEZ("Núñez", "CABA", "Argentina"),
    PALERMO("Palermo", "CABA", "Argentina"),
    PARQUE_AVELLANEDA("Parque Avellaneda", "CABA", "Argentina"),
    PARQUE_CHACABUCO("Parque Chacabuco", "CABA", "Argentina"),
    PARQUE_CHAS("Parque Chas", "CABA", "Argentina"),
    PARQUE_PATRICIOS("Parque Patricios", "CABA", "Argentina"),
    PUERTO_MADERO("Puerto Madero", "CABA", "Argentina"),
    RECOLETA("Recoleta", "CABA", "Argentina"),
    RETIRO("Retiro", "CABA", "Argentina"),
    SAAVEDRA("Saavedra", "CABA", "Argentina"),
    SAN_CRISTOBAL("San Cristóbal", "CABA", "Argentina"),
    SAN_NICOLAS("San Nicolás", "CABA", "Argentina"),
    SAN_TELMO("San Telmo", "CABA", "Argentina"),
    VERSALLES("Versalles", "CABA", "Argentina"),
    VILLA_CRESPO("Villa Crespo", "CABA", "Argentina"),
    VILLA_DEVOTO("Villa Devoto", "CABA", "Argentina"),
    VILLA_GENERAL_MITRE("Villa General Mitre", "CABA", "Argentina"),
    VILLA_LUGANO("Villa Lugano", "CABA", "Argentina"),
    VILLA_LURO("Villa Luro", "CABA", "Argentina"),
    VILLA_ORTUZAR("Villa Ortúzar", "CABA", "Argentina"),
    VILLA_PUEYRREDON("Villa Pueyrredón", "CABA", "Argentina"),
    VILLA_REAL("Villa Real", "CABA", "Argentina"),
    VILLA_RIACHUELO("Villa Riachuelo", "CABA", "Argentina"),
    VILLA_SANTA_RITA("Villa Santa Rita", "CABA", "Argentina"),
    VILLA_SOLDATI("Villa Soldati", "CABA", "Argentina"),
    VILLA_URQUIZA("Villa Urquiza", "CABA", "Argentina"),
    VILLA_DEL_PARQUE("Villa del Parque", "CABA", "Argentina"),
    VELEZ_SARSFIELD("Vélez Sársfield", "CABA", "Argentina"),
    NOT_SPECIFIED("Not Specified", "Not Specified", "Not Specified");

    private final String name;
    private final String city;
    private final String country;

    District(String name, String city, String country) {
        this.name = name;
        this.city = city;
        this.country = country;
    }

    public String getName() {
        return WordUtils.capitalizeFully(name);
    }

    public boolean isAll() {
        return name.equalsIgnoreCase("all");
    }

    public String getCity() {
        return WordUtils.capitalizeFully(city);
    }

    public String getCountry() {
        return WordUtils.capitalizeFully(country);
    }

    public static District getDistrictByName(String name) {
        if (name == null) {
            return NOT_SPECIFIED;
        }

        if (name.equalsIgnoreCase("all")) {
            return ALL;
        }
        for (District district : District.values()) {
            if (StringUtils.stripAccents(district.toString()).equalsIgnoreCase(StringUtils.stripAccents(name))) {
                return district;
            }
        }

        return NOT_SPECIFIED;
    }

    public static List<District> getDistrictsByCity(String city) {
        List<District> districts = new ArrayList<>();
        for (District district : District.values()) {
            if (district.city.equalsIgnoreCase(city)) {
                districts.add(district);
            }
        }
        return districts;
    }

    public static List<District> getDistrictsByCountry(String country) {
        List<District> districts = new ArrayList<>();
        for (District district : District.values()) {
            if (district.country.equalsIgnoreCase(country)) {
                districts.add(district);
            }
        }
        return districts;
    }

    public static List<District> getDistricts() {
        return Arrays.asList(Arrays.stream(District.values()).filter(district -> !district.equals(NOT_SPECIFIED) && !district.equals(NOT_YET_DEFINED)).toArray(District[]::new));
    }

}
