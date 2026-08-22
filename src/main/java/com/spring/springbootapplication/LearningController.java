package com.spring.springbootapplication;

import com.spring.springbootapplication.service.LearningService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class LearningController {

    private final LearningService learningService;

    public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }

    @GetMapping("/learning")
    public String learningList(
            @RequestParam(required = false) String month,
            HttpSession session,
            Model model) {

        Object loginUserIdObject =
            session.getAttribute("loginUserId");

        if (loginUserIdObject == null) {
            return "redirect:/login";
        }

        Long loginUserId =
            ((Number) loginUserIdObject).longValue();

        YearMonth currentMonth = YearMonth.now();

        YearMonth selectedYearMonth;

        if (month == null || month.isBlank()) {
            selectedYearMonth = currentMonth;
        } else {
            try {
                selectedYearMonth = YearMonth.parse(month);
            } catch (Exception e) {
                selectedYearMonth = currentMonth;
            }
        }

        List<YearMonth> selectableMonths =
            new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            selectableMonths.add(
                currentMonth.minusMonths(i)
            );
        }

        LocalDate learningMonth =
            selectedYearMonth.atDay(1);

        List<Map<String, Object>> learningData =
            learningService.findLearningData(
                loginUserId,
                learningMonth
            );

        model.addAttribute(
            "learningData",
            learningData
        );

        model.addAttribute(
            "selectableMonths",
            selectableMonths
        );

        model.addAttribute(
            "selectedMonth",
            selectedYearMonth
        );

        model.addAttribute(
            "monthFormatter",
            DateTimeFormatter.ofPattern("M月")
        );

        return "learning-list";
    }
}