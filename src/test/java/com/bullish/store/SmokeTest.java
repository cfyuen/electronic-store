package com.bullish.store;

import com.bullish.store.basket.BasketController;
import com.bullish.store.discount.DiscountController;
import com.bullish.store.product.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SmokeTest {

    @Autowired
    private ProductController productController;
    @Autowired
    private DiscountController discountController;
    @Autowired
    private BasketController basketController;

    @Test
    public void contextLoads() throws Exception {
        assertThat(productController).isNotNull();
        assertThat(discountController).isNotNull();
        assertThat(basketController).isNotNull();
    }
}
