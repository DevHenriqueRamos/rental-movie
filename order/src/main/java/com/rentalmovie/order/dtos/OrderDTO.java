package com.rentalmovie.order.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {

    @NotNull
    private Set<UUID> moviesIds;

    @NotBlank
    private String paymentMethodId;
}
