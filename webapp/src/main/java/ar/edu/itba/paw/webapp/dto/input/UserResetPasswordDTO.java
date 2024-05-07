package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.Null;

public class UserResetPasswordDTO {

    @Null
    private String password;

    public UserResetPasswordDTO() {
    }

    public String getPassword() {
        return password;
    }

}
