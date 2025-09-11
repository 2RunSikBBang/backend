package com.project.sikbbang.app.domain.store.repository;

import com.project.sikbbang.app.domain.store.model.Store;
import com.project.sikbbang.app.domain.store.model.StoreInspection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findById(Long id);
    List<Store> findByInspection(StoreInspection inspection);
}
