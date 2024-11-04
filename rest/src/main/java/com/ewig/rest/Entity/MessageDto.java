package com.ewig.rest.Entity;

public record MessageDto(
        MessageType messageType,
        String message
) {

}
