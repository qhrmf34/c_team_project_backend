package com.hotel_project.hotel_jpa.hotel.mapper;

import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.hotel.dto.*;
import com.hotel_project.hotel_jpa.room.dto.RoomDto;
import com.hotel_project.review_jpa.reviews.dto.ReviewsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HotelPublicMapper {

    List<HotelSummaryDto> searchHotels(
            @Param("destination") String destination,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("guests") Integer guests,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("rating") Integer rating,
            @Param("hotelType") String hotelType,
            @Param("freebies") List<Long> freebies,
            @Param("amenities") List<Long> amenities,
            @Param("sortBy") String sortBy,
            @Param("offset") long offset,
            @Param("size") int size
    );

    Long countSearchHotels(
            @Param("destination") String destination,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("guests") Integer guests,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("rating") Integer rating,
            @Param("hotelType") String hotelType,
            @Param("freebies") List<Long> freebies,
            @Param("amenities") List<Long> amenities
    );

    HotelDetailDto findHotelDetailById(@Param("hotelId") Long hotelId);

    List<FreebiesDto> findFreebiesByHotelId(@Param("hotelId") Long hotelId);

    List<AmenitiesDto> findAmenitiesByHotelId(@Param("hotelId") Long hotelId);

    List<String> findImagesByHotelId(@Param("hotelId") Long hotelId);

    // RoomDto로 변경
    List<RoomDto> findAvailableRoomsByHotelId(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    List<RoomDto> findRoomsByHotelId(@Param("hotelId") Long hotelId);

    // ReviewsDto로 변경
    List<ReviewsDto> findReviewsByHotelId(
            @Param("hotelId") Long hotelId,
            @Param("offset") long offset,
            @Param("size") int size
    );

    List<FreebiesDto> findFreebiesOptions();

    List<AmenitiesDto> findAmenitiesOptions();

    PriceRangeDto findPriceRange();

    Long countByType(String hotelType);
}