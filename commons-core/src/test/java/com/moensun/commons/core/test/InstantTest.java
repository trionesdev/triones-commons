package com.moensun.commons.core.test;

import org.junit.jupiter.api.Test;

import java.time.Instant;

public class InstantTest {

    @Test
    public void t(){
        Instant.ofEpochSecond(Instant.now().getEpochSecond());
    }

}
