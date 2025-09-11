package com.project.sikbbang.app.domain.store.model;

import com.project.sikbbang.app.domain.admin.model.Admin;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLRestriction("deleted = false")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String password;

    private String profileImageUrl;

    @ElementCollection
    @CollectionTable(name = "store_product_images", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "image_url")
    private List<String> productImageUrls;

    private String refundPolicy;

    private String bankAccount;

    private String cancelPhoneNumber;

    @Column(nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private StoreStatus status;

    @Enumerated(EnumType.STRING)
    private StoreInspection inspection;

    @OneToOne(mappedBy = "store", fetch = FetchType.LAZY)
    private Admin admin;

    public void approve() {
        this.inspection = StoreInspection.APPROVED;
    }

    public void reject() {
        this.inspection = StoreInspection.REJECTED;
    }

    public void pending() {
        this.inspection = StoreInspection.PENDING;
    }

    public void updateStatus(StoreStatus status) {
        this.status = status;
    }

    public boolean checkPassword(String password, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, this.password);
    }

    public void updateInfo(List<String> productImageUrls, String refundPolicy, String bankAccount, String cancelPhoneNumber) {
        this.productImageUrls = productImageUrls;
        this.refundPolicy = refundPolicy;
        this.bankAccount = bankAccount;
        this.cancelPhoneNumber = cancelPhoneNumber;
    }
}
