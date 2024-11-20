package com.rentalmovie.order.controllers;

import com.rentalmovie.order.configs.security.AuthenticationCurrentUserService;
import com.rentalmovie.order.dtos.OrderDTO;
import com.rentalmovie.order.enums.OrderStatus;
import com.rentalmovie.order.models.MovieModel;
import com.rentalmovie.order.models.OrderModel;
import com.rentalmovie.order.services.MovieService;
import com.rentalmovie.order.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    MovieService movieService;

    @Autowired
    AuthenticationCurrentUserService authenticationCurrentUserService;

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody OrderDTO orderDTO) {
        UUID userId = authenticationCurrentUserService.getCurrentUser().getUserId();
        Set<MovieModel> movieModelSet = movieService.getMovieSet(orderDTO.getMoviesIds());
        if (movieModelSet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Movies not found!");
        }
        BigDecimal totalPrice = movieModelSet.stream()
                .map(MovieModel::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setMovies(movieModelSet);
        orderModel.setTotalPrice(totalPrice);
        orderModel.setStatus(OrderStatus.IN_PROGRESS);
        orderModel.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.save(orderModel));
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping
    public ResponseEntity<Page<OrderModel>> getAllOrders(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID userId = authenticationCurrentUserService.getCurrentUser().getUserId();
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findAllByUserId(userId, pageable));
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderById(@PathVariable UUID orderId) {
        UUID userId = authenticationCurrentUserService.getCurrentUser().getUserId();
        Optional<OrderModel> orderModelOptional = orderService.findByOrderIdAndUserId(orderId, userId);
        if(orderModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderModelOptional.get());
    }
}
