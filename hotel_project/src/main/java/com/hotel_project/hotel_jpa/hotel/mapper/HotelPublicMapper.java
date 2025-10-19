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

    List<HotelSummaryDto> searchHotels(HotelSearchRequestDto request);

    Long countSearchHotels(HotelSearchRequestDto request);

    HotelDetailDto findHotelDetailById(@Param("hotelId") Long hotelId);

    List<FreebiesDto> findFreebiesByHotelId(@Param("hotelId") Long hotelId);

    List<AmenitiesDto> findAmenitiesByHotelId(@Param("hotelId") Long hotelId);

    List<String> findImagesByHotelId(@Param("hotelId") Long hotelId);

    List<RoomDto> findAvailableRoomsByHotelId(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    List<RoomDto> findRoomsByHotelId(@Param("hotelId") Long hotelId);

    List<ReviewsDto> findReviewsByHotelId(
            @Param("hotelId") Long hotelId,
            @Param("offset") long offset
    );

    List<FreebiesDto> findFreebiesOptions();

    List<AmenitiesDto> findAmenitiesOptions();

    PriceRangeDto findPriceRange();

    Long countByTypeWithFilters(HotelSearchRequestDto request);

    List<HotelSummaryDto> findWishlistHotelsByMemberId(@Param("memberId") Long memberId);

    void updateHotelRating(@Param("hotelId") Long hotelId);

    Long countAvailableHotels(HotelSearchRequestDto request);
}