package com.userservice;

import com.userservice.entities.Rating;
import com.userservice.externalapi.RatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {

	@Autowired
	private RatingService ratingService;

	/*@Test
	void creatingRating() {
		Rating rating = Rating.builder().rating(10).userId("").hotelId("").feedback("This is created using feign client").build();
		ratingService.createRating(rating);
		System.out.println("Created!");
	}

	@Test
	void deleteRating() {
		String ratingId = "67cef353d7541314213033c7";
		ratingService.deleteRating(ratingId);
		System.out.println("Deleted!");
	}*/

}
