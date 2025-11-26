package com.trionesdev.commons.core.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Deprecated
public class OrikaUtils {
    private static MapperFactory MAPPER_FACTORY;
    private static MapperFacade MAPPER_FACADE;

    public static void setMapperFactory(MapperFactory mapperFactory) {
        OrikaUtils.MAPPER_FACTORY = mapperFactory;
    }

    public static MapperFactory getMapperFactory() {
        if (MAPPER_FACTORY == null) {
            MAPPER_FACTORY = new DefaultMapperFactory.Builder().build();
        }
        return MAPPER_FACTORY;
    }

    public static void setMapperFacade(MapperFacade mapperFacade) {
        OrikaUtils.MAPPER_FACADE = mapperFacade;
    }

    public static MapperFacade getMapperFacade() {
        if (MAPPER_FACADE == null) {
            return getMapperFactory().getMapperFacade();
        }
        return MAPPER_FACADE;
    }

}
