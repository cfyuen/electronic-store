package com.bullish.store.discount.scheme;

import com.bullish.store.basket.Basket;
import com.bullish.store.discount.DiscountApplied;
import com.bullish.store.discount.DiscountType;
import com.bullish.store.product.Product;
import com.bullish.store.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TotalBasketPctOffTest {

    ProductRepository productRepository;
    Product product1 = new Product("Apple", 30.0);
    Product product2 = new Product("Orange", 60.0);

    @BeforeEach
    public void init() {
        productRepository = new ProductRepository();
        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    public void testDiscountValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Correct discount
        IDiscount discount1 = new TotalBasketPctOff(100.0, 0.8);
        Set<ConstraintViolation<IDiscount>> violations1 = validator.validate(discount1);
        assertTrue(violations1.isEmpty());

        // Wrong percentage
        IDiscount discount2 = new TotalBasketPctOff(100.0, 1.2);
        Set<ConstraintViolation<IDiscount>> violations2 = validator.validate(discount2);
        assertFalse(violations2.isEmpty());
    }

    @Test
    public void testNoDiscount() {
        IDiscount discount = new TotalBasketPctOff(100.0, 0.8);
        Basket basket = new Basket("testCustomer");
        // Adding 1 product1, 1 product2 should not have discount.
        basket.addProduct(product1);
        basket.addProduct(product2);
        DiscountApplied discountApplied1 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(DiscountType.TOTAL_BASKET_PCT_OFF, discountApplied1.getDiscountType());
        assertEquals(0.0, discountApplied1.getReduction());

        // Adding and removing should result in no discount
        basket.addProduct(product1);
        basket.removeProduct(product1);
        DiscountApplied discountApplied2 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(0.0, discountApplied2.getReduction());
    }

    @Test
    public void testBasicDiscount() {
        IDiscount discount = new TotalBasketPctOff(100.0, 0.8);
        Basket basket = new Basket("testCustomer");
        // Adding 2 product2 should have discount.
        basket.addProduct(product2);
        basket.addProduct(product2);
        DiscountApplied discountApplied1 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(DiscountType.TOTAL_BASKET_PCT_OFF, discountApplied1.getDiscountType());
        assertEquals(24.0, discountApplied1.getReduction(), 0.0001);

        // Adding one more product1 should give larger discount
        basket.addProduct(product1);
        DiscountApplied discountApplied2 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(30.0, discountApplied2.getReduction(), 0.0001);

        // Removing one should reduce the discount
        basket.removeProduct(product1);
        DiscountApplied discountApplied3 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(24.0, discountApplied3.getReduction(), 0.0001);

        // Remove to less than 100 should remove the discount
        basket.removeProduct(product2);
        DiscountApplied discountApplied4 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(0.0, discountApplied4.getReduction(), 0.0001);
    }

    @Test
    public void testMultipleDiscounts() {
        IDiscount globalDiscount = new TotalBasketPctOff(100.0, 0.8);
        IDiscount productDiscount = new PerProductPctOff(product1.getUuid(), 2, 0.0);
        Basket basket = new Basket("testCustomer");
        // Adding 2 product1, 1 product2 should not have discount.
        basket.addProduct(product2);
        basket.addProduct(product1);
        basket.addProduct(product1);

        // Verify per product discount
        DiscountApplied productDiscountApplied = productDiscount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(DiscountType.PER_PRODUCT_PCT_OFF, productDiscountApplied.getDiscountType());
        assertEquals(30.0, productDiscountApplied.getReduction(), 0.0001);

        // Now the basket value is less than 100, so no discount
        DiscountApplied globalDiscountApplied1 = globalDiscount.calculateDiscount(productRepository, basket, Arrays.asList(productDiscountApplied));
        assertEquals(DiscountType.TOTAL_BASKET_PCT_OFF, globalDiscountApplied1.getDiscountType());
        assertEquals(0.0, globalDiscountApplied1.getReduction(), 0.0001);

        basket.addProduct(product2);
        // The discounted total price is 150, hence the reduction is 30
        DiscountApplied globalDiscountApplied2 = globalDiscount.calculateDiscount(productRepository, basket, Arrays.asList(productDiscountApplied));
        assertEquals(DiscountType.TOTAL_BASKET_PCT_OFF, globalDiscountApplied2.getDiscountType());
        assertEquals(30.0, globalDiscountApplied2.getReduction(), 0.0001);
    }
}
