package com.dcode.product_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "stock_import_history")
public class StockImportHistory extends Auditable{

    @Column(updatable = false, unique = true, nullable = false)
    @NaturalId
    private String stockImportHistoryId;
    @Column(columnDefinition = "TEXT")
    private String images;
    private String billCode;
    private String employeeId;

}
