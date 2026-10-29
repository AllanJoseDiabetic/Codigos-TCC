package app.commerce.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.commerce.api.ApiDtos.CategoryView;
import app.commerce.api.ApiDtos.ProductView;
import app.commerce.api.ApiMapper;
import app.commerce.api.ResourceNotFoundException;

import app.commerce.domain.Category;
import app.commerce.domain.Product;

import app.commerce.repository.CategoryRepository;
import app.commerce.repository.ProductRepository;

@Service
@Transactional(readOnly = true)
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CatalogService(
        CategoryRepository categoryRepository,
        ProductRepository productRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<CategoryView> findAllCategories() {
        return categoryRepository
            .findAllByOrderByIdAsc()
            .stream()
            .map(ApiMapper::toCategoryView)
            .collect(Collectors.toList());
    }

    public CategoryView findCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "Categoria nao encontrada: " + id
                )
            );

        return ApiMapper.toCategoryView(category);
    }

    public List<ProductView> findAllProducts() {
        return productRepository
            .findAllByOrderByIdAsc()
            .stream()
            .map(ApiMapper::toProductView)
            .collect(Collectors.toList());
    }

    public ProductView findProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException(
                    "Produto nao encontrado: " + id
                )
            );

        return ApiMapper.toProductView(product);
    }
}