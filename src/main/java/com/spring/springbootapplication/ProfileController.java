package com.spring.springbootapplication;

import com.spring.springbootapplication.form.ProfileForm;
import com.spring.springbootapplication.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile/edit")
    public String editProfile(
            HttpSession session,
            Model model) {

        String email =
            (String) session.getAttribute("loginUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        Map<String, Object> user =
            userService.findByEmail(email);

        ProfileForm profileForm =
            new ProfileForm();

        if (user != null) {
            profileForm.setSelfIntroduction(
                (String) user.get("self_introduction")
            );
        }

        model.addAttribute(
            "profileForm",
            profileForm
        );

        model.addAttribute(
            "currentAvatarImage",
            user != null
                ? user.get("avatar_image")
                : null
        );

        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            @Valid @ModelAttribute("profileForm") ProfileForm profileForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model) throws IOException {

        String email =
            (String) session.getAttribute("loginUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        Map<String, Object> user =
            userService.findByEmail(email);

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                "currentAvatarImage",
                user != null
                    ? user.get("avatar_image")
                    : null
            );

            return "profile-edit";
        }

        String avatarFileName =
            user != null
                ? (String) user.get("avatar_image")
                : null;

        MultipartFile avatar =
            profileForm.getAvatarImage();

        if (avatar != null && !avatar.isEmpty()) {

            String originalFileName =
                avatar.getOriginalFilename();

            String extension = "";

            if (originalFileName != null &&
                originalFileName.contains(".")) {

                extension =
                    originalFileName.substring(
                        originalFileName.lastIndexOf(".")
                    );
            }

            avatarFileName =
                UUID.randomUUID() + extension;

            Path uploadDirectory =
                Paths.get("uploads");

            Files.createDirectories(uploadDirectory);

            Path destination =
                uploadDirectory.resolve(avatarFileName);

            Files.copy(
                avatar.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING
            );
        }

        userService.updateProfile(
            email,
            profileForm.getSelfIntroduction(),
            avatarFileName
        );

        return "redirect:/top";
    }
}