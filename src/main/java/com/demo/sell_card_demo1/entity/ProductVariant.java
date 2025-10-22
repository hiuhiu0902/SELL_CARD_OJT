package com.demo.sell_card_demo1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.dialect.InnoDBStorageEngine;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "product_variants")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variant_id")
    private Long variantId;

    @Column(name = "value")
    private Long value;

    @Column(name = "price")
    private Long price;

    @Column(name = "currency")
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    private List<Storage> storageList;

    // Getters and Setters
}