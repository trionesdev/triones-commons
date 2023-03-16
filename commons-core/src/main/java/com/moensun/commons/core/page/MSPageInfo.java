package com.moensun.commons.core.page;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class MSPageInfo<T> extends PageInfo<T> {
    private static final long serialVersionUID = 7156771088266349472L;
    private int pageNum;
    private int pageSize;
    private long total;
    private int pages;
    private List<T> rows = Collections.emptyList();
}
