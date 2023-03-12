package me.khadija.mappers;

import me.khadija.models.ConfirmationToken;
import me.khadija.models.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ConfirmationTokenMapper {

    @Select("SELECT * FROM confirmation_tokens WHERE user = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "token", column = "token"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "expiresAt", column = "expires_at"),
            @Result(property = "confirmedAt", column = "confirmed_at"),
            @Result(property = "user",
                    column = "user",
                    javaType = User.class,
                    one = @One(select = "me.khadija.mappers.UserMapper.find")
            )
    })
    List<ConfirmationToken> findUser(User user);

    @Select("SELECT * FROM confirmation_tokens")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "token", column = "token"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "expiresAt", column = "expires_at"),
            @Result(property = "confirmedAt", column = "confirmed_at"),
            @Result(property = "user",
                    column = "user",
                    javaType = User.class,
                    one = @One(select = "me.khadija.mappers.UserMapper.findById")
            )
    })
    List<ConfirmationToken> findAll();

    @Select("SELECT * FROM confirmation_tokens WHERE token = #{token}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "token", column = "token"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "expiresAt", column = "expires_at"),
            @Result(property = "confirmedAt", column = "confirmed_at"),
            @Result(property = "user",
                    column = "user",
                    javaType = User.class,
                    one = @One(select = "me.khadija.mappers.UserMapper.findById")
            )
    })
    ConfirmationToken find(String token);

    @Insert("INSERT INTO confirmation_tokens (token, " +
            "created_at, " +
            "expires_at, " +
            "confirmed_at, " +
            "user) VALUES( #{token}," +
            "#{createdAt}," +
            "#{expiresAt}," +
            "#{confirmedAt}," +
            "#{user.id}" +
            ")")
    void insert(ConfirmationToken confirmationToken);

    @Update("UPDATE confirmation_tokens SET token = #{token}," +
            "created_at = #{createdAt}," +
            "expires_at = #{expiresAt}," +
            "confirmed_at = #{confirmedAt}," +
            "user = #{user.id} " +
            "WHERE id = #{id}")
    void update(ConfirmationToken confirmationToken);

    @Delete("DELETE FROM confirmation_tokens WHERE id = #{id}")
    void delete(ConfirmationToken confirmationToken);

    @Update("CREATE TABLE IF NOT EXISTS confirmation_tokens (id int(11) NOT NULL AUTO_INCREMENT," +
            "token varchar(255) NOT NULL," +
            "created_at DATETIME NOT NULL," +
            "expires_at DATETIME NOT NULL," +
            "confirmed_at DATETIME DEFAULT NULL," +
            "user int(11) NOT NULL," +
            "PRIMARY KEY (`id`)," +
            "FOREIGN KEY (user) REFERENCES users(id) ON DELETE CASCADE," +
            "UNIQUE(`token`)" +
            ")")
    void createTableIfNotExists();

    @Update("DROP TABLE IF EXISTS confirmation_tokens")
    void dropTableIfExists();

}
