package ar.edu.itba.paw.models.exception.notFound;

public abstract class EntityNotFoundException extends RuntimeException {

    private final String entityName;
    private final Long entityId;

    protected EntityNotFoundException(String entityName, Long entityId) {
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getEntityName() {
        return entityName;
    }
}
