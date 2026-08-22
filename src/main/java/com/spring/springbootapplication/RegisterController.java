package com.spring.springbootapplication;

import com.spring.springbootapplication.form.RegisterForm;
import com.spring.springbootapplication.service.LearningService;
import com.spring.springbootapplication.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class RegisterController {

    private final UserService userService;
    private final LearningService learningService;

    public RegisterController(
            UserService userService,
            LearningService learningService) {

        this.userService = userService;
        this.learningService = learningService;
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

        Map<String, Object> user =
            userService.findByEmail(registerForm.getEmail());

        session.setAttribute(
            "loginUserId",
            user.get("id")
        );

        session.setAttribute(
            "loginUserEmail",
            registerForm.getEmail()
        );

        session.setAttribute(
            "loginUserName",
            registerForm.getUserName()
        );

        return "redirect:/top";
    }

    @GetMapping("/top")
    public String top(
            HttpSession session,
            Model model) {

        String email =
            (String) session.getAttribute("loginUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        Map<String, Object> user =
            userService.findByEmail(email);

        model.addAttribute("user", user);

        Long userId =
            ((Number) user.get("id")).longValue();

        YearMonth currentMonth = YearMonth.now();
        YearMonth twoMonthsAgo = currentMonth.minusMonths(2);

        LocalDate fromMonth =
            twoMonthsAgo.atDay(1);

        LocalDate toMonth =
            currentMonth.atDay(1);

        List<Map<String, Object>> chartData =
            learningService.findChartData(
                userId,
                fromMonth,
                toMonth
            );

        List<Integer> backendData =
            new ArrayList<>(List.of(0, 0, 0));

        List<Integer> frontendData =
            new ArrayList<>(List.of(0, 0, 0));

        List<Integer> infrastructureData =
            new ArrayList<>(List.of(0, 0, 0));

        for (Map<String, Object> row : chartData) {

            String categoryName =
                String.valueOf(row.get("category_name"));

            String monthText =
                String.valueOf(row.get("learning_month"));

            YearMonth rowMonth =
                YearMonth.parse(
                    monthText.substring(0, 7)
                );

            int monthIndex;

            if (rowMonth.equals(currentMonth.minusMonths(2))) {
                monthIndex = 0;
            } else if (rowMonth.equals(currentMonth.minusMonths(1))) {
                monthIndex = 1;
            } else if (rowMonth.equals(currentMonth)) {
                monthIndex = 2;
            } else {
                continue;
            }

            int totalHours =
                ((Number) row.get("total_hours")).intValue();

            switch (categoryName) {
                case "バックエンド":
                    backendData.set(monthIndex, totalHours);
                    break;

                case "フロントエンド":
                    frontendData.set(monthIndex, totalHours);
                    break;

                case "インフラ":
                    infrastructureData.set(monthIndex, totalHours);
                    break;

                default:
                    break;
            }
        }

        model.addAttribute(
            "chartLabels",
            List.of("先々月", "先月", "今月")
        );

        model.addAttribute(
            "backendData",
            backendData
        );

        model.addAttribute(
            "frontendData",
            frontendData
        );

        model.addAttribute(
            "infrastructureData",
            infrastructureData
        );

        return "top";
    }
}