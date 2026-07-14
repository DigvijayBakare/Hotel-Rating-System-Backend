package com.userservice.externalapi;

import com.userservice.entities.Hotel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "HOTELSERVICE")
public interface HotelService {

    @GetMapping("/hotel/{hotelId}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Hotel> getHotel(@PathVariable("hotelId") String hotelId);
}
