package com.spring.springbootapplication;

import com.spring.springbootapplication.form.LearningAddForm;
import com.spring.springbootapplication.service.LearningService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;

@Controller
public class LearningAddController {

    private final LearningService learningService;

    public LearningAddController(
            LearningService learningService) {

        this.learningService = learningService;
    }

    @GetMapping("/learning/add")
    public String showAdd(
            @RequestParam Long categoryId,
            @RequestParam String month,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loginUserId") == null) {
            return "redirect:/login";
        }

        YearMonth selectedMonth;

        try {
            selectedMonth = YearMonth.parse(month);
        } catch (Exception e) {
            selectedMonth = YearMonth.now();
        }

        String categoryName =
            learningService.findCategoryName(categoryId);

        if (categoryName == null) {
            return "redirect:/learning";
        }

        if (!model.containsAttribute("learningAddForm")) {
            model.addAttribute(
                "learningAddForm",
                new LearningAddForm()
            );
        }

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("selectedMonth", selectedMonth);

        return "learning-add";
    }

    @PostMapping("/learning/add")
    public String add(
            @RequestParam Long categoryId,
            @RequestParam String month,
            @Valid @ModelAttribute("learningAddForm")
                LearningAddForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Object loginUserIdObject =
            session.getAttribute("loginUserId");

        if (loginUserIdObject == null) {
            return "redirect:/login";
        }

        Long userId =
            ((Number) loginUserIdObject).longValue();

        YearMonth selectedMonth;

        try {
            selectedMonth = YearMonth.parse(month);
        } catch (Exception e) {
            selectedMonth = YearMonth.now();
        }

        LocalDate learningMonth =
            selectedMonth.atDay(1);

        Integer learningHours = null;

        if (form.getLearningHours() == null ||
            form.getLearningHours().isBlank()) {

            bindingResult.rejectValue(
                "learningHours",
                "required",
                "学習時間は必ず入力してください"
            );

        } else {

            try {
                learningHours =
                    Integer.parseInt(
                        form.getLearningHours()
                    );

                if (learningHours < 0) {
                    bindingResult.rejectValue(
                        "learningHours",
                        "min",
                        "学習時間は0以上の数字で入力してください"
                    );
                }

            } catch (NumberFormatException e) {

                bindingResult.rejectValue(
                    "learningHours",
                    "number",
                    "学習時間は0以上の数字で入力してください"
                );
            }
        }

        if (form.getItemName() != null &&
            !form.getItemName().isBlank() &&
            form.getItemName().length() <= 50) {

            if (learningService.existsDuplicate(
                    userId,
                    categoryId,
                    form.getItemName(),
                    learningMonth)) {

                bindingResult.rejectValue(
                    "itemName",
                    "duplicate",
                    form.getItemName()
                        + "は既に登録されています"
                );
            }
        }

        String categoryName =
            learningService.findCategoryName(categoryId);

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                "categoryId",
                categoryId
            );

            model.addAttribute(
                "categoryName",
                categoryName
            );

            model.addAttribute(
                "selectedMonth",
                selectedMonth
            );

            return "learning-add";
        }

        learningService.addLearningData(
            userId,
            categoryId,
            form.getItemName(),
            learningMonth,
            learningHours
        );

        redirectAttributes.addFlashAttribute(
            "showSuccessModal",
            true
        );

        redirectAttributes.addFlashAttribute(
            "registeredItemName",
            form.getItemName()
        );

        redirectAttributes.addFlashAttribute(
            "registeredLearningHours",
            learningHours
        );

        return "redirect:/learning/add"
            + "?categoryId=" + categoryId
            + "&month=" + selectedMonth;
    }
}