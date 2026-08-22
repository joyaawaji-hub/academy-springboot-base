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
}