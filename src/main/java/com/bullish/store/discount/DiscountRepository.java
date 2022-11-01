package com.bullish.store.discount;

import com.bullish.store.discount.scheme.IDiscount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class DiscountRepository {

    private static Logger logger = LogManager.getLogger(DiscountRepository.class);

    private HashMap<UUID, IDiscount> discountMap;

    public DiscountRepository() {
        this.discountMap = new HashMap<>();
    }

    public synchronized List<IDiscount> findAll() {
        return new ArrayList<>(this.discountMap.values());
    }

    public synchronized IDiscount save(IDiscount discount) {
        logger.info("Saved discount " + discount);
        discountMap.put(discount.getUuid(), discount);
        return discount;
    }

    public synchronized boolean deleteById(UUID discountId) {
        if (this.discountMap.containsKey(discountId)) {
            this.discountMap.remove(discountId);
            return true;
        }
        else {
            return false;
        }
    }
}
