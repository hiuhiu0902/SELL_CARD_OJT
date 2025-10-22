package com.demo.sell_card_demo1.entity;

import com.demo.sell_card_demo1.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long userId;
    @Column(unique = true, nullable = false,name = "username")
    public String username;
    @Column(nullable = false,name = "password")
    public String password;
    @Column(unique = true, nullable = false,name = "email")
    public String email;
    @Column(unique = true, nullable = false,name = "phone")
    public String phone;
    @Column(name = "banned_at")
    public LocalDateTime banned_at;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    public Role role;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Member memberProfile;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Thêm tiền tố "ROLE_" vào đây
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toString()));
    }
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
