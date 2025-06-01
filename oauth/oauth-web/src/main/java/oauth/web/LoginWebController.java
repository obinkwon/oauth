package oauth.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginWebController {
	
    @GetMapping("/web/login")
    public String loginView(final Model model) {

        return "thymeleaf/login";
    }
    
    @GetMapping("/web/signup")
    public String signupView(final Model model) {
    	
    	return "thymeleaf/signup";
    }
    
    @GetMapping("/web/main")
    public String mainView(final Model model) {
    	
    	return "thymeleaf/main";
    }
}
