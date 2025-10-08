package com.hotel_project.hotel_jpa.hotel.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.hotel.dto.*;
import com.hotel_project.hotel_jpa.hotel.mapper.HotelPublicMapper;
import com.hotel_project.hotel_jpa.hotel.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelPublicService {

    private final HotelPublicMapper hotelPublicMapper;
    private final HotelRepository hotelRepository;

    public HotelSearchResponseDto searchHotels(HotelSearchRequestDto request, Pageable pageable) {
        log.info("호텔 검색 시작 - destination: {}, hotelType: {}, page: {}",
                request.getDestination(), request.getHotelType(), pageable.getPageNumber());

        List<HotelSummaryDto> hotels = hotelPublicMapper.searchHotels(
                request.getDestination(),
                request.getCheckIn(),
                request.getCheckOut(),
                request.getGuests(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getRating(),
                request.getHotelType(),
                request.getFreebies(),
                request.getAmenities(),
                request.getSortBy(),
                pageable.getOffset(),
                pageable.getPageSize()
        );

        Long totalCount = hotelPublicMapper.countSearchHotels(
                request.getDestination(),
                request.getCheckIn(),
                request.getCheckOut(),
                request.getGuests(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getRating(),
                request.getHotelType(),
                request.getFreebies(),
                request.getAmenities()
        );

        for (HotelSummaryDto hotel : hotels) {
            hotel.setFreebies(hotelPublicMapper.findFreebiesByHotelId(hotel.getId()));
            hotel.setAmenities(hotelPublicMapper.findAmenitiesByHotelId(hotel.getId()));
            hotel.setRatingText(getRatingText(hotel.getRating()));
            hotel.setType(getStarTypeText(hotel.getStars()));
            hotel.setCurrency("KRW");

            if (hotel.getImage() == null || hotel.getImage().isEmpty()) {
                hotel.setImage("/images/hotel_img/hotel1.jpg");
            }

            hotel.setWishlisted(false);
        }

        return HotelSearchResponseDto.builder()
                .hotels(hotels)
                .totalCount(totalCount)
                .currentPage(pageable.getPageNumber())
                .totalPages((int) Math.ceil((double) totalCount / pageable.getPageSize()))
                .pageSize(pageable.getPageSize())
                .build();
    }

    public HotelDetailDto getHotelDetail(Long hotelId, LocalDate checkIn, LocalDate checkOut) throws CommonExceptionTemplate {
        if (hotelId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        if (!hotelRepository.existsById(hotelId)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        HotelDetailDto hotel = hotelPublicMapper.findHotelDetailById(hotelId);

        // 편의시설 조회
        hotel.setFreebies(hotelPublicMapper.findFreebiesByHotelId(hotelId));
        hotel.setAmenities(hotelPublicMapper.findAmenitiesByHotelId(hotelId));

        // 이미지 조회
        hotel.setImages(hotelPublicMapper.findImagesByHotelId(hotelId));

        // 객실 정보 조회 (RoomDto 사용)
        if (checkIn != null && checkOut != null) {
            hotel.setRooms(hotelPublicMapper.findAvailableRoomsByHotelId(hotelId, checkIn, checkOut));
        } else {
            hotel.setRooms(hotelPublicMapper.findRoomsByHotelId(hotelId));
        }

        // 리뷰 조회 (ReviewsDto 사용)
        hotel.setReviews(hotelPublicMapper.findReviewsByHotelId(hotelId, 0, 10));

        hotel.setWishlisted(false);

        return hotel;
    }

    public FilterOptionsDto getFilterOptions() {
        List<FreebiesDto> freebies = hotelPublicMapper.findFreebiesOptions();
        List<AmenitiesDto> amenities = hotelPublicMapper.findAmenitiesOptions();

        PriceRangeDto priceRange = hotelPublicMapper.findPriceRange();
        if (priceRange == null) {
            priceRange = PriceRangeDto.builder()
                    .min(new BigDecimal("50000"))
                    .max(new BigDecimal("1200000"))
                    .build();
        }

        List<String> hotelTypes = Arrays.asList("hotel", "motel", "resort");

        Map<String, Long> hotelTypeCounts = new HashMap<>();
        hotelTypeCounts.put("hotel", hotelPublicMapper.countByType("hotel"));
        hotelTypeCounts.put("motel", hotelPublicMapper.countByType("motel"));
        hotelTypeCounts.put("resort", hotelPublicMapper.countByType("resort"));

        return FilterOptionsDto.builder()
                .freebies(freebies)
                .amenities(amenities)
                .priceRange(priceRange)
                .hotelTypes(hotelTypes)
                .hotelTypeCounts(hotelTypeCounts)
                .build();
    }

    private String getRatingText(BigDecimal rating) {
        if (rating == null) {
            return "No Rating";
        }

        double ratingValue = rating.doubleValue();
        if (ratingValue >= 4.5) {
            return "Excellent";
        } else if (ratingValue >= 4.0) {
            return "Very Good";
        } else if (ratingValue >= 3.5) {
            return "Good";
        } else if (ratingValue >= 3.0) {
            return "Average";
        } else {
            return "Below Average";
        }
    }

    private String getStarTypeText(Integer stars) {
        if (stars == null || stars == 0) {
            return "Hotel";
        }
        return stars + " Star Hotel";
    }
}