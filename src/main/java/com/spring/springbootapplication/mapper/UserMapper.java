package com.spring.springbootapplication.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
