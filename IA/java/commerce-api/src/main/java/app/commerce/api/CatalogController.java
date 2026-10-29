package app.commerce.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import app.commerce.api.ApiDtos.CategoryView;
import app.commerce.api.ApiDtos.ProductView;
import app.commerce.service.CatalogService;

@RestController
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryView>> findAllCategories() {
        return ResponseEntity.ok(
            catalogService.findAllCategories()
        );
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryView> findCategory(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            catalogService.findCategory(id)
        );
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductView>> findAllProducts() {
        return ResponseEntity.ok(
            catalogService.findAllProducts()
        );
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductView> findProduct(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            catalogService.findProduct(id)
        );
    }
}