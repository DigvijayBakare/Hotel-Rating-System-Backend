package com.userservice.controller;

import com.userservice.entities.Users;
import com.userservice.service.UsersService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    // create user api
    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users users) {
        Users user = usersService.saveUser(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

//    int retryCount = 1;

    // get single user

//    @CircuitBreaker(name = "ratingHotelBreaking", fallbackMethod = "ratingHotelFallback")
//    @Retry(name = "ratingHotelService", fallbackMethod = "ratingHotelFallback")
    @GetMapping("/{userId}")
    @RateLimiter(name = "userRateLimiter", fallbackMethod = "ratingHotelFallback")
    public ResponseEntity<Users> getUser(@PathVariable("userId") String userId) {
//        log.info("Retry count: {}", retryCount);
//        retryCount++;
        Users user = usersService.getUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


    // creating a fallback method for circuit breaker
    public ResponseEntity<Users> ratingHotelFallback(String userId, Exception ex) {
        log.info("Fallback method executed because service is down! {} ", ex.getMessage());
        ex.printStackTrace();
        Users user = Users.builder().userName("Dummy").email("dummy@gmail.com").about("This is dummy user data!")
                .userId("12456321").build();
        return new ResponseEntity<>(user, HttpStatus.UNAUTHORIZED);
    }

    // get all users
    @PreAuthorize("hasAuthority('SCOPE_internal') || hasAuthority('Admin')")
    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> allUsers = usersService.getAllUsers();
        return ResponseEntity.status(HttpStatus.FOUND).body(allUsers);
    }

    // update the user with specified id
    @PreAuthorize("hasAuthority('SCOPE_internal')")
    @PutMapping("/{userId}")
    public ResponseEntity<Users> updateUser(@PathVariable("userId") String userId, @RequestBody Users users) {
        Users user = usersService.updateUser(userId, users);
        return ResponseEntity.ok().body(user);
    }

    // delete the user with specified id
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") String userId) {
        usersService.deleteUser(userId);
        return ResponseEntity.ok("User with id: " + userId + " deleted successfully!!");
    }
}
