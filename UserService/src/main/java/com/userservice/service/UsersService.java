package com.userservice.service;

import com.userservice.entities.Users;

import java.util.List;

public interface UsersService {
    // create a user
    Users saveUser(Users users);

    // get all users
    List<Users> getAllUsers();

    // get single user with specific id
    Users getUser(String userId);

    // update a user
    Users updateUser(String userId, Users users);

    // delete a user
    void deleteUser(String userId);
}
