package com.userservice.externalapi;

import com.userservice.entities.Rating;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@FeignClient(name = "RATINGSERVICE")
public interface RatingService {
    @GetMapping("/ratings/user/{ratingId}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Rating> getRatings(@PathVariable("ratingId") String ratingId);

    // post api for creating a rating
    @PostMapping("/ratings")
    public Rating createRating(Rating rating);

    // put api for updating the rating
    @PutMapping("/ratings/rating/{ratingId}")
    Rating updateRating(@PathVariable("ratingId") String ratingId, Rating rating);

    // delete api for deleting the rating
    @DeleteMapping("/ratings/{ratingId}")
    void deleteRating(@PathVariable("ratingId") String ratingId);
}
