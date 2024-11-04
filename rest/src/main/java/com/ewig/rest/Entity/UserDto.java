package com.ewig.rest.Entity;

import java.time.LocalDate;

public record UserDto(
        long id,
        String fullName,
        String email,
        String phone
) {
}
