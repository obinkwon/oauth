package oauth.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/web/login")
public class LoginWebController {
	
    @GetMapping("")
    public String loginView(final Model model) {

        return "thymeleaf/login";
    }
    
    @GetMapping("/signup")
    public String signupView(@RequestParam(required = false) String email, @RequestParam(required = false) boolean isSocial, final Model model) {
        model.addAttribute("email", email);
        model.addAttribute("isSocial", isSocial);
    	return "thymeleaf/signup";
    }
}
