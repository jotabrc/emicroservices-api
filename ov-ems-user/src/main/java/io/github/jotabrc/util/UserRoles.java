package io.github.jotabrc.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRoles {

    SYSTEM("SYSTEM"),
    ADMIN("ADMIN"),
    USER("USER"),
    GUEST("GUEST");

    private final String name;
}
