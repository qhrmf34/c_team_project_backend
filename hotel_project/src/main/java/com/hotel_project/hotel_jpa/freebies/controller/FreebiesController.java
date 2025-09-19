package com.hotel_project.hotel_jpa.freebies.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.IFreebies;
import com.hotel_project.hotel_jpa.freebies.service.FreebiesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/freebies")
public class FreebiesController {

    @Autowired
    private FreebiesService freebiesService;

    // CREATE - POST /api/v1/freebies (JPA)
    @PostMapping("")
    public IFreebies createFreebies(@Validated @RequestBody FreebiesDto dto) throws CommonExceptionTemplate {
        log.info("Creating freebies: {}", dto.getFreebiesName());
        IFreebies created = freebiesService.insertRepository(dto);
        // CommonResponseController가 자동으로 ApiResponse로 감싸줌
        // 성공: {"code": 200, "message": "success", "data": {무료시설정보}}
        // 실패: MemberExceptionAdvice가 처리 → {"code": 400400, "message": "그런 아이디 없음"}
        return created;
    }

    // UPDATE - PATCH /api/v1/freebies/{id} (JPA)
    @PatchMapping("/{id}")
    public IFreebies updateFreebies(
            @PathVariable Long id,
            @Validated @RequestBody FreebiesDto dto) throws CommonExceptionTemplate {
        log.info("Updating freebies ID: {} with name: {}", id, dto.getFreebiesName());
        IFreebies updated = freebiesService.updateRepository(id, dto);
        return updated;
    }

    // DELETE - DELETE /api/v1/freebies/{id} (JPA)
    @DeleteMapping("/{id}")
    public String deleteFreebies(@PathVariable Long id) throws CommonExceptionTemplate {
        log.info("Deleting freebies ID: {}", id);
        Boolean result = freebiesService.deleteRepository(id);
        return result ? "무료시설이 성공적으로 삭제되었습니다." : "삭제에 실패했습니다.";
    }

    // READ - GET /api/v1/freebies/{id} (MyBatis)
    @GetMapping("/{id}")
    public IFreebies getFreebies(@PathVariable Long id) throws CommonExceptionTemplate {
        log.info("Finding freebies ID: {}", id);
        IFreebies freebies = freebiesService.findByIdMybatis(id);
        return freebies;
    }

    // READ ALL - GET /api/v1/freebies (MyBatis)
    @GetMapping("")
    public List<IFreebies> getAllFreebies() {
        log.info("Finding all freebies");
        List<IFreebies> freebiesList = freebiesService.findAllMybatis();
        return freebiesList;
    }

    // SEARCH - GET /api/v1/freebies/search (MyBatis + Slice)
    @GetMapping("/search")
    public Slice<FreebiesDto> searchFreebies(
            @RequestParam("name") String freebiesName,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Searching freebies with name containing: {}", freebiesName);
        Slice<FreebiesDto> result = freebiesService.findByNameContainsMybatis(freebiesName, pageable);
        return result;
    }
}