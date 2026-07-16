package com.ratingservice.controller;

import com.ratingservice.entites.Rating;
import com.ratingservice.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ratings")
public class RatingController {
    private final RatingService ratingService;
    private final Logger LOGGER = LoggerFactory.getLogger(RatingController.class);

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    // create a rating
    @PreAuthorize("hasAuthority('Admin')")
    @PostMapping
    public ResponseEntity<Rating> createRating(@RequestBody Rating rating) {
        Rating createdRating = this.ratingService.creatingRating(rating);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRating);
    }

    // get all ratings
    @GetMapping
    public ResponseEntity<List<Rating>> getAllRatings() {
        List<Rating> allRating = ratingService.getAllRating();
        return ResponseEntity.status(HttpStatus.OK).body(allRating);
    }

    // get all ratings by user id
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getAllRatingsByUser(@PathVariable String userId) {
        LOGGER.debug("Rating controller: all ratings of user: {}", userId);
        List<Rating> allRating = ratingService.getAllRatingByUser(userId);
        LOGGER.debug("All ratings: {}", allRating);
        return ResponseEntity.status(HttpStatus.OK).body(allRating);
    }

    // get all ratings by hotel id
    @PreAuthorize("hasAuthority('SCOPE_internal') || hasAuthority('Admin')")
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Rating>> getAllRatingsByHotel(@PathVariable String hotelId) {
        List<Rating> allRating = ratingService.getAllRatingByHotel(hotelId);
        return ResponseEntity.status(HttpStatus.OK).body(allRating);
    }

    // update a rating by user id
    @PutMapping("/rating/{ratingId}")
    @PreAuthorize("hasAuthority('SCOPE_internal')")
    public ResponseEntity<Rating> updateRating(@PathVariable String ratingId, @RequestBody Rating rating) {
        Rating updatedRating = ratingService.updateRating(ratingId, rating);
        return ResponseEntity.ok(updatedRating);
    }

    // delete a rating
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<String> deleteRating(@PathVariable String ratingId) {
        ratingService.deleteRating(ratingId);
        return ResponseEntity.ok("Rating with id: " + ratingId + " deleted successfully!");
    }
}
