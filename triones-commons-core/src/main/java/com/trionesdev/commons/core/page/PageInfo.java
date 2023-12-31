package com.trionesdev.commons.core.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@SuperBuilder
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = 7156771088266349472L;
    private int pageNum;
    private int pageSize;
    private long total;
    private int pages;
    private List<T> rows = Collections.emptyList();
}
