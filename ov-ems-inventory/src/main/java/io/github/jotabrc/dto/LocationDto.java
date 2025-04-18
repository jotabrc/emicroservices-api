package io.github.jotabrc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Getter
@Builder
@AllArgsConstructor
public class LocationDto {

    private final String uuid;
    private final String name;
    private final long inventoryId;
    private final List<ItemDto> items;
}
