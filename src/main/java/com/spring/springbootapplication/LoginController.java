package com.spring.springbootapplication;

import com.spring.springbootapplication.form.LoginForm;
import com.spring.springbootapplication.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model) {

        if (!model.containsAttribute("loginForm")) {
            model.addAttribute(
                "loginForm",
                new LoginForm()
            );
        }

        return "login";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute("loginForm") LoginForm loginForm,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<String, Object> user =
            userService.authenticate(
                loginForm.getEmail(),
                loginForm.getPassword()
            );

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                "loginError",
                "メールアドレス、もしくはパスワードが間違っています"
            );

            return "redirect:/login";
        }

        session.setAttribute(
            "loginUserEmail",
            user.get("email")
        );

        session.setAttribute(
            "loginUserName",
            user.get("user_name")
        );

        return "redirect:/top";
    }
}