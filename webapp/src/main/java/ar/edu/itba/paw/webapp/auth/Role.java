package ar.edu.itba.paw.webapp.auth;

public enum Role {

    ROLE_USER("USER"),
    ROLE_PROVIDER("PROVIDER"),

    ONE_TIME_TOKEN("ONE_TIME_TOKEN");

    final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.getRole().equalsIgnoreCase(role)) {
                return r;
            }
        }
        return null;
    }

}
