package com.dcode.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_price_trackings")
public class ProductPriceTracking extends Auditable{
    private BigDecimal actualPrice;
    private BigDecimal sellingPrice;
    private LocalDateTime effectiveDate;
    private LocalDateTime expirationDate;


}
