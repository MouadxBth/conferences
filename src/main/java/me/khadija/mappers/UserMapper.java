package me.khadija.mappers;

import me.khadija.models.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "hashedPassword", column = "hashed_password"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "email", column = "email"),
            @Result(property = "enabled", column = "enabled")
    })
    @Select("SELECT * FROM users WHERE username = #{username}")
    User find(@Param("username") String username);

    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "hashedPassword", column = "hashed_password"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "email", column = "email"),
            @Result(property = "enabled", column = "enabled")
    })
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(long id);

    @Select("SELECT id FROM users WHERE username = #{username}")
    long findId(@Param("username") String username);

    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "hashedPassword", column = "hashed_password"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "email", column = "email"),
            @Result(property = "enabled", column = "enabled")
    })
    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "hashedPassword", column = "hashed_password"),
            @Result(property = "firstName", column = "first_name"),
            @Result(property = "lastName", column = "last_name"),
            @Result(property = "email", column = "email"),
            @Result(property = "enabled", column = "enabled")
    })
    @Select("SELECT * FROM users")
    List<User> findAll();

    @Insert("INSERT INTO users VALUES( #{id} , #{username}," +
            "#{hashedPassword}," +
            "#{email}," +
            "#{firstName}," +
            "#{lastName}," +
            "#{enabled}" +
            ")")
    void insert(User user);

    @Update("UPDATE users SET username = #{username}," +
            "hashed_password = #{hashedPassword}," +
            "email = #{email}," +
            "first_name = #{firstName}," +
            "last_name = #{lastName}," +
            "enabled = #{enabled} " +
            "WHERE id = #{id}")
    void update(User user);

    @Delete("DELETE FROM users WHERE id = #{id}")
    void delete(User user);

    @Update("CREATE TABLE IF NOT EXISTS users (id int(11) NOT NULL AUTO_INCREMENT," +
            "username varchar(255) DEFAULT NULL," +
            "hashed_password varchar(255) DEFAULT NULL," +
            "email varchar(255) DEFAULT NULL," +
            "first_name varchar(128) DEFAULT NULL,"+
            "last_name varchar(128) DEFAULT NULL," +
            "enabled tinyint DEFAULT 0," +
            "PRIMARY KEY (`id`)," +
            "UNIQUE (`username`)," +
            "UNIQUE (`email`)" +
            ")")
    void createTableIfNotExists();

    @Update("DROP TABLE IF EXISTS users")
    void dropTableIfExists();

}
