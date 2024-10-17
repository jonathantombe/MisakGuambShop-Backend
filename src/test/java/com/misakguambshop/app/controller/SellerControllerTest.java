package com.misakguambshop.app.controller;

import com.misakguambshop.app.model.Seller;
import com.misakguambshop.app.service.SellerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SellerControllerTest {

    @Mock
    private SellerService sellerService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private SellerController sellerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getAllSellers() {
        List<Seller> sellers = Arrays.asList(new Seller(), new Seller());
        when(sellerService.getAllSellers()).thenReturn(sellers);

        ResponseEntity<List<Seller>> response = sellerController.getAllSellers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sellers, response.getBody());
    }

    @Test
    void getSellerById() {
        Seller seller = new Seller();
        seller.setId(1L);
        when(sellerService.getSellerById(1L)).thenReturn(Optional.of(seller));

        ResponseEntity<Seller> response = sellerController.getSellerById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(seller, response.getBody());
    }

    @Test
    void getSellerByIdNotFound() {
        when(sellerService.getSellerById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Seller> response = sellerController.getSellerById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateSeller() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setFullName("Updated Seller");
        seller.setEmail("updated@example.com");
        seller.setPassword("newPassword");
        seller.setPhone("1234567890");
        when(sellerService.updateSeller(eq(1L), any(Seller.class))).thenReturn(seller);

        ResponseEntity<Seller> response = sellerController.updateSeller(1L, seller);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(seller, response.getBody());
    }

    @Test
    void updateSellerNotFound() {
        when(sellerService.updateSeller(eq(1L), any(Seller.class))).thenReturn(null);

        ResponseEntity<Seller> response = sellerController.updateSeller(1L, new Seller());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteSeller() {
        ResponseEntity<?> response = sellerController.deleteSeller(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(sellerService, times(1)).deleteSeller(1L);
    }
}