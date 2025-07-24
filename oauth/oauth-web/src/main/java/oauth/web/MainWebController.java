package oauth.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web/main")
public class MainWebController {

    @GetMapping("")
    public String mainView(final Model model) {

        return "thymeleaf/main";
    }
}
