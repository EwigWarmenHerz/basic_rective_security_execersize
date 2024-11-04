package com.ewig.database.entity;

public record MessageDto(
        MessageType messageType,
        String message
) {

}
