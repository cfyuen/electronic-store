package com.bullish.store.basket;

import com.bullish.store.product.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * In memory storage of the products.
 */
@Repository
public class BasketRepository {

    private static Logger logger = LogManager.getLogger(BasketRepository.class);

    private HashMap<String, Basket> basketMap;

    public BasketRepository() {
        this.basketMap = new HashMap<>();
    }

    public synchronized Basket findById(String customerId) {
        logger.info("Getting basket of " + customerId);
        return basketMap.getOrDefault(customerId, new Basket(customerId));
    }

    public synchronized Basket addProductsToBasket(String customerId, List<Product> products) {
        Basket currentBasket = basketMap.getOrDefault(customerId, new Basket(customerId));
        for (Product product : products) {
            if (product.isActive()) {
                logger.info("Adding " + product.getName() + " to customer " + customerId);
                currentBasket.addProduct(product);
            }
            else {
                logger.warn("Product " + product.getName() + " is not active. Not adding to basket.");
            }
        }
        basketMap.put(customerId, currentBasket);
        return currentBasket;
    }

    public synchronized Basket removeProductsFromBasket(String customerId, List<Product> products) {
        Basket currentBasket = basketMap.getOrDefault(customerId, new Basket(customerId));
        for (Product product : products) {
            logger.info("Removing " + product.getName() + " from customer " + customerId);
            currentBasket.removeProduct(product);
        }
        basketMap.put(customerId, currentBasket);
        return currentBasket;
    }
}
