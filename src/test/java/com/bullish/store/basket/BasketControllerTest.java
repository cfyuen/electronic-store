package com.bullish.store.basket;

import com.bullish.store.discount.DiscountRepository;
import com.bullish.store.discount.scheme.IDiscount;
import com.bullish.store.discount.scheme.PerProductPctOff;
import com.bullish.store.discount.scheme.TotalBasketPctOff;
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

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BasketControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    private BasketRepository basketRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private DiscountRepository discountRepository;

    private final String testCustomer = "testCustomer";
    private final String basketApiUrl = "/api/v1/testCustomer/basket";

    Product product1 = new Product("Apple", 30.0);
    Product product2 = new Product("Orange", 60.0);

    @BeforeEach
    public void init() {
        Mockito.when(this.productRepository.findById(product1.getUuid())).thenReturn(product1);
        Mockito.when(this.productRepository.findById(product2.getUuid())).thenReturn(product2);
    }

    @Test
    public void testGetEmptyBasket() throws Exception {
        Basket basket = new Basket(testCustomer);
        Mockito.when(this.basketRepository.findById(testCustomer)).thenReturn(basket);

        this.mockMvc.perform(get(basketApiUrl))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId", is(testCustomer)))
            .andExpect(jsonPath("$.items").exists());
    }

    @Test
    public void testAddToBasket() throws Exception {
        Map<Product, Integer> items = new HashMap<>();
        items.put(product1, 1);
        items.put(product2, 1);
        Basket basket = new Basket(testCustomer, items);
        Mockito.when(
            this.basketRepository.addProductsToBasket(testCustomer, Arrays.asList(product1, product2))
        ).thenReturn(basket);

        MockHttpServletRequestBuilder mockRequest =
            MockMvcRequestBuilders.post(basketApiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Arrays.asList(product1.getUuid(), product2.getUuid())));

        this.mockMvc.perform(mockRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.customerId", is(testCustomer)))
            .andExpect(jsonPath("$.items", aMapWithSize(2)));
    }

    @Test
    public void testBasketReceiptNoDiscount() throws Exception {
        Map<Product, Integer> items = new HashMap<>();
        items.put(product1, 1);
        items.put(product2, 1);
        Basket basket = new Basket(testCustomer, items);
        Mockito.when(this.basketRepository.findById(testCustomer)).thenReturn(basket);
        Mockito.when(this.discountRepository.findAll()).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get(basketApiUrl + "/receipt"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.discountsApplied", hasSize(0)))
            .andExpect(jsonPath("$.totalPrice", is(90.0)));
    }

    @Test
    public void testBasketReceiptInactiveProduct() throws Exception {
        Product inactiveProduct = new Product("Apple", 30.0);
        inactiveProduct.setActive(false);
        Map<Product, Integer> items = new HashMap<>();
        items.put(inactiveProduct, 1);
        items.put(product2, 1);
        Basket basket = new Basket(testCustomer, items);
        Mockito.when(this.basketRepository.findById(testCustomer)).thenReturn(basket);
        Mockito.when(this.discountRepository.findAll()).thenReturn(Collections.emptyList());

        this.mockMvc.perform(get(basketApiUrl + "/receipt"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.discountsApplied", hasSize(0)))
            .andExpect(jsonPath("$.totalPrice", is(60.0)));
    }

    @Test
    public void testBasketReceiptWithDiscount() throws Exception {
        IDiscount productDiscount = new PerProductPctOff(product1.getUuid(), 2, 0.5);

        Map<Product, Integer> items = new HashMap<>();
        items.put(product1, 2);
        Basket basket = new Basket(testCustomer, items);
        Mockito.when(this.basketRepository.findById(testCustomer)).thenReturn(basket);
        Mockito.when(this.discountRepository.findAll()).thenReturn(List.of(productDiscount));

        this.mockMvc.perform(get(basketApiUrl + "/receipt"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.discountsApplied", hasSize(1)))
            .andExpect(jsonPath("$.totalPrice", is(45.0)));
    }

    @Test
    public void testBasketReceiptWithOrderedDiscount() throws Exception {
        // Discount should apply in correct order
        IDiscount globalDiscount = new TotalBasketPctOff(100.0, 0.8);
        IDiscount productDiscount = new PerProductPctOff(product1.getUuid(), 2, 0.0);

        Map<Product, Integer> items = new HashMap<>();
        items.put(product1, 2);
        items.put(product2, 2);
        Basket basket = new Basket(testCustomer, items);
        Mockito.when(this.basketRepository.findById(testCustomer)).thenReturn(basket);
        Mockito.when(this.discountRepository.findAll()).thenReturn(Arrays.asList(globalDiscount, productDiscount));

        // Product discount must be applied before global discount
        this.mockMvc.perform(get(basketApiUrl + "/receipt"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.discountsApplied", hasSize(2)))
            .andExpect(jsonPath("$.totalPrice", is(120.0)));
    }

}
