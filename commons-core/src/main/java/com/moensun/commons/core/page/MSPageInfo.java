package com.moensun.commons.core.page;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MSPageInfo<T> extends PageInfo<T> {

}
