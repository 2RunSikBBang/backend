package com.project.sikbbang.app.domain.order.repository;

import com.project.sikbbang.app.domain.order.model.Order;
import com.project.sikbbang.app.domain.store.model.Store;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStoreOrderByOrderDateDesc(Store store);
    List<Order> findByPhoneNumber(String phoneNumber);
    List<Order> findByStoreAndPhoneNumberOrderByOrderDateDesc(Store store, String phoneNumber);
    Optional<Order> findByIdAndStoreId(Long orderId, Long storeId);
}
