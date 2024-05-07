package ar.edu.itba.paw.models.eventOfferingRelation;

public enum OfferingStatus {

    NEW,
    ACCEPTED,
    PENDING,
    REJECTED,
    DONE;

    public static OfferingStatus fromString(String status) {
        switch (status) {
            case "ACCEPTED":
                return ACCEPTED;
            case "PENDING":
                return PENDING;
            case "REJECTED":
                return REJECTED;
            case "DONE":
                return DONE;
            case "NEW":
                return NEW;
            default:
                return null;
        }
    }


}
