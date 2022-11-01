package com.bullish.store.discount;

import com.bullish.store.discount.scheme.PerProductPctOff;
import com.bullish.store.discount.scheme.TotalBasketPctOff;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class DiscountRequestDeserializer extends JsonDeserializer<DiscountRequest> {

    private final String DISCOUNT_TYPE = "discountType";

    @Override
    public DiscountRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, IllegalStateException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = mapper.readTree(jsonParser);
        if (!node.has(DISCOUNT_TYPE)) {
            throw new IOException("Missing field " + DISCOUNT_TYPE);
        }
        String discountTypeStr = node.get(DISCOUNT_TYPE).asText();
        DiscountType discountType = DiscountType.valueOf(discountTypeStr);
        DiscountRequest discountRequest =
            switch (discountType) {
                case PER_PRODUCT_PCT_OFF -> new DiscountRequest(discountType, mapper.readValue(node.toString(), PerProductPctOff.class));
                case TOTAL_BASKET_PCT_OFF -> new DiscountRequest(discountType, mapper.readValue(node.toString(), TotalBasketPctOff.class));
                default -> throw new IllegalStateException("Unexpected value: " + DiscountType.valueOf(discountTypeStr));
            };
        return discountRequest;
    }
}
