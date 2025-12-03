package com.demo.sell_card_demo1.entity;

import com.demo.sell_card_demo1.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long member_id;
    public String name;
    public String address;
    @Enumerated(EnumType.STRING)
    public Gender gender;
    @Column(unique = true, nullable = false, name = "phone")
    public String phone;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    public User user;
    public String avatarUrl;

}
