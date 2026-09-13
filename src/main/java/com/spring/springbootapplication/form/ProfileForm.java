package com.spring.springbootapplication.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ProfileForm {

    @NotBlank(message = "\u81ea\u5df1\u7d39\u4ecb\u306f50\u6587\u5b57\u4ee5\u4e0a200\u6587\u5b57\u4ee5\u5185\u3067\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044")
    @Size(
        min = 50,
        max = 200,
        message = "\u81ea\u5df1\u7d39\u4ecb\u306f50\u6587\u5b57\u4ee5\u4e0a200\u6587\u5b57\u4ee5\u5185\u3067\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044"
    )
    private String selfIntroduction;

    private MultipartFile avatarImage;

    /*
     * バリデーションエラー時に一時保存した
     * アバター画像のファイル名
     */
    private String retainedAvatarFileName;

    /*
     * 画面表示用の元ファイル名
     */
    private String retainedAvatarOriginalName;

    public String getSelfIntroduction() {
        return selfIntroduction;
    }

    public void setSelfIntroduction(String selfIntroduction) {
        if (selfIntroduction == null) {
            this.selfIntroduction = null;
            return;
        }

        this.selfIntroduction = selfIntroduction
                .replace("\r\n", "\n")
                .replace("\r", "\n");
    }

    public MultipartFile getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(MultipartFile avatarImage) {
        this.avatarImage = avatarImage;
    }

    public String getRetainedAvatarFileName() {
        return retainedAvatarFileName;
    }

    public void setRetainedAvatarFileName(String retainedAvatarFileName) {
        this.retainedAvatarFileName = retainedAvatarFileName;
    }

    public String getRetainedAvatarOriginalName() {
        return retainedAvatarOriginalName;
    }

    public void setRetainedAvatarOriginalName(String retainedAvatarOriginalName) {
        this.retainedAvatarOriginalName = retainedAvatarOriginalName;
    }
}