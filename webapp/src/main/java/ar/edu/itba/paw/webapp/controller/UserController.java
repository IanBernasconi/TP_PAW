package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.EmailLanguages;
import ar.edu.itba.paw.models.RangeFilter;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;
import ar.edu.itba.paw.models.exception.notFound.UserNotFoundException;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.auth.JwtUtils;
import ar.edu.itba.paw.webapp.dto.ImageDTO;
import ar.edu.itba.paw.webapp.dto.OccupiedDatesDTO;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.dto.input.UserResetPasswordDTO;
import ar.edu.itba.paw.webapp.dto.input.UserSetProviderDTO;
import ar.edu.itba.paw.webapp.dto.input.UserUpdatePasswordDTO;
import ar.edu.itba.paw.webapp.dto.input.UserVerifyDTO;
import ar.edu.itba.paw.webapp.exception.httpExceptions.HttpBadRequestException;
import ar.edu.itba.paw.webapp.exception.httpExceptions.ServerErrorException;
import ar.edu.itba.paw.webapp.exception.httpExceptions.user.UserEmailAlreadyExistsException;
import ar.edu.itba.paw.webapp.form.DateRangeFilter;
import ar.edu.itba.paw.webapp.mediaType.VndType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@Path("users")
@Component
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private Environment env;


    @GET
    @Produces(value = {VndType.APPLICATION_USERS})
    public Response getUserByEmail(@QueryParam("email") final String email) {
        if (email == null || email.isEmpty()) {
            throw new HttpBadRequestException("exception.user.requiredEmail");
        }
        try {
            final Optional<User> maybeUser = userService.findByEmail(email);
            if (!maybeUser.isPresent()) {
                return Response.ok(new GenericEntity<List<UserDTO>>(Collections.emptyList()) {
                }).build();
            }
            return Response.ok(new GenericEntity<List<UserDTO>>(Collections.singletonList(UserDTO.fromUser(maybeUser.get(), uriInfo))) {
            }).build();
        } catch (final IllegalArgumentException e) {
            throw new HttpBadRequestException("exception.user.invalidEmail");
        }
    }

    @POST
    @Consumes(value = {VndType.APPLICATION_USER})
    @Transactional
    public Response createUser(@Valid final UserDTO userDto) {
        try {
            User user = userService.createUser(userDto.getName(), userDto.getPassword(), userDto.getEmail(), userDto.isProvider(), userDto.getLanguage())
                    .orElseThrow(() -> new ServerErrorException("exception.user.create"));

            String token = jwtUtils.generateOneTimeToken(user.getEmail(), UserDTO.getSelfUri(user.getId(), uriInfo).toString());

            String seeDetailsUrl = env.getProperty("base_path") + "user/verify?token=" + token;
            userService.setUserVerificationToken(user, seeDetailsUrl, token);

            return Response.created(uriInfo.getBaseUriBuilder().path("user").path(String.valueOf(user.getId())).build()).build();
        } catch (final DuplicateEmailException e) {
            throw new UserEmailAlreadyExistsException(userDto.getEmail());
        }
    }

    @GET
    @Path("/{id}")
    @Produces(value = {VndType.APPLICATION_USER})
    public Response getById(@PathParam("id") final long id) {
        final User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return Response.ok(UserDTO.fromUser(user, uriInfo)).build();
    }

    @GET
    @Path("/{id}/occupiedDates")
    @Produces(value = {VndType.APPLICATION_USER_OCCUPIED_DATES})
    public Response getOccupiedDates(@PathParam("id") final long id, @BeanParam final DateRangeFilter dateRangeFilter) {
        final User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        final RangeFilter rangeFilter = new RangeFilter();
        dateRangeFilter.addRange(rangeFilter);
        final OccupiedDatesDTO occupiedDatesDTO = new OccupiedDatesDTO(userService.getUserOccupiedDates(user, rangeFilter));
        return Response.ok(occupiedDatesDTO).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(value = {VndType.APPLICATION_USER})
    public Response updateUser(@PathParam("id") final long id, @Valid final UserDTO userDto) {
        final User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (userDto.getProfilePicture() != null) {
            Long imageId = ImageDTO.getImageId(userDto.getProfilePicture());
            userService.updateUser(user, userDto.getName(), imageId, userDto.getDescription(), EmailLanguages.fromString(userDto.getLanguage()));
        } else {
            userService.updateUser(user, userDto.getName(), userDto.getDescription(), EmailLanguages.fromString(userDto.getLanguage()));
        }
        return Response.ok(new GenericEntity<UserDTO>(UserDTO.fromUser(user, uriInfo)) {
        }).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = {VndType.APPLICATION_USER_VERIFY})
    @Produces(value = {VndType.APPLICATION_USER})
    @PreAuthorize("hasAuthority('ONE_TIME_TOKEN') and @securityAccessFunctions.isVerificationTokenValid(authentication)")
    public Response verifyUser(@PathParam("id") final long id, @Valid final UserVerifyDTO userVerifyDTO) {
        final User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (userVerifyDTO.isVerified()) {
            userService.verifyUser(user);
        }
        return Response.ok(new GenericEntity<UserDTO>(UserDTO.fromUser(user, uriInfo)) {
        }).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = {VndType.APPLICATION_USER_RESET_PASSWORD})
    public Response forgotUserPassword(@PathParam("id") final long id, @Valid final UserResetPasswordDTO userUpdatePasswordDTO) {
        final User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return changePasswordToken(user);
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = {VndType.APPLICATION_USER_PASSWORD})
    @Produces(value = {VndType.APPLICATION_USER})
    @PreAuthorize("hasAuthority('ONE_TIME_TOKEN') and @securityAccessFunctions.isResetPasswordTokenValid(authentication)")
    public Response resetUserPassword(@PathParam("id") final long id, @Valid final UserUpdatePasswordDTO userUpdatePasswordDTO) {
        final User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return changePassword(user, userUpdatePasswordDTO.getPassword());
    }

    @PATCH
    @Path("/{id}")
    @Consumes(value = {VndType.APPLICATION_USER_PROVIDER})
    @Produces(value = {VndType.APPLICATION_USER})
    public Response setProvider(@PathParam("id") final long id, @Valid final UserSetProviderDTO userSetProviderDTO) {
        User user = userService.setProvider(id, userSetProviderDTO.isProvider());
        return Response.ok(new GenericEntity<UserDTO>(UserDTO.fromUser(user, uriInfo)) {
        }).build();
    }

    private Response changePasswordToken(User user) {
        try {
            String token = jwtUtils.generateOneTimeToken(user.getEmail(), UserDTO.getSelfUri(user.getId(), uriInfo).toString());
            String seeDetailsUrl = env.getProperty("base_path") + "user/reset-password?token=" + token;
            userService.setUserResetPasswordToken(user, seeDetailsUrl, token);

            return Response.ok().build();
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(user.getId());
        }
    }

    private Response changePassword(User user, String newPassword) {
        userService.resetUserPassword(user.getId(), newPassword);
        return Response.ok(new GenericEntity<UserDTO>(UserDTO.fromUser(user, uriInfo)) {
        }).build();
    }

}
