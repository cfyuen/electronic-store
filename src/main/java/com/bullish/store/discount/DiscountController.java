package com.bullish.store.discount;

import com.bullish.store.discount.scheme.IDiscount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/discount")
public class DiscountController {

    private final DiscountService discountService;

    @Autowired
    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping("")
    public List<IDiscount> getAllDiscounts() {
        return this.discountService.getAllDiscounts();
    }

    @PostMapping("")
    public IDiscount createNewDiscount(@Valid @RequestBody DiscountRequest discountRequest) {
        return this.discountService.createNewDiscount(discountRequest);
    }

    @DeleteMapping("/{discountId}")
    public void removeDiscount(@PathVariable("discountId") UUID discountId) {
        this.discountService.removeDiscount(discountId);
    }

}
