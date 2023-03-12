package me.khadija.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserConference {
    private Long id;
    private User user;
    private Conference conference;
}
