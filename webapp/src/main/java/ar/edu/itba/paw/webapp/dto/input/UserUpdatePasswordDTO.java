package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserUpdatePasswordDTO {

  @NotNull
  @Size(min = 8)
  private String password;

  public UserUpdatePasswordDTO() {
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}

