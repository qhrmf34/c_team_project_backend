package com.hotel_project.hotel_jpa.freebies.sevice;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesEntity;
import com.hotel_project.hotel_jpa.freebies.dto.IFreebies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@Service
public class FreebiesService {
    @Autowired
    private FreebiesMapper mapper;

    @Autowired
    private FreebiesRepository repository;

    public IFreebies insert(FreebiesDto dto) {
        FreebiesEntity entity = new FreebiesEntity();
        entity.copyMembers(dto);
        FreebiesEntity result = this.repository.save(entity);
        return result;
    }

    public List<FreebiesEntity> findAll() {
        List<FreebiesEntity> all = this.repository.findAll();
        return all;
    }
}
