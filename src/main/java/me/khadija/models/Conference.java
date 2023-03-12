package me.khadija.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conference {
    private Long id;
    private String name;
    private User owner;
    private String title;
    private String description;
    private Integer member_limit;
    private Boolean privateConference;
    private LocalDateTime createdAt;
    private LocalDateTime startsAt;
    private Boolean started;

}
