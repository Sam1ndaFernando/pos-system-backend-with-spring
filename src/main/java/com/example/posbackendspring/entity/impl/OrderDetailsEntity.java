package com.example.posbackendspring.entity.impl;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "orderDetails")
public class OrderDetailsEntity {
    @Id
    private String id;
    private LocalDate date;
    private String customerId;
    private String customerName;
    private String customerCity;
    private String customerTel;
    @Column(insertable = false, updatable = false)
    private String itemCode;
    private String itemName;
    private int orderQTY;
    private double unitPrice;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "itemCode",referencedColumnName = "itemCode") //duplicate column mapping
    private ItemEntity item;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "orderID")
    private OrderEntity order;
}
