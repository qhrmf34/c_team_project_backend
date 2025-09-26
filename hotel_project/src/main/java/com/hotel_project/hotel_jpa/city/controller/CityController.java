package com.hotel_project.hotel_jpa.city.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.city.dto.CityDto;
import com.hotel_project.hotel_jpa.city.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admin/cities")
@Tag(name = "Cities API", description = "도시 관리 API")
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping
    @Operation(summary = "전체 도시 조회", description = "모든 도시를 조회합니다.")
    public ResponseEntity<ApiResponse<Page<CityDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CityDto> cities = cityService.findAll(pageable, search);
        return ResponseEntity.ok(ApiResponse.success(200, "success", cities));
    }

    @GetMapping("/{id}")
    @Operation(summary = "도시 단건 조회", description = "ID로 도시를 조회합니다.")
    public ResponseEntity<ApiResponse<CityDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        CityDto cityDto = cityService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", cityDto));
    }

    @GetMapping("/search")
    @Operation(summary = "도시 이름으로 검색", description = "도시 이름으로 검색합니다.")
    public ResponseEntity<ApiResponse<List<CityDto>>> findByName(@RequestParam String name) throws CommonExceptionTemplate {
        List<CityDto> cities = cityService.findByName(name);
        return ResponseEntity.ok(ApiResponse.success(200, "success", cities));
    }

    @PostMapping
    @Operation(summary = "도시 등록", description = "새로운 도시를 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody CityDto cityDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        String result = cityService.insert(cityDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "도시 수정", description = "기존 도시 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, @RequestBody CityDto cityDto) throws CommonExceptionTemplate {
        cityDto.setId(id);
        String result = cityService.update(cityDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "도시 삭제", description = "도시를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = cityService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }
}