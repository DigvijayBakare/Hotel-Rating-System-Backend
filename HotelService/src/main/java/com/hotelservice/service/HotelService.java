package com.hotelservice.service;

import com.hotelservice.entities.Hotel;

import java.util.List;

public interface HotelService {
    // create a hotel
    Hotel createHotel(Hotel hotel);

    // get all hotels
    List<Hotel> getAllHotels();

    // get single hotel
    Hotel getHotel(String hotelId);

    // update hotel
    Hotel updateHotel(String hotelId, Hotel hotel);

    // delete hotel
    void deleteHotel(String hotelId);
}
