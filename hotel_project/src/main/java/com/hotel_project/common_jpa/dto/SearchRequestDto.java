package com.hotel_project.common_jpa.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequestDto {
    private String searchType;
    private String searchWord;
    private Integer page;
    private Integer size;

    public int getPage() {
        return page == null || page <= 0 ? 1 : page;
    }

    public int getSize() {
        return size == null || size <= 0 ? 10 : size;
    }

    public int getOffset() {
        return (getPage() - 1) * getSize();
    }
}
