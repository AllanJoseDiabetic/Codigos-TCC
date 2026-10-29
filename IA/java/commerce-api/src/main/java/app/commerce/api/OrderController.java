package app.commerce.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.commerce.api.ApiDtos.OrderView;
import app.commerce.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderView>> findAll() {
        return ResponseEntity.ok(
            orderService.findAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderView> findById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            orderService.findById(id)
        );
    }
}