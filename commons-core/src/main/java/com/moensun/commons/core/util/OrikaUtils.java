package com.moensun.commons.core.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class OrikaUtils {
    public static MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
    public static MapperFacade mapperFacade = mapperFactory.getMapperFacade();
}
