package com.bullish.store.discount.scheme;

import com.bullish.store.basket.Basket;
import com.bullish.store.discount.DiscountApplied;
import com.bullish.store.discount.DiscountType;
import com.bullish.store.product.Product;
import com.bullish.store.product.ProductRepository;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * If the total price is greater or equal to {totalDiscountedPrice}, apply {pctOfOriginal} to the whole basket.
 */
public class TotalBasketPctOff extends AbstractDiscount {

    @NotNull
    private Double totalDiscountedPriceReq;
    @NotNull
    @Min(0)
    @Max(1)
    private Double pctOfOriginal;

    public TotalBasketPctOff(Double totalDiscountedPriceReq, Double pctOfOriginal) {
        super();
        this.totalDiscountedPriceReq = totalDiscountedPriceReq;
        this.pctOfOriginal = pctOfOriginal;
    }

    @Override
    public DiscountType getDiscountType() {
        return DiscountType.TOTAL_BASKET_PCT_OFF;
    }

    @Override
    public Boolean valid(ProductRepository productRepository) {
        return totalDiscountedPriceReq != null && pctOfOriginal != null;
    }

    @Override
    public DiscountApplied calculateDiscount(ProductRepository productRepository, Basket basket, List<DiscountApplied> discountsApplied) {
        Double totalDiscountedPrice = basket.calculatePreDiscountPrice();
        for (DiscountApplied discountApplied : discountsApplied) {
            totalDiscountedPrice -= discountApplied.getReduction();
        }
        Double reduction;
        if (totalDiscountedPrice >= totalDiscountedPriceReq) {
            reduction = totalDiscountedPrice * (1-pctOfOriginal);
        }
        else {
            reduction = 0.0;
        }
        return new DiscountApplied(getDiscountType(), reduction);
    }

    @Override
    public String toString() {
        return "TotalBasketPctOff{" +
            "totalDiscountedPriceReq=" + totalDiscountedPriceReq +
            ", pctOfOriginal=" + pctOfOriginal +
            '}';
    }
}
