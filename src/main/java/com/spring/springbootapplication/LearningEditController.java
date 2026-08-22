package com.spring.springbootapplication;

import com.spring.springbootapplication.service.LearningService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LearningEditController {

    private final LearningService learningService;

    public LearningEditController(
            LearningService learningService) {

        this.learningService = learningService;
    }

    @PostMapping("/learning/edit")
    public String edit(
            @RequestParam Long learningId,
            @RequestParam Integer learningHours,
            @RequestParam String month,
            @RequestParam String itemName,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Object loginUserIdObject =
            session.getAttribute("loginUserId");

        if (loginUserIdObject == null) {
            return "redirect:/login";
        }

        Long userId =
            ((Number) loginUserIdObject).longValue();

        if (learningHours < 0) {
            return "redirect:/learning?month=" + month;
        }

        learningService.updateLearningHours(
            learningId,
            userId,
            learningHours
        );

        redirectAttributes.addFlashAttribute(
            "showEditSuccessModal",
            true
        );

        redirectAttributes.addFlashAttribute(
            "editedItemName",
            itemName
        );

        return "redirect:/learning?month=" + month;
    }
}