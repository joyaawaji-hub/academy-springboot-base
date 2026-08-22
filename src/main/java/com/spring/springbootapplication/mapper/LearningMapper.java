package com.spring.springbootapplication.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    @Select("""
        SELECT category_name
        FROM categories
        WHERE id = #{categoryId}
        """)
    String findCategoryNameById(
        @Param("categoryId") Long categoryId
    );

    @Select("""
        SELECT COUNT(*)
        FROM learning_data
        WHERE user_id = #{userId}
          AND category_id = #{categoryId}
          AND item_name = #{itemName}
          AND learning_month = #{learningMonth}
        """)
    int countDuplicate(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId,
        @Param("itemName") String itemName,
        @Param("learningMonth") LocalDate learningMonth
    );

    @Insert("""
        INSERT INTO learning_data (
            user_id,
            category_id,
            item_name,
            learning_month,
            learning_hours
        )
        VALUES (
            #{userId},
            #{categoryId},
            #{itemName},
            #{learningMonth},
            #{learningHours}
        )
        """)
    void insertLearningData(
        @Param("userId") Long userId,
        @Param("categoryId") Long categoryId,
        @Param("itemName") String itemName,
        @Param("learningMonth") LocalDate learningMonth,
        @Param("learningHours") Integer learningHours
    );

    @Update("""
        UPDATE learning_data
        SET
            learning_hours = #{learningHours},
            updated_at = CURRENT_TIMESTAMP
        WHERE id = #{learningId}
          AND user_id = #{userId}
        """)
    void updateLearningHours(
        @Param("learningId") Long learningId,
        @Param("userId") Long userId,
        @Param("learningHours") Integer learningHours
    );

    @Delete("""
        DELETE FROM learning_data
        WHERE id = #{learningId}
          AND user_id = #{userId}
        """)
    void deleteLearningData(
        @Param("learningId") Long learningId,
        @Param("userId") Long userId
    );
}