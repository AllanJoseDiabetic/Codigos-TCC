package app.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import app.commerce.domain.Category;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByIdAsc();

}