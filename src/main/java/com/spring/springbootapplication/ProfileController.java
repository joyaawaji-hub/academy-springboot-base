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

    private static final Path UPLOAD_DIRECTORY =
        Paths.get("uploads");

    private static final Path TEMP_DIRECTORY =
        Paths.get("uploads", "temp");

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

        MultipartFile avatar =
            profileForm.getAvatarImage();

        System.out.println(
            "自己紹介文字数: " +
            (profileForm.getSelfIntroduction() == null
                ? "null"
                : profileForm.getSelfIntroduction().length())
        );

        /*
         * バリデーションエラーでも
         * 選択された画像を一時保存する。
         */
        if (bindingResult.hasErrors()) {

            if (avatar != null && !avatar.isEmpty()) {

                deleteTemporaryFile(
                    profileForm.getRetainedAvatarFileName()
                );

                String temporaryFileName =
                    saveTemporaryFile(avatar);

                profileForm.setRetainedAvatarFileName(
                    temporaryFileName
                );

                profileForm.setRetainedAvatarOriginalName(
                    avatar.getOriginalFilename()
                );
            }

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

        /*
         * 今回新しくファイルを選択した場合
         */
        if (avatar != null && !avatar.isEmpty()) {

            deleteTemporaryFile(
                profileForm.getRetainedAvatarFileName()
            );

            avatarFileName =
                savePermanentFile(avatar);

        /*
         * バリデーションエラー前に選択していた
         * 一時ファイルがある場合
         */
        } else if (
            profileForm.getRetainedAvatarFileName() != null &&
            !profileForm.getRetainedAvatarFileName().isBlank()
        ) {

            avatarFileName =
                moveTemporaryFileToPermanent(
                    profileForm.getRetainedAvatarFileName()
                );
        }

        userService.updateProfile(
            email,
            profileForm.getSelfIntroduction(),
            avatarFileName
        );

        return "redirect:/top";
    }

    private String saveTemporaryFile(
            MultipartFile file) throws IOException {

        Files.createDirectories(TEMP_DIRECTORY);

        String fileName =
            createSafeFileName(file.getOriginalFilename());

        Path destination =
            TEMP_DIRECTORY.resolve(fileName);

        Files.copy(
            file.getInputStream(),
            destination,
            StandardCopyOption.REPLACE_EXISTING
        );

        return fileName;
    }

    private String savePermanentFile(
            MultipartFile file) throws IOException {

        Files.createDirectories(UPLOAD_DIRECTORY);

        String fileName =
            createSafeFileName(file.getOriginalFilename());

        Path destination =
            UPLOAD_DIRECTORY.resolve(fileName);

        Files.copy(
            file.getInputStream(),
            destination,
            StandardCopyOption.REPLACE_EXISTING
        );

        return fileName;
    }

    private String moveTemporaryFileToPermanent(
            String retainedFileName) throws IOException {

        Files.createDirectories(UPLOAD_DIRECTORY);

        String safeFileName =
            Paths.get(retainedFileName)
                .getFileName()
                .toString();

        Path temporaryFile =
            TEMP_DIRECTORY.resolve(safeFileName);

        if (!Files.exists(temporaryFile)) {
            return null;
        }

        Path destination =
            UPLOAD_DIRECTORY.resolve(safeFileName);

        Files.move(
            temporaryFile,
            destination,
            StandardCopyOption.REPLACE_EXISTING
        );

        return safeFileName;
    }

    private void deleteTemporaryFile(
            String retainedFileName) throws IOException {

        if (retainedFileName == null ||
            retainedFileName.isBlank()) {
            return;
        }

        String safeFileName =
            Paths.get(retainedFileName)
                .getFileName()
                .toString();

        Files.deleteIfExists(
            TEMP_DIRECTORY.resolve(safeFileName)
        );
    }

    private String createSafeFileName(
            String originalFileName) {

        String extension = "";

        if (originalFileName != null &&
            originalFileName.contains(".")) {

            String candidate =
                originalFileName.substring(
                    originalFileName.lastIndexOf(".")
                );

            if (candidate.matches("\\.[A-Za-z0-9]{1,10}")) {
                extension = candidate;
            }
        }

        return UUID.randomUUID() + extension;
    }
}