package com.userservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "User_info")
public class Users {
    @Id
    private String userId;

    private String userName;
    private String email;
    private String about;
    private String usersAge;

    @Transient
    private List<Rating> ratings = new ArrayList<>();

    public Users(String userId, String userName, String email, String about, String usersAge) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.about = about;
        this.usersAge = usersAge;
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", about='" + about + '\'' +
                ", usersAge='" + usersAge + '\'' +
                '}';
    }
}
