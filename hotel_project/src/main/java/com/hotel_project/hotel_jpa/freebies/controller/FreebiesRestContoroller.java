package com.hotel_project.hotel_jpa.freebies.controller;

import com.hotel_project.common_jpa.CommonRestController;
import com.hotel_project.common_jpa.dto.ResponseCode;
import com.hotel_project.common_jpa.dto.ResponseDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesEntity;
import com.hotel_project.hotel_jpa.freebies.dto.IFreebies;
import com.hotel_project.hotel_jpa.freebies.sevice.FreebiesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/test")
public class FreebiesRestContoroller extends CommonRestController {
    @Autowired
    private FreebiesService service;

    public FreebiesRestContoroller(FreebiesService service) {
        this.service = service;
    }

    @PostMapping("")
    public ResponseEntity<ResponseDto> insert(@Validated @RequestBody FreebiesDto dto) {
        try {
            IFreebies freebies= this.service.insert(dto);
            return this.getReponseEntity(ResponseCode.SUCCESS, "Ok", freebies, null);
        } catch (Throwable t) {
            log.error(t.toString());
            return this.getReponseEntity(ResponseCode.INSERT_FAIL,"Error", dto, t);
        }
    }

    @GetMapping("/freebies")
    public ResponseEntity<ResponseDto> findAll() {
        try{
            List<FreebiesEntity> all = this.service.findAll();
            return this.getReponseEntity(ResponseCode.SUCCESS, "Ok", all, null);
        } catch (Throwable t) {
            log.error(t.toString());
            return this.getReponseEntity(ResponseCode.INSERT_FAIL,"Error", null, t);
        }
    }
}
