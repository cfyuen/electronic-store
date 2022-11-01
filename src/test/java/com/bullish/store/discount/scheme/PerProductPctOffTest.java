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
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PerProductPctOffTest {

    ProductRepository productRepository;
    Product product1 = new Product("Apple", 10.0);
    Product product2 = new Product("Orange", 20.0);

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
        IDiscount discount1 = new PerProductPctOff(product1.getUuid(), 2, 0.8);
        Set<ConstraintViolation<IDiscount>> violations1 = validator.validate(discount1);
        assertTrue(violations1.isEmpty());

        // Wrong number of items
        IDiscount discount2 = new PerProductPctOff(product1.getUuid(), 0, 0.8);
        Set<ConstraintViolation<IDiscount>> violations2 = validator.validate(discount2);
        assertFalse(violations2.isEmpty());

        // Wrong percentage
        IDiscount discount3 = new PerProductPctOff(product1.getUuid(), 2, 1.2);
        Set<ConstraintViolation<IDiscount>> violations3 = validator.validate(discount3);
        assertFalse(violations3.isEmpty());
    }

    @Test
    public void testNoDiscount() {
        IDiscount discount = new PerProductPctOff(product1.getUuid(), 2, 0.8);
        Basket basket = new Basket("testCustomer");
        // Adding 1 product1, 2 product2 should not have discount.
        basket.addProduct(product1);
        basket.addProduct(product2);
        basket.addProduct(product2);
        DiscountApplied discountApplied1 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(DiscountType.PER_PRODUCT_PCT_OFF, discountApplied1.getDiscountType());
        assertEquals(0.0, discountApplied1.getReduction());

        // Adding one more item of product2 should not have discount.
        basket.addProduct(product2);
        DiscountApplied discountApplied2 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(0.0, discountApplied2.getReduction());

        // Adding and removing should result in no discount
        basket.addProduct(product1);
        basket.removeProduct(product1);
        DiscountApplied discountApplied3 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(0.0, discountApplied3.getReduction());
    }

    @Test
    public void testBasicDiscount() {
        IDiscount discount = new PerProductPctOff(product1.getUuid(), 2, 0.8);
        Basket basket = new Basket("testCustomer");
        // Adding 2 product1 should have discount.
        basket.addProduct(product1);
        basket.addProduct(product1);
        DiscountApplied discountApplied1 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(DiscountType.PER_PRODUCT_PCT_OFF, discountApplied1.getDiscountType());
        assertEquals(2.0, discountApplied1.getReduction(), 0.0001);

        // Adding one more product1 should not affect discount
        basket.addProduct(product1);
        DiscountApplied discountApplied2 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(2.0, discountApplied2.getReduction(), 0.0001);

        // Adding one more should double the discount
        basket.addProduct(product1);
        DiscountApplied discountApplied3 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(4.0, discountApplied3.getReduction(), 0.0001);

        // Removing one should remove the discount
        basket.removeProduct(product1);
        DiscountApplied discountApplied4 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(2.0, discountApplied4.getReduction(), 0.0001);

        // Adding other product should not affect the discount
        basket.addProduct(product2);
        basket.addProduct(product2);
        DiscountApplied discountApplied5 = discount.calculateDiscount(productRepository, basket, Collections.emptyList());
        assertEquals(2.0, discountApplied5.getReduction(), 0.0001);
    }

}
