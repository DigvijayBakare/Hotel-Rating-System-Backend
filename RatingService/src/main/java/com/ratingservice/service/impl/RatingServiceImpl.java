package com.ratingservice.service.impl;

import com.ratingservice.entites.Rating;
import com.ratingservice.exception.RatingNotFoundException;
import com.ratingservice.repository.RatingRepository;
import com.ratingservice.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingServiceImpl implements RatingService {
    private RatingRepository ratingRepository;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public Rating creatingRating(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override
    public List<Rating> getAllRating() {
        return ratingRepository.findAll();
    }

    @Override
    public List<Rating> getAllRatingByUser(String userId) {
        return ratingRepository.findByUserId(userId);
    }

    @Override
    public List<Rating> getAllRatingByHotel(String hotelId) {
        return ratingRepository.findByHotelId(hotelId);
    }

    @Override
    public Rating updateRating(String ratingId, Rating rating) {
        ratingRepository.findById(ratingId).orElseThrow(() ->
                new RatingNotFoundException("Rating with id: " + ratingId + " not found!!"));

        Rating updatedRating = new Rating(ratingId, rating.getUserId(), rating.getHotelId(), rating.getRating(), rating.getFeedback());

        return ratingRepository.save(updatedRating);
    }

    @Override
    public void deleteRating(String ratingId) {
        Rating rating = ratingRepository.findById(ratingId).orElseThrow(() ->
                new RatingNotFoundException("Rating with id: " + ratingId + " not found!!"));
        ratingRepository.delete(rating);
    }
}
