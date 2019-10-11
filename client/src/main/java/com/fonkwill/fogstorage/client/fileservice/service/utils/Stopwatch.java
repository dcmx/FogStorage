package com.fonkwill.fogstorage.client.fileservice.service.utils;

import java.time.Instant;

public class Stopwatch {

    private Long start;


    /**
     * Instantiates the stopwatch and starts it
     */
    public Stopwatch() {
        this.start = Instant.now().toEpochMilli();

    }

    /**
     * Stops the stopwatch and returns the resulting time
     * @return The difference between start and stop
     */
    public Long stop() {
        Long end = Instant.now().toEpochMilli();
        return  end - start;
    }
}
