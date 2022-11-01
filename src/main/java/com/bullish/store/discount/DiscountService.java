package com.bullish.store.discount;

import com.bullish.store.discount.scheme.IDiscount;
import com.bullish.store.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    @Autowired
    public DiscountService(DiscountRepository discountRepository, ProductRepository productRepository) {
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
    }

    public List<IDiscount> getAllDiscounts() {
        return this.discountRepository.findAll();
    }

    public IDiscount createNewDiscount(DiscountRequest discountRequest) {
        // Need to validate discount, e.g. productId should exist.
        //  This is done by creating a validate function in the IDiscount interface.
        IDiscount discount = discountRequest.getDiscountDetails();
        if (!discount.valid(productRepository)) {
            throw new IllegalStateException("Validation failed for discount " + discount.getDiscountType());
        }
        this.discountRepository.save(discount);
        return discountRequest.getDiscountDetails();
    }

    public void removeDiscount(UUID discountId) {
        this.discountRepository.deleteById(discountId);
    }
}
