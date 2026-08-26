package com.spring.springbootapplication;

import com.spring.springbootapplication.form.RegisterForm;
import com.spring.springbootapplication.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerForm") RegisterForm registerForm,
            BindingResult bindingResult,
            HttpSession session) {

        if (userService.existsByEmail(registerForm.getEmail())) {
            bindingResult.rejectValue(
                "email",
                "duplicate",
                "このメールアドレスは既に登録されています"
            );
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.register(registerForm);

        session.setAttribute("loginUserEmail", registerForm.getEmail());
        session.setAttribute("loginUserName", registerForm.getUserName());

        return "redirect:/top";
    }

    @GetMapping("/top")
    public String top(HttpSession session) {

        if (session.getAttribute("loginUserEmail") == null) {
            return "redirect:/login";
        }

        return "top";
    }
}
