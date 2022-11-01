package com.bullish.store.basket;

import com.bullish.store.product.Product;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasketRepositoryTest {

    private final String testCustomer = "testCustomer";

    Product product1 = new Product("Apple", 30.0);
    Product product2 = new Product("Orange", 60.0);

    @Test
    public void testEmptyBasket() {
        BasketRepository basketRepository = new BasketRepository();
        assertEquals(0, basketRepository.findById(testCustomer).getItems().size());
    }

    @Test
    public void testAddAndRemoveBasket() {
        BasketRepository basketRepository = new BasketRepository();
        basketRepository.addProductsToBasket(testCustomer, Arrays.asList(product1, product2, product1));
        assertEquals(2, basketRepository.findById(testCustomer).getItems().size());
        assertEquals(2, basketRepository.findById(testCustomer).getItems().get(product1));
        assertEquals(1, basketRepository.findById(testCustomer).getItems().get(product2));

        basketRepository.removeProductsFromBasket(testCustomer, Arrays.asList(product1));
        assertEquals(2, basketRepository.findById(testCustomer).getItems().size());
        assertEquals(1, basketRepository.findById(testCustomer).getItems().get(product1));

        basketRepository.removeProductsFromBasket(testCustomer, Arrays.asList(product1));
        assertEquals(1, basketRepository.findById(testCustomer).getItems().size());
    }

    @Test
    public void testAddInactiveToBasket() {
        Product inactiveProduct = new Product("Inactive", 30.0);
        inactiveProduct.setActive(false);
        BasketRepository basketRepository = new BasketRepository();
        basketRepository.addProductsToBasket(testCustomer, Arrays.asList(inactiveProduct));
        assertEquals(0, basketRepository.findById(testCustomer).getItems().size());
    }

}
