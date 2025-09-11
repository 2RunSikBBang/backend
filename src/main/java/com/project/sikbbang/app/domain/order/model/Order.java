package com.project.sikbbang.app.domain.order.model;

import com.project.sikbbang.app.domain.store.model.Store;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    private String phoneNumber;

    private String customerName;

    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Integer totalPrice;

    private LocalDateTime orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
}
