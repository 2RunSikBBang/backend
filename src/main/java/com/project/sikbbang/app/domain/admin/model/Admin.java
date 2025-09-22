package com.project.sikbbang.app.domain.admin.model;

import com.project.sikbbang.app.domain.store.model.Store;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdminRole role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    public void deactivate() {
        this.role = AdminRole.NOT_ACTIVATE_ADMIN;
    }

    public void activate() {
        if (this.role.equals(AdminRole.NOT_ACTIVATE_ADMIN)) {
            this.role = AdminRole.STORE_ADMIN;
        }
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setRole(AdminRole role) {
        this.role = role;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
