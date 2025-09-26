package com.hotel_project.hotel_jpa.city_image.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.city_image.dto.CityImageDto;
import com.hotel_project.hotel_jpa.city_image.service.CityImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/city_images")
@Tag(name = "City Images API", description = "도시 이미지 관리 API")
public class CityImageController {

    @Autowired
    private CityImageService cityImageService;

    @GetMapping
    @Operation(summary = "전체 도시 이미지 조회", description = "모든 도시 이미지를 조회합니다.")
    public ResponseEntity<ApiResponse<Page<CityImageDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CityImageDto> cityImages = cityImageService.findAll(pageable, search);
        return ResponseEntity.ok(ApiResponse.success(200, "success", cityImages));
    }

    @GetMapping("/{id}")
    @Operation(summary = "도시 이미지 단건 조회", description = "ID로 도시 이미지를 조회합니다.")
    public ResponseEntity<ApiResponse<CityImageDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        CityImageDto cityImageDto = cityImageService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", cityImageDto));
    }

    @GetMapping("/search")
    @Operation(summary = "도시 이미지 검색", description = "도시 이름으로 이미지를 검색합니다.")
    public ResponseEntity<ApiResponse<List<CityImageDto>>> findByName(@RequestParam String name) throws CommonExceptionTemplate {
        List<CityImageDto> cityImages = cityImageService.findByName(name);
        return ResponseEntity.ok(ApiResponse.success(200, "success", cityImages));
    }

    @PostMapping
    @Operation(summary = "도시 이미지 등록", description = "새로운 도시 이미지를 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody CityImageDto cityImageDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        String result = cityImageService.insert(cityImageDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "도시 이미지 수정", description = "기존 도시 이미지 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, @RequestBody CityImageDto cityImageDto) throws CommonExceptionTemplate {
        cityImageDto.setId(id);
        String result = cityImageService.update(cityImageDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "도시 이미지 삭제", description = "도시 이미지를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = cityImageService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PostMapping("/upload")
    @Operation(summary = "파일 업로드", description = "이미지 파일을 업로드합니다.")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) throws CommonExceptionTemplate {
        String filePath = cityImageService.uploadFile(file);
        return ResponseEntity.ok(ApiResponse.success(200, "Upload successful", filePath));
    }
}