package ar.edu.itba.paw.persistence;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UtilitiesFunctions {

    private UtilitiesFunctions() {}

    @SuppressWarnings("unchecked")
    public static List<Long> getIdsFromNativeQuery(Query nativeQuery) {
        return ((List<Object>) nativeQuery.getResultList()).stream()
                .map(Object::toString)
                .map(Long::valueOf).collect(Collectors.toList());
    }

    public static List<Long> getEntitiesIdListByQuery(EntityManager em, String query, Map<String, Object> params, int page, int pageSize) {
        final Query nativeQuery = em.createNativeQuery(query);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult(page * pageSize);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            nativeQuery.setParameter(entry.getKey(), entry.getValue());
        }

        return UtilitiesFunctions.getIdsFromNativeQuery(nativeQuery);
    }



}
