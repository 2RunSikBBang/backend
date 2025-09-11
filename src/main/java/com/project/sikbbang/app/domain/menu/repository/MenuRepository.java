package com.project.sikbbang.app.domain.menu.repository;

import com.project.sikbbang.app.domain.menu.model.Menu;
import com.project.sikbbang.app.domain.store.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByStore(Store store);
}
