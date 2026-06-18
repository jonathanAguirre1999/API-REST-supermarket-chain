package com.fireboxsys.supermarkets.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Double total;

    @ManyToOne
    private Branch branch;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleDetails> saleDetails = new ArrayList<>();

}
