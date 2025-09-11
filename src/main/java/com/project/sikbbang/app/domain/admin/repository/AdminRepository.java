package com.project.sikbbang.app.domain.admin.repository;

import com.project.sikbbang.app.domain.admin.model.Admin;
import com.project.sikbbang.app.domain.admin.model.AdminRole;
import com.project.sikbbang.app.domain.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
    boolean existsByUsername(String username);
    List<Admin> findByRole(AdminRole role);
    Optional<Admin> findByStore(Store store);
}
