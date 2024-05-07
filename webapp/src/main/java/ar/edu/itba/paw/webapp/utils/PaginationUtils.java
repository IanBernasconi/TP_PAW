package ar.edu.itba.paw.webapp.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public class PaginationUtils {

    public static final int MAX_PAGE_SIZE = 100;
    public static final int DEFAULT_PAGE_SIZE = 20;

    public static final String DEFAULT_PAGE_SIZE_STRING = "20";

    public static class PaginationInfo {
        private final int page;
        private final int pageCount;
        private final int pageSize;
        private final int itemCount;

        public PaginationInfo(int page, int pageCount, int pageSize, int itemCount) {
            this.page = page;
            this.pageCount = pageCount;
            this.pageSize = pageSize;
            this.itemCount = itemCount;
        }

        public int getPage() {
            return page;
        }

        public int getPageCount() {
            return pageCount;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getItemCount() {
            return itemCount;
        }
    }

    public static PaginationInfo calculatePage(int page, int pageSize, int itemCount) {
        if (page < 0) {
            page = 0;
        }
        if (pageSize < 1) {
            pageSize = 1;
        } else if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        int pageCount = (int) Math.ceil((double) itemCount / pageSize);
        if (page > pageCount) {
            page = pageCount;
        }
        return new PaginationInfo(page, pageCount, pageSize, itemCount);
    }

    public static void  addLinks(Response.ResponseBuilder responseBuilder, PaginationInfo paginationInfo, UriInfo uriInfo) {
        addLink(responseBuilder, "first", 0, uriInfo);

        if (paginationInfo.getPage() > 0) {
            addLink(responseBuilder, "prev", paginationInfo.getPage() - 1, uriInfo);
        }

        if (paginationInfo.getPage() < paginationInfo.getPageCount() - 1) {
            addLink(responseBuilder, "next", paginationInfo.getPage() + 1, uriInfo);
        }

        addLink(responseBuilder, "last", paginationInfo.getPageCount() - 1, uriInfo);
    }

    private static void addLink(Response.ResponseBuilder responseBuilder, String rel, int page, UriInfo uriInfo) {
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().queryParam("page", page);
        uriInfo.getQueryParameters().forEach((k, v) -> {
                    if (!k.equals("page")) {
                        uriBuilder.queryParam(k, v.toArray());
                    }});
        responseBuilder.link(uriBuilder.build(), rel);
    }

    private PaginationUtils() {
    }

}
