package app.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.commerce.domain.Product;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    List<Product> findAllByOrderByIdAsc();

}