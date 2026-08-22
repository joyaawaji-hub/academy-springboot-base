package com.spring.springbootapplication.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LearningAddForm {

    @NotBlank(message = "項目名は必ず入力してください")
    @Size(
        max = 50,
        message = "項目名は50文字以内で入力してください"
    )
    private String itemName;

    private String learningHours;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getLearningHours() {
        return learningHours;
    }

    public void setLearningHours(String learningHours) {
        this.learningHours = learningHours;
    }
}