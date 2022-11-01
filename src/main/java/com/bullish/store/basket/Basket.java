package com.bullish.store.basket;

import com.bullish.store.product.Product;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Basket {

    // Assume customerId is a string for now. It can be extends to a full-fledge Customer class
    @NotNull
    private String customerId;
    private Map<Product, Integer> items;

    public Basket(String customerId) {
        this(customerId, new HashMap<>());
    }

    public Basket(String customerId, Map<Product, Integer> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Map<Product, Integer> getItems() {
        return items;
    }

    public void addProduct(Product product) {
        Integer qty = items.getOrDefault(product, 0);
        Integer newQty = qty + 1;
        items.put(product, newQty);
    }

    public void removeProduct(Product product) {
        Integer qty = items.getOrDefault(product, 0);
        Integer newQty = qty - 1;
        if (newQty <= 0) {
            items.remove(product);
        }
        else {
            items.put(product, newQty);
        }
    }

    public Basket filterActiveProducts() {
        Map<Product, Integer> filteredItems =
            items.entrySet().stream()
                .filter(entry -> entry.getKey().isActive())
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
        return new Basket(customerId, filteredItems);
    }

    public Double calculatePreDiscountPrice() {
        Double totalPrice = 0.0;
        for (var item : items.entrySet()) {
            Product product = item.getKey();
            Integer qty = item.getValue();
            totalPrice += product.getPrice() * qty;
        }
        return totalPrice;
    }

    public Integer getProductQty(Product product) {
        return this.items.getOrDefault(product, 0);
    }

    @Override
    public String toString() {
        return "Basket{" +
            "customerId='" + customerId + '\'' +
            ", items=" + items +
            '}';
    }
}
