package com.hotel_project.hotel_jpa.freebies.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.freebies.service.FreebiesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/freebies")
@RequiredArgsConstructor
@Api(tags = "Freebies API", description = "무료시설 관리 API")
public class FreebiesController {

    private final FreebiesService freebiesService;

    @GetMapping
    @ApiOperation(value = "전체 무료시설 조회", notes = "모든 무료시설을 조회합니다.")
    public ResponseEntity<ApiResponse<List<FreebiesDto>>> findAll() {
        List<FreebiesDto> freebiesList = freebiesService.findAll();
        return ResponseEntity.ok(ApiResponse.success(200, "success", freebiesList));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "무료시설 단건 조회", notes = "ID로 무료시설을 조회합니다.")
    public ResponseEntity<ApiResponse<FreebiesDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        FreebiesDto freebiesDto = freebiesService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", freebiesDto));
    }

    @GetMapping("/search")
    @ApiOperation(value = "무료시설 이름으로 검색", notes = "무료시설 이름으로 검색합니다.")
    public ResponseEntity<ApiResponse<List<FreebiesDto>>> findByName(@RequestParam String name) throws CommonExceptionTemplate {
        List<FreebiesDto> freebiesList = freebiesService.findByName(name);
        return ResponseEntity.ok(ApiResponse.success(200, "success", freebiesList));
    }

    @PostMapping
    @ApiOperation(value = "무료시설 등록", notes = "새로운 무료시설을 등록합니다.")
    public String save(@Valid @RequestBody FreebiesDto freebiesDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" ")
            );
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        return freebiesService.save(freebiesDto);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "무료시설 수정", notes = "기존 무료시설 정보를 수정합니다.")
    public String update(@PathVariable Long id, @RequestBody FreebiesDto freebiesDto) throws CommonExceptionTemplate {
        freebiesDto.setId(id);
        return freebiesService.update(freebiesDto);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "무료시설 삭제", notes = "무료시설을 삭제합니다.")
    public String delete(@PathVariable Long id) throws CommonExceptionTemplate {
        return freebiesService.delete(id);
    }
}