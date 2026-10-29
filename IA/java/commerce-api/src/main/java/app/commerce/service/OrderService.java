package app.commerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.commerce.api.ApiDtos.OrderView;
import app.commerce.api.ApiMapper;
import app.commerce.api.ResourceNotFoundException;
import app.commerce.domain.PurchaseOrder;
import app.commerce.repository.OrderRepository;

@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderView> findAll() {
        return orderRepository
            .findAllByOrderByIdAsc()
            .stream()
            .map(ApiMapper::toOrderView)
            .collect(Collectors.toList());
    }

    public OrderView findById(Long id) {
        PurchaseOrder order = orderRepository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "Pedido nao encontrado: " + id
                )
            );

        return ApiMapper.toOrderView(order);
    }
}