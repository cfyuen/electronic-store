package com.bullish.store.product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * In memory storage of the products.
 */
@Repository
public class ProductRepository {

    private static Logger logger = LogManager.getLogger(ProductRepository.class);

    private HashMap<UUID, Product> productMap;

    public ProductRepository() {
        this.productMap = new HashMap<>();
    }

    public synchronized List<Product> findAll() {
        return new ArrayList<>(this.productMap.values());
    }

    public synchronized List<Product> findAllActive() {
        return new ArrayList<>(this.productMap.values()).stream().filter(product -> product.isActive()).toList();
    }

    public synchronized Product findById(UUID productId) {
        return this.productMap.get(productId);
    }

    public synchronized Product save(Product product) {
        logger.info("Saved product " + product);
        productMap.put(product.getUuid(), product);
        return product;
    }

    public synchronized boolean deleteById(UUID productId) {
        if (this.productMap.containsKey(productId)) {
            // Instead of removing the product, we should mark them as inactive
            Product product = this.productMap.get(productId);
            product.setActive(false);
            return true;
        }
        else {
            return false;
        }
    }
}
