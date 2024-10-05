package com.dcode.product_service.entity;

import com.dcode.product_service.enumeration.PreorderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "preorders")
public class Preorder extends Auditable {

    @Column(nullable = false, unique = true, updatable = false)
    private String preorderId;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private Instant preorderDate;

    @Column(nullable = false)
    private String identity;

    // TODO: 3 trạng thái: Chờ thông báo, Đã thông báo có hàng, Hủy thông báo
    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status;

//    @Column(nullable = false)
//    private String customer;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "variant_id", referencedColumnName = "id", nullable = false)
    private Variant variant;

    // Enum
    public void setStatus(PreorderStatus preorderStatus) {
        this.status = preorderStatus.getValue();
    }
    public PreorderStatus getStatus() {
        return PreorderStatus.fromValue(this.status);
    }

}
