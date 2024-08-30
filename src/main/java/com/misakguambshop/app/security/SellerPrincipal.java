package com.misakguambshop.app.security;

import com.misakguambshop.app.model.Seller;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class SellerPrincipal implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public SellerPrincipal(Long id, String username, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_SELLER"));
    }

    public static SellerPrincipal create(Seller seller) {
        return new SellerPrincipal(
                seller.getId(),
                seller.getUsername(),
                seller.getEmail(),
                seller.getPassword()
        );
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}