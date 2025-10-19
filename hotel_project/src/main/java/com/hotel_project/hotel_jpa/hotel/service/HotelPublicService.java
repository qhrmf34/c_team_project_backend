package com.hotel_project.hotel_jpa.hotel.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.hotel.dto.*;
import com.hotel_project.hotel_jpa.hotel.mapper.HotelPublicMapper;
import com.hotel_project.hotel_jpa.hotel.repository.HotelRepository;
import com.hotel_project.member_jpa.cart.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelPublicService {

    private final HotelPublicMapper hotelPublicMapper;
    private final HotelRepository hotelRepository;
    private final CartMapper cartMapper;

    public HotelSearchResponseDto searchHotels(HotelSearchRequestDto request, Long memberId) {
        log.info("호텔 검색 시작 - destination: {}, hotelType: {}, offset: {}, size: {}, memberId: {}",
                request.getDestination(), request.getHotelType(), request.getOffset(), request.getSize(), memberId);

        // ✅ 모든 호텔 조회 (예약 가능 + 예약 마감)
        List<HotelSummaryDto> hotels = hotelPublicMapper.searchHotels(request);

        // ✅ 전체 호텔 개수 (페이지네이션용)
        Long totalCount = hotelPublicMapper.countSearchHotels(request);

        // ✅ 예약 가능한 호텔만 카운트 (showing-count용)
        Long availableCount = hotelPublicMapper.countAvailableHotels(request);

        log.info("검색 결과 - hotels.size(): {}, totalCount: {}, availableCount: {}",
                hotels.size(), totalCount, availableCount);

        // 현재 검색 조건으로 호텔 타입별 카운트 조회 (예약 가능만)
        Map<String, Long> hotelTypeCounts = new HashMap<>();

        // 원본 hotelType 백업
        String originalHotelType = request.getHotelType();

        // 각 타입별로 현재 검색 조건을 적용하여 카운트 (예약 가능만)
        for (String type : Arrays.asList("hotel", "motel", "resort")) {
            request.setHotelType(type);
            Long count = hotelPublicMapper.countAvailableHotels(request);
            hotelTypeCounts.put(type, count);
            log.info("호텔 타입 카운트 - {}: {}", type, count);
        }

        // 원본 hotelType 복원
        request.setHotelType(originalHotelType);

        // 로그인 사용자 찜 목록 조회
        List<Long> wishlistedHotelIds = new ArrayList<>();
        if (memberId != null) {
            wishlistedHotelIds = cartMapper.findHotelIdsByMemberId(memberId);
        }

        // 호텔 상세 정보 보완
        for (HotelSummaryDto hotel : hotels) {
            hotel.setFreebies(hotelPublicMapper.findFreebiesByHotelId(hotel.getId()));
            hotel.setAmenities(hotelPublicMapper.findAmenitiesByHotelId(hotel.getId()));
            hotel.setRatingText(getRatingText(hotel.getRating()));
            hotel.setType(getStarTypeText(hotel.getStars()));
            hotel.setCurrency("KRW");

            if (hotel.getImage() == null || hotel.getImage().isEmpty()) {
                hotel.setImage("/images/hotel_img/hotel1.jpg");
            }

            hotel.setWishlisted(wishlistedHotelIds.contains(hotel.getId()));
        }

        return HotelSearchResponseDto.builder()
                .hotels(hotels)
                .totalCount(totalCount)              // ✅ 전체 호텔 (페이지네이션용)
                .availableCount(availableCount)      // ✅ 예약 가능한 호텔만 (showing-count용)
                .currentPage(0)
                .totalPages(1)
                .pageSize(hotels.size())
                .hotelTypeCounts(hotelTypeCounts)    // ✅ 예약 가능한 호텔만
                .build();
    }

    public HotelDetailDto getHotelDetail(Long hotelId, LocalDate checkIn, LocalDate checkOut, Long memberId) throws CommonExceptionTemplate {
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

        // 객실 정보 조회
        if (checkIn != null && checkOut != null) {
            hotel.setRooms(hotelPublicMapper.findAvailableRoomsByHotelId(hotelId, checkIn, checkOut));
        } else {
            hotel.setRooms(hotelPublicMapper.findRoomsByHotelId(hotelId));
        }

        // 리뷰 조회
        hotel.setReviews(hotelPublicMapper.findReviewsByHotelId(hotelId, 0L));

        // 찜 상태 조회
        if (memberId != null) {
            int count = cartMapper.existsByMemberIdAndHotelId(memberId, hotelId);
            hotel.setWishlisted(count > 0);
        } else {
            hotel.setWishlisted(false);
        }

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

    public List<HotelSummaryDto> getWishlistHotels(Long memberId) {
        log.info("찜한 호텔 목록 조회 - memberId: {}", memberId);

        List<HotelSummaryDto> hotels = hotelPublicMapper.findWishlistHotelsByMemberId(memberId);

        for (HotelSummaryDto hotel : hotels) {
            hotel.setFreebies(hotelPublicMapper.findFreebiesByHotelId(hotel.getId()));
            hotel.setAmenities(hotelPublicMapper.findAmenitiesByHotelId(hotel.getId()));
            hotel.setRatingText(getRatingText(hotel.getRating()));
            hotel.setType(getStarTypeText(hotel.getStars()));
            hotel.setCurrency("KRW");
            hotel.setWishlisted(true);

            if (hotel.getImage() == null || hotel.getImage().isEmpty()) {
                hotel.setImage("/images/hotel_img/hotel1.jpg");
            }
        }

        return hotels;
    }
}