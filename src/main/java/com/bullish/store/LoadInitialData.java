package com.bullish.store;

import com.bullish.store.basket.BasketRepository;
import com.bullish.store.discount.DiscountRepository;
import com.bullish.store.discount.scheme.PerProductPctOff;
import com.bullish.store.product.Product;
import com.bullish.store.product.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class LoadInitialData {

    @Bean
    CommandLineRunner loadData(ProductRepository productRepository, DiscountRepository discountRepository, BasketRepository basketRepository) {
        Product orangeProduct = new Product("Orange", 10.0);
        Product appleProduct = new Product("Apple", 20.0);
        return args -> {
            // Add product Orange and Apple
            productRepository.save(orangeProduct);
            productRepository.save(appleProduct);
            // Buy 2 orange get 20% off
            discountRepository.save(new PerProductPctOff(orangeProduct.getUuid(), 2, 0.8));
            // Adding cart item for John
            basketRepository.addProductsToBasket("john", Arrays.asList(orangeProduct));
            basketRepository.addProductsToBasket("john", Arrays.asList(orangeProduct));
            basketRepository.addProductsToBasket("john", Arrays.asList(orangeProduct));
        };
    }
}
