package com.spring.springbootapplication;

import com.spring.springbootapplication.service.LearningService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LearningDeleteController {

    private final LearningService learningService;

    public LearningDeleteController(
            LearningService learningService) {

        this.learningService = learningService;
    }

    @PostMapping("/learning/delete")
    public String delete(
            @RequestParam Long learningId,
            @RequestParam String itemName,
            @RequestParam String month,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Object loginUserIdObject =
            session.getAttribute("loginUserId");

        if (loginUserIdObject == null) {
            return "redirect:/login";
        }

        Long userId =
            ((Number) loginUserIdObject).longValue();

        learningService.deleteLearningData(
            learningId,
            userId
        );

        redirectAttributes.addFlashAttribute(
            "showDeleteSuccessModal",
            true
        );

        redirectAttributes.addFlashAttribute(
            "deletedItemName",
            itemName
        );

        return "redirect:/learning?month=" + month;
    }
}