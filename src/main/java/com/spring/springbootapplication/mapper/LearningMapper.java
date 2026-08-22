package com.spring.springbootapplication.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface LearningMapper {

    @Select("""
        SELECT
            c.id AS category_id,
            c.category_name,
            c.sort_order,
            ld.id AS learning_id,
            ld.item_name,
            ld.learning_hours
        FROM categories c
        LEFT JOIN learning_data ld
            ON c.id = ld.category_id
            AND ld.user_id = #{userId}
            AND ld.learning_month = #{learningMonth}
        ORDER BY
            c.sort_order,
            ld.id
        """)
    List<Map<String, Object>> findLearningData(
        @Param("userId") Long userId,
        @Param("learningMonth") LocalDate learningMonth
    );
}