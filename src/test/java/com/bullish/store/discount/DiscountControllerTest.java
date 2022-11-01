package com.bullish.store.discount;

import com.bullish.store.product.Product;
import com.bullish.store.product.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DiscountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private DiscountRepository discountRepository;

    private final String discountApiUrl = "/api/v1/discount";
    Product product = new Product("Apple", 30.0);

    @BeforeEach
    public void init() {
        Mockito.when(this.productRepository.findById(product.getUuid())).thenReturn(product);
    }

    @Test
    public void testGetEmptyDiscount() throws Exception {
        Mockito.when(this.discountRepository.findAll()).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get(discountApiUrl))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testCorrectParseDiscount() throws Exception {
        String discountRequestStr1 = "{\"discountType\":\"PER_PRODUCT_PCT_OFF\",\"productId\":\"" + product.getUuid() + "\",\"perNumOfItems\":2,\"pctOfOriginal\":0.8}";

        MockHttpServletRequestBuilder mockRequest1 =
            MockMvcRequestBuilders.post(discountApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(discountRequestStr1);

        this.mockMvc.perform(mockRequest1)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.discountType", is(DiscountType.PER_PRODUCT_PCT_OFF.toString())));

        String discountRequestStr2 = "{\"discountType\":\"TOTAL_BASKET_PCT_OFF\",\"totalDiscountedPriceReq\":100.0,\"pctOfOriginal\":0.8}";

        MockHttpServletRequestBuilder mockRequest2 =
            MockMvcRequestBuilders.post(discountApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(discountRequestStr2);

        this.mockMvc.perform(mockRequest2)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.discountType", is(DiscountType.TOTAL_BASKET_PCT_OFF.toString())));
    }

    @Test
    public void testWrongParseDiscount() throws Exception {
        String discountRequestStr1 = "{\"discountType\":\"PER_PRODUCT_PCT_OFF\",\"wrong\":2,\"pctOfOriginal\":0.8}";

        MockHttpServletRequestBuilder mockRequest1 =
            MockMvcRequestBuilders.post(discountApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(discountRequestStr1);

        assertThatThrownBy(() -> this.mockMvc.perform(mockRequest1))
            .isInstanceOf(NestedServletException.class);
    }

}
