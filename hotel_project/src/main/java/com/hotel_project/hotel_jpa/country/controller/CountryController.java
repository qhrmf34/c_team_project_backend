package com.hotel_project.hotel_jpa.country.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.country.dto.CountryDto;
import com.hotel_project.hotel_jpa.country.service.CountryService;
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

@RestController
@RequestMapping("/api/admin/countries")
@Tag(name = "Countries API", description = "국가 관리 API")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @GetMapping
    @Operation(summary = "국가 검색", description = "국가명으로 검색합니다. 검색어가 없으면 전체 조회")
    public ResponseEntity<ApiResponse<Page<CountryDto>>> findByName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CountryDto> countries = countryService.findByName(pageable, search);
        return ResponseEntity.ok(ApiResponse.success(200, "success", countries));
    }

    // 이제 search endpoint는 제거됨 (findByName으로 통합)

    @GetMapping("/{id}")
    @Operation(summary = "국가 단건 조회", description = "ID로 국가를 조회합니다.")
    public ResponseEntity<ApiResponse<CountryDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        CountryDto countryDto = countryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", countryDto));
    }

    @PostMapping
    @Operation(summary = "국가 등록", description = "새로운 국가를 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody CountryDto countryDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        String result = countryService.insert(countryDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "국가 수정", description = "기존 국가 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, @RequestBody CountryDto countryDto) throws CommonExceptionTemplate {
        countryDto.setId(id);
        String result = countryService.update(countryDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "국가 삭제", description = "국가를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = countryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }
}