package com.dcode.product_service.entity;

public interface IVariant {
        public abstract String getVariantId();
        public abstract String getSizeName();
        public abstract String getCategoryName();
        public abstract String getPackageType();
        public abstract Double getQuantity();
}
