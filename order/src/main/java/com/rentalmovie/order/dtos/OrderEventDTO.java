package com.rentalmovie.order.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEventDTO {

    private UUID orderId;
    private UUID userId;
    private Set<UUID> movieIds;
}
