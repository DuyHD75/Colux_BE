package com.dcode.order_service.entity.waybill;

import com.dcode.order_service.entity.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "waybill_log")
public class WaybillLog extends Auditable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waybill_id", nullable = false)
    @JsonBackReference
    private Waybill waybill;

    @Column(name = "previous_status")
    private Integer previousStatus;

    @Column(name = "current_status")
    private Integer currentStatus;
}
