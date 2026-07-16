package com.ratingservice.service;

import com.ratingservice.entites.Rating;

import java.util.List;

public interface RatingService {
    // create a rating
    Rating creatingRating(Rating rating);

    // get all rating
    List<Rating> getAllRating();

    // get all rating by userId
    List<Rating> getAllRatingByUser(String userId);

    // get all rating by hotelId
    List<Rating> getAllRatingByHotel(String hotelId);

    // update the rating by user id
    Rating updateRating(String ratingId, Rating rating);

    // delete rating by id
    void deleteRating(String ratingId);
}
