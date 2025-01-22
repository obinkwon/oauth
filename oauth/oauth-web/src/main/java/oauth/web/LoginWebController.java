package oauth.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;

@Controller
public class LoginWebController {
	
    @GetMapping("/web/login-view")
    @Operation(tags = {"ContWebController"}, summary = "기본 컨텐츠 화면", description = "기본 컨텐츠 화면 화면을 호출한다.")
    public String loginView(final Model model) {

        return "thymeleaf/login";
    }
}
