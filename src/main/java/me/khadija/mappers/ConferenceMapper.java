package me.khadija.mappers;

import me.khadija.models.Conference;
import me.khadija.models.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface ConferenceMapper {

    @Select("SELECT * FROM conferences")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description", jdbcType = JdbcType.LONGVARCHAR),
            @Result(property = "member_limit", column = "member_limit"),
            @Result(property = "privateConference", column = "is_private"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "startsAt", column = "starts_at"),
            @Result(property = "started", column = "started"),
            @Result(property = "owner",
                    column = "owner",
                    javaType = User.class,
                    one = @One(select = "me.khadija.mappers.UserMapper.findById")
            )
    })
    List<Conference> findAll();

    @Select("SELECT * FROM conferences WHERE name = #{name}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description", jdbcType = JdbcType.LONGVARCHAR),
            @Result(property = "member_limit", column = "member_limit"),
            @Result(property = "privateConference", column = "is_private"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "startsAt", column = "starts_at"),
            @Result(property = "started", column = "started"),
            @Result(property = "owner",
                    column = "owner",
                    javaType = User.class,
                    one = @One(select = "me.khadija.mappers.UserMapper.findById")
            )
    })
    Conference find(@Param("name") String name);

    @Select("SELECT * FROM conferences WHERE id = #{id}")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description", jdbcType = JdbcType.LONGVARCHAR),
            @Result(property = "member_limit", column = "member_limit"),
            @Result(property = "privateConference", column = "is_private"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "startsAt", column = "starts_at"),
            @Result(property = "started", column = "started"),
            @Result(property = "owner",
                    column = "owner",
                    javaType = User.class,
                    one = @One(select = "me.khadija.mappers.UserMapper.findById")
            )
    })
    Conference findById(long id);

    @Insert("INSERT INTO conferences VALUES( #{id} , #{name}," +
            "#{owner.id}," +
            "#{title}," +
            "#{description}," +
            "#{member_limit}," +
            "#{privateConference}," +
            "#{createdAt}," +
            "#{startsAt}," +
            "#{started}" +
            ")")
    void insert(Conference conference);

    @Update("UPDATE conferences SET name = #{name}," +
            "owner = #{owner.id}," +
            "title = #{title}," +
            "description = #{description}, " +
            "member_limit = #{member_limit}, " +
            "is_private = #{privateConference}, " +
            "created_at = #{createdAt}, " +
            "starts_at = #{startsAt}, " +
            "started = #{started} " +
            "WHERE id = #{id}")
    void update(Conference conference);

    @Delete("DELETE FROM conferences WHERE id = #{id}")
    void delete(Conference conference);

    @Update("CREATE TABLE IF NOT EXISTS conferences (id int(11) NOT NULL AUTO_INCREMENT," +
            "name varchar(255) DEFAULT NULL," +
            "owner int(11) DEFAULT NULL," +
            "title varchar(255) DEFAULT NULL," +
            "description text DEFAULT NULL," +
            "member_limit int DEFAULT -1," +
            "is_private tinyint DEFAULT 0,"+
            "created_at DATETIME NOT NULL," +
            "starts_at DATETIME DEFAULT NULL," +
            "started TINYINT DEFAULT 0," +
            "PRIMARY KEY (`id`)," +
            "FOREIGN KEY (owner) REFERENCES users(id) ON DELETE CASCADE," +
            "UNIQUE (`name`)" +
            ")")
    void createTableIfNotExists();

    @Update("DROP TABLE IF EXISTS conferences")
    void dropTableIfExists();

}
