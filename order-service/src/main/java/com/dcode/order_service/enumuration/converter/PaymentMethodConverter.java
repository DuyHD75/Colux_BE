package com.dcode.order_service.enumuration.converter;

import com.dcode.order_service.enumuration.PaymentMethodType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodConverter implements AttributeConverter<PaymentMethodType, String> {

    @Override
    public String convertToDatabaseColumn(PaymentMethodType paymentMethod) {
        if (paymentMethod == null) return null;
        return paymentMethod.getValue();
    }

    @Override
    public PaymentMethodType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return PaymentMethodType.fromValue(dbData);
    }
}
