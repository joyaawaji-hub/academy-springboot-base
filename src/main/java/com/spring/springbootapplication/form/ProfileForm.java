package com.spring.springbootapplication.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ProfileForm {

    @NotBlank(message = "自己紹介は50文字以上200文字以下で入力してください")
    @Size(
        min = 50,
        max = 200,
        message = "自己紹介は50文字以上200文字以下で入力してください"
    )
    private String selfIntroduction;

    private MultipartFile avatarImage;

    public String getSelfIntroduction() {
        return selfIntroduction;
    }

    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }

    public MultipartFile getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(MultipartFile avatarImage) {
        this.avatarImage = avatarImage;
    }
}