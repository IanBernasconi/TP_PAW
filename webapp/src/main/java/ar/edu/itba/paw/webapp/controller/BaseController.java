package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.PriceType;
import ar.edu.itba.paw.models.offering.OfferingCategory;
import ar.edu.itba.paw.webapp.dto.EnumEntityDTO;
import ar.edu.itba.paw.webapp.mediaType.VndType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Path("/")
@Component
public class BaseController {


    @Autowired
    private MessageSource messageSource;

    @Autowired
    private HttpServletRequest request;


    @GET
    @Path("/categories")
    @Produces(value = {VndType.APPLICATION_OFFERING_CATEGORIES})
    public Response getCategories() {
        final List<OfferingCategory> categories = Arrays.asList(OfferingCategory.values());
        final List<EnumEntityDTO> categoriesDTOs = categories.stream().map(category -> new EnumEntityDTO(category.toString(),
                messageSource.getMessage("offering.category." + category, null, request.getLocale()))).collect(Collectors.toList());

        return generateEnumResponse(categoriesDTOs);
    }

    @GET
    @Path("/priceTypes")
    @Produces(value = {VndType.APPLICATION_OFFERING_PRICE_TYPES})
    public Response getPriceTypes() {
        final List<PriceType> priceTypes = Arrays.asList(PriceType.values());
        final List<EnumEntityDTO> priceTypesDTOs = priceTypes.stream().map(priceType -> new EnumEntityDTO(priceType.toString(),
                messageSource.getMessage("offering.priceType." + priceType, null, request.getLocale()))).collect(Collectors.toList());

        return generateEnumResponse(priceTypesDTOs);
    }

    @GET
    @Path("/districts")
    @Produces(value = {VndType.APPLICATION_DISTRICTS})
    public Response getDistricts() {
        final List<String> districts = Arrays.stream(District.values()).map(District::toString).collect(Collectors.toList());
        final List<EnumEntityDTO> districtsDTOs = districts.stream().map(district -> new EnumEntityDTO(district,
                messageSource.getMessage("district." + district, null, request.getLocale()))).collect(Collectors.toList());

        return generateEnumResponse(districtsDTOs);
    }

    private Response generateEnumResponse(List<EnumEntityDTO> enumEntityDTOs) {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(60 * 60 * 24); // 1 day

        return Response.ok(new GenericEntity<List<EnumEntityDTO>>(enumEntityDTOs) {
        }).cacheControl(cacheControl).header("Vary", "Accept-Language").build();
    }


}
