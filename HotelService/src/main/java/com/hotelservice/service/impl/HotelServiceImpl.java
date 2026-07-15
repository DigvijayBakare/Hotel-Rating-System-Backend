package com.hotelservice.service.impl;

import com.hotelservice.entities.Hotel;
import com.hotelservice.exceptions.HotelNotFoundException;
import com.hotelservice.repositories.HotelRepository;
import com.hotelservice.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HotelServiceImpl implements HotelService {
    private HotelRepository hotelRepository;

    @Autowired
    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public Hotel createHotel(Hotel hotel) {
        String randomHotelId = UUID.randomUUID().toString();
        hotel.setHotelId(randomHotelId);
        return hotelRepository.save(hotel);
    }

    @Override
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    @Override
    public Hotel getHotel(String hotelId) {
        return hotelRepository.findById(hotelId).orElseThrow(
                () -> new HotelNotFoundException("Hotel with id: " + hotelId + " not found in the database!"));
    }

    @Override
    public Hotel updateHotel(String hotelId, Hotel hotel) {
        Hotel updatedHotel = new Hotel(hotelId, hotel.getHotelName(), hotel.getLocation(), hotel.getAbout());
        return hotelRepository.save(updatedHotel);
    }

    @Override
    public void deleteHotel(String hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel with id: " + hotelId + " not found in the database!"));
        hotelRepository.delete(hotel);
    }
}
