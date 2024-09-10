package com.misakguambshop.app.service;

import com.misakguambshop.app.model.Seller;
import com.misakguambshop.app.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SellerService sellerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllSellers() {
        List<Seller> sellers = Arrays.asList(new Seller(), new Seller());
        when(sellerRepository.findAll()).thenReturn(sellers);

        List<Seller> result = sellerService.getAllSellers();

        assertEquals(2, result.size());
        verify(sellerRepository, times(1)).findAll();
    }

    @Test
    void getSellerById() {
        Seller seller = new Seller();
        seller.setId(1L);
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));

        Optional<Seller> result = sellerService.getSellerById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(sellerRepository, times(1)).findById(1L);
    }

    @Test
    void updateSeller() {
        Seller existingSeller = new Seller();
        existingSeller.setId(1L);
        existingSeller.setFullName("Old FullName");
        existingSeller.setEmail("old@example.com");
        existingSeller.setPassword("oldPassword");

        Seller updatedDetails = new Seller();
        updatedDetails.setFullName("New FullName");
        updatedDetails.setEmail("new@example.com");
        updatedDetails.setPassword("newPassword");

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(existingSeller));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(sellerRepository.save(any(Seller.class))).thenReturn(updatedDetails);

        Seller result = sellerService.updateSeller(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("New FullName", result.getFullName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("newPassword", result.getPassword());
        verify(sellerRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    @Test
    void updateSellerWithoutPassword() {
        Seller existingSeller = new Seller();
        existingSeller.setId(1L);
        existingSeller.setFullName("Old FullName");
        existingSeller.setEmail("old@example.com");
        existingSeller.setPassword("oldPassword");

        Seller updatedDetails = new Seller();
        updatedDetails.setFullName("New FullName");
        updatedDetails.setEmail("new@example.com");
        // No se establece una nueva contraseña

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(existingSeller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(updatedDetails);

        Seller result = sellerService.updateSeller(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("New FullName", result.getFullName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("oldPassword", existingSeller.getPassword());
        verify(sellerRepository, times(1)).findById(1L);
        verify(passwordEncoder, never()).encode(anyString());
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    @Test
    void deleteSeller() {
        sellerService.deleteSeller(1L);

        verify(sellerRepository, times(1)).deleteById(1L);
    }
}