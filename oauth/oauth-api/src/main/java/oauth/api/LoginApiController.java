package oauth.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth.core.entity.UserEntity;
import oauth.core.model.user.UserRegistRequestDto;
import oauth.core.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/api/login")
public class LoginApiController {

    private final UserService userService;

    @PostMapping("/signup")
    public String signup(@Valid UserRegistRequestDto request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return "redirect:/web/signup";
        }

        UserEntity userEntity = userService.createUserData(request.getUsername(), request.getEmail(), request.getPassword());
        return "redirect:/web/login";
    }
}
