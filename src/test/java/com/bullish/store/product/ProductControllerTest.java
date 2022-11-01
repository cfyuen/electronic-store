package com.bullish.store.product;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ProductRepository productRepository;

    private final String productApiUrl = "/api/v1/product";

    Product product1 = new Product("Apple", 123.0);
    Product product2 = new Product("Orange", 456.0);
    Product product3 = new Product("Apple", 789.0);

    @Test
    public void testProductEmpty() throws Exception {
        this.mockMvc.perform(get(productApiUrl))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetAll() throws Exception {
        List<Product> products = new ArrayList<>(Arrays.asList(product1, product2, product3));
        Mockito.when(this.productRepository.findAll()).thenReturn(products);

        this.mockMvc.perform(get(productApiUrl + "?activeOnly=false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].uuid", notNullValue()))
            .andExpect(jsonPath("$[0].name", is(product1.getName())))
            .andExpect(jsonPath("$[0].price", is(product1.getPrice())))
            .andExpect(jsonPath("$[1].name", is(product2.getName())))
            .andExpect(jsonPath("$[1].price", is(product2.getPrice())));
    }

    @Test
    public void testGetAllActive() throws Exception {
        List<Product> products = new ArrayList<>(Arrays.asList(product1, product2, product3));
        Mockito.when(this.productRepository.findAllActive()).thenReturn(products);

        // Default should be activeOnly
        this.mockMvc.perform(get(productApiUrl))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].uuid", notNullValue()))
            .andExpect(jsonPath("$[0].name", is(product1.getName())))
            .andExpect(jsonPath("$[0].price", is(product1.getPrice())))
            .andExpect(jsonPath("$[1].name", is(product2.getName())))
            .andExpect(jsonPath("$[1].price", is(product2.getPrice())));
    }

    @Test
    public void testCorrectPost() throws Exception {
        Mockito.when(this.productRepository.save(product1)).thenReturn(product1);

        MockHttpServletRequestBuilder mockRequest =
            MockMvcRequestBuilders.post(productApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(product1));

        this.mockMvc.perform(mockRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uuid", is(product1.getUuid().toString())))
            .andExpect(jsonPath("$.name", is(product1.getName())))
            .andExpect(jsonPath("$.price", is(product1.getPrice())));
    }

    @Test
    public void testWrongPost() throws Exception {
        // Invalid json
        MockHttpServletRequestBuilder mockRequest1 =
            MockMvcRequestBuilders.post(productApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Apple\"}");

        this.mockMvc.perform(mockRequest1)
            .andExpect(status().isBadRequest());

        // Invalid price
        MockHttpServletRequestBuilder mockRequest2 =
            MockMvcRequestBuilders.post(productApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Apple\", \"price\": -1}");

        this.mockMvc.perform(mockRequest2)
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testBasicDelete() throws Exception {
        Mockito.when(this.productRepository.deleteById(product1.getUuid())).thenReturn(true);;
        Mockito.when(this.productRepository.deleteById(product2.getUuid())).thenReturn(false);;

        // Normal delete
        this.mockMvc.perform(delete(productApiUrl + "/" + product1.getUuid()))
            .andExpect(status().isOk());
        // Non existence entry
        assertThatThrownBy(() -> this.mockMvc.perform(delete(productApiUrl + "/" + product2.getUuid())))
            .isInstanceOf(NestedServletException.class);
    }

}
