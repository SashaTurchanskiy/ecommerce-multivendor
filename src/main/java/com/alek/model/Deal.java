package com.alek.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer discount;

    @OneToOne
    private HomeCategory category;

    public Deal(Long id, Integer discount, HomeCategory category) {
        this.id = id;
        this.discount = discount;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public HomeCategory getCategory() {
        return category;
    }

    public void setCategory(HomeCategory category) {
        this.category = category;
    }
}