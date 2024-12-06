package com.rentalmovie.movie.testutils;

import lombok.experimental.UtilityClass;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@UtilityClass
public class CreatorUtil {
    private static final PodamFactory factory = new PodamFactoryImpl();

    public static <T> T generateMock(final Class<T> clazz) {
        return factory.manufacturePojo(clazz);
    }
}
