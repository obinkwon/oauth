package oauth.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/login")
public class LoginWebController {
	
    @GetMapping("")
    public String loginView(final Model model) {

        return "thymeleaf/login";
    }
    
    @GetMapping("/signup")
    public String signupView(final Model model) {
    	
    	return "thymeleaf/signup";
    }
}
