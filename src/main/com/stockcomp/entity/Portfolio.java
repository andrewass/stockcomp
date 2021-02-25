package com.stockcomp.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Portfolio {

    @Id
    @Column(name = "PORTFOLIO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
