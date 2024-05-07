package ar.edu.itba.paw.webapp.dto.input;

public class UserVerifyDTO {

    private boolean verified;

    public UserVerifyDTO() {
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
