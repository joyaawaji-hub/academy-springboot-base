package com.spring.springbootapplication.service;

import com.spring.springbootapplication.mapper.LearningMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class LearningService {

    private final LearningMapper learningMapper;

    public LearningService(LearningMapper learningMapper) {
        this.learningMapper = learningMapper;
    }

    public List<Map<String, Object>> findLearningData(
            Long userId,
            LocalDate learningMonth) {

        return learningMapper.findLearningData(
            userId,
            learningMonth
        );
    }

    public String findCategoryName(Long categoryId) {
        return learningMapper.findCategoryNameById(categoryId);
    }

    public boolean existsDuplicate(
            Long userId,
            Long categoryId,
            String itemName,
            LocalDate learningMonth) {

        return learningMapper.countDuplicate(
            userId,
            categoryId,
            itemName,
            learningMonth
        ) > 0;
    }

    public void addLearningData(
            Long userId,
            Long categoryId,
            String itemName,
            LocalDate learningMonth,
            Integer learningHours) {

        learningMapper.insertLearningData(
            userId,
            categoryId,
            itemName,
            learningMonth,
            learningHours
        );
    }

    public void updateLearningHours(
            Long learningId,
            Long userId,
            Integer learningHours) {

        learningMapper.updateLearningHours(
            learningId,
            userId,
            learningHours
        );
    }
    public void deleteLearningData(
            Long learningId,
            Long userId) {

        learningMapper.deleteLearningData(
            learningId,
            userId
        );
    }
}