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
import java.util.List;

@RestController
@RequestMapping("/api/admin/countries")
@Tag(name = "Countries API", description = "국가 관리 API")
public class CountryController {

    @Autowired
    private CountryService countryService;

    @GetMapping
    @Operation(summary = "전체 국가 조회", description = "모든 국가를 조회합니다.")
    public ResponseEntity<ApiResponse<Page<CountryDto>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CountryDto> countries = countryService.findAll(pageable, search);
        return ResponseEntity.ok(ApiResponse.success(200, "success", countries));
    }

    @GetMapping("/{id}")
    @Operation(summary = "국가 단건 조회", description = "ID로 국가를 조회합니다.")
    public ResponseEntity<ApiResponse<CountryDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        CountryDto countryDto = countryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", countryDto));
    }

    @GetMapping("/search")
    @Operation(summary = "국가 이름으로 검색", description = "국가 이름으로 검색합니다.")
    public ResponseEntity<ApiResponse<List<CountryDto>>> findByName(@RequestParam String name) throws CommonExceptionTemplate {
        List<CountryDto> countries = countryService.findByName(name);
        return ResponseEntity.ok(ApiResponse.success(200, "success", countries));
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