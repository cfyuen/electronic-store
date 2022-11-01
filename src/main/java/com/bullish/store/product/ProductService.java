package com.bullish.store.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts(Boolean activeOnly) {
        if (activeOnly) {
            return this.productRepository.findAllActive();
        }
        else {
            return this.productRepository.findAll();
        }
    }

    public Product createNewProduct(Product product) {
        return this.productRepository.save(product);
    }

    public void removeProduct(UUID productId) {
        // TODO: Remove related discounts also
        if (!this.productRepository.deleteById(productId)) {
            throw new IllegalStateException("Product ID " + productId + " cannot be removed");
        }
    }
}
