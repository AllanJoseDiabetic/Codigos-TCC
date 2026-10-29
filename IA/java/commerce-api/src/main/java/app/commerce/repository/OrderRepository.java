package app.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.commerce.domain.PurchaseOrder;

public interface OrderRepository
        extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findAllByOrderByIdAsc();

    boolean existsByCustomerId(Long customerId);

}