package com.rentalmovie.order.controllers;

import com.rentalmovie.order.configs.security.AuthenticationCurrentUserService;
import com.rentalmovie.order.dtos.OrderDTO;
import com.rentalmovie.order.enums.OrderStatus;
import com.rentalmovie.order.models.MovieModel;
import com.rentalmovie.order.models.OrderModel;
import com.rentalmovie.order.services.MovieService;
import com.rentalmovie.order.services.OrderService;
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

import static com.rentalmovie.order.utils.ResponseUtils.createMessageResponse;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    private final MovieService movieService;

    private final AuthenticationCurrentUserService authenticationCurrentUserService;

    public OrderController(OrderService orderService, MovieService movieService, AuthenticationCurrentUserService authenticationCurrentUserService) {
        this.orderService = orderService;
        this.movieService = movieService;
        this.authenticationCurrentUserService = authenticationCurrentUserService;
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @PostMapping
    public ResponseEntity<Object> createOrder(@RequestBody OrderDTO orderDTO) {
        UUID userId = authenticationCurrentUserService.getCurrentUser().getUserId();

        if (verifyIfAlreadyInProgressOrderToUser(userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createMessageResponse("Already in progress order to this user!"));
        }

        Set<MovieModel> movieModelSet = movieService.getMovieSet(orderDTO.getMoviesIds());
        if (movieModelSet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createMessageResponse("Movies not found!"));
        }

        BigDecimal totalPrice = calculateTotalPrice(movieModelSet);

        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setMovies(movieModelSet);
        orderModel.setTotalPrice(totalPrice);
        orderModel.setStatus(OrderStatus.IN_PROGRESS);
        orderModel.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));

        OrderModel processedOrder = orderService.processOrder(orderModel, orderDTO.getPaymentMethodId());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(processedOrder);
    }

    private boolean verifyIfAlreadyInProgressOrderToUser(UUID userId) {
        Optional<OrderModel> inProgressOrderModel = orderService.findLastUserOrder(userId);
        return inProgressOrderModel.isPresent() && inProgressOrderModel.get().getStatus().equals(OrderStatus.IN_PROGRESS);
    }

    private BigDecimal calculateTotalPrice(Set<MovieModel> movieModelSet) {
        return movieModelSet.stream()
                .map(MovieModel::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping
    public ResponseEntity<Page<OrderModel>> getAllOrders(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID userId = authenticationCurrentUserService.getCurrentUser().getUserId();
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findAllByUserId(userId, pageable));
    }

    @PreAuthorize("hasAnyRole('CONSUMER')")
    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getOrderById(@PathVariable UUID orderId) {
        UUID userId = authenticationCurrentUserService.getCurrentUser().getUserId();
        OrderModel orderModel = orderService.findByOrderIdAndUserId(orderId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(orderModel);
    }
}
