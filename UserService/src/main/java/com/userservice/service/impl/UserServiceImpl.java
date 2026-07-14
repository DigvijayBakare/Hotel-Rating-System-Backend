package com.userservice.service.impl;

import com.userservice.entities.Hotel;
import com.userservice.entities.Rating;
import com.userservice.entities.Users;
import com.userservice.exceptions.UserNotFoundException;
import com.userservice.externalapi.HotelService;
import com.userservice.repository.UsersRepository;
import com.userservice.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UsersService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    /*@Autowired
    public UserServiceImpl(UsersRepository usersRepository, RestTemplate restTemplate, HotelService hotelService) {
        this.usersRepository = usersRepository;
        this.restTemplate = restTemplate;
        this.hotelService = hotelService;
    }*/

    @Override
    public Users saveUser(Users users) {
        String randomUserId = UUID.randomUUID().toString();
        users.setUserId(randomUserId);
        return usersRepository.save(users);
    }

    @Override
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    @Override
    public Users getUser(String userId) {
        // get user from database with the help of user repository
        Users users = usersRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id" + userId + " is not found on the server"));

        log.info("user id while getting ratings: " + userId);
        // fetch rating of above user using Rating service with
        // http://localhost:8083/ratings/user/4dfa1d3e-dd82-4f89-97b3-04c7359626aa
        Rating[] ratingsOfUser = restTemplate.getForObject("http://RATINGSERVICE/ratings/user/" + userId, Rating[].class);
        log.debug("Ratings of user: {} ", (Object) ratingsOfUser);

        List<Rating> ratings = Arrays.stream(ratingsOfUser).toList();

        List<Rating> ratingList = ratings.stream().map(rating -> {
            // api call to hotel service to get the hotel
//            ResponseEntity<Hotel> hotelEntity = restTemplate.getForEntity("http://HOTELSERVICE/hotel/"+rating.getHotelId(), Hotel.class);
//            Hotel hotel = hotelEntity.getBody();
//            log.info("Response status code: {} ", hotelEntity.getStatusCode());

            ResponseEntity<Hotel> hotelResponse = hotelService.getHotel(rating.getHotelId());
            Hotel hotel = hotelResponse.getBody();

            // set the hotel to the rating
            rating.setHotel(hotel);

            // return the rating
            return rating;
        }).collect(Collectors.toList());

        users.setRatings(ratingList);
        return users;
    }

    @Override
    public Users updateUser(String userId, Users users) {
        Users updatedUser = new Users(userId, users.getUserName(), users.getEmail(), users.getAbout(), users.getUsersAge());
        return usersRepository.save(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {
        Users deleteUser = usersRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id" + userId + " is not found on the server"));
        usersRepository.delete(deleteUser);
    }
}
