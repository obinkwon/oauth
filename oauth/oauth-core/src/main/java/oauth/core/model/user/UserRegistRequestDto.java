package oauth.core.model.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistRequestDto {

    @Size(min = 3, max = 25)
    @NotEmpty(message = "사용자 명은 필수입니다.")
    private String username;

    @NotEmpty(message = "이메일은 필수입니다.")
    @Email
    private String email;

    private String password;
    private String confirmPassword;
    private boolean social;
}
