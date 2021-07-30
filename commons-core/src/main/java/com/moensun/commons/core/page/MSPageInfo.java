package com.moensun.commons.core.page;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MSPageInfo<T> implements Serializable {
    private int pageNum;
    private int pageSize;
    private long total;
    private int pages;
    private List<T> rows;
}
