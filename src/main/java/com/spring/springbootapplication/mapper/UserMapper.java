package com.spring.springbootapplication.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Insert("""
        INSERT INTO users (
            email,
            password,
            user_name,
            self_introduction,
            avatar_image
        )
        VALUES (
            #{email},
            #{password},
            #{userName},
            NULL,
            NULL
        )
        """)
    void insert(
        @Param("email") String email,
        @Param("password") String password,
        @Param("userName") String userName
    );

    @Select("""
        SELECT COUNT(*)
        FROM users
        WHERE email = #{email}
        """)
    int countByEmail(@Param("email") String email);

    @Select("""
        SELECT
            id,
            email,
            password,
            user_name,
            self_introduction,
            avatar_image
        FROM users
        WHERE email = #{email}
        """)
    Map<String, Object> findByEmail(@Param("email") String email);

    @Update("""
        UPDATE users
        SET
            self_introduction = #{selfIntroduction},
            avatar_image = #{avatarImage},
            updated_at = CURRENT_TIMESTAMP
        WHERE email = #{email}
        """)
    void updateProfile(
        @Param("email") String email,
        @Param("selfIntroduction") String selfIntroduction,
        @Param("avatarImage") String avatarImage
    );
}