package com.hotel_project.hotel_jpa.hotel_image.controller;

import com.hotel_project.common_jpa.dto.PublicSearchDto;
import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.city_image.dto.CityImageDto;
import com.hotel_project.hotel_jpa.city_image.dto.CityImageViewDto;
import com.hotel_project.hotel_jpa.city_image.service.CityImageService;
import com.hotel_project.hotel_jpa.hotel_image.dto.HotelImageDto;
import com.hotel_project.hotel_jpa.hotel_image.dto.HotelImageViewDto;
import com.hotel_project.hotel_jpa.hotel_image.service.HotelImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/hotel_images")
@Tag(name = "Hotel Images API", description = "호텔 이미지 관리 API")
public class HotelImageController {

    @Autowired
    private HotelImageService hotelImageService;

    @GetMapping
    @Operation(summary = "호텔 이미지 검색", description = "호텔 이미지명으로 검색합니다. 검색어가 없으면 전체 조회")
    public ResponseEntity<ApiResponse<Page<HotelImageViewDto>>> findByName(
    PublicSearchDto publicSearchDto) {

        Pageable pageable = PageRequest.of(publicSearchDto.getPage(), publicSearchDto.getSize());
        Page<HotelImageViewDto> hotelImages = hotelImageService.findByName(pageable, publicSearchDto.getSearch());
        return ResponseEntity.ok(ApiResponse.success(200, "success", hotelImages));
    }

    @GetMapping("/{id}")
    @Operation(summary = "호텔 이미지 단건 조회", description = "ID로 호텔 이미지를 조회합니다.")
    public ResponseEntity<ApiResponse<HotelImageDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        HotelImageDto hotelImageDto = hotelImageService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", hotelImageDto));
    }

    @PostMapping
    @Operation(summary = "호텔 이미지 등록", description = "새로운 호텔 이미지를 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody HotelImageDto hotelImageDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        String result = hotelImageService.insert(hotelImageDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "호텔 이미지 수정", description = "기존 호텔 이미지 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, @RequestBody HotelImageDto hotelImageDto) throws CommonExceptionTemplate {
        hotelImageDto.setId(id);
        String result = hotelImageService.update(hotelImageDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "호텔 이미지 삭제", description = "호텔 이미지를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = hotelImageService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }


}