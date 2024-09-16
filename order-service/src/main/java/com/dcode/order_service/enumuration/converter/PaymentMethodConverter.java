package com.dcode.order_service.enumuration.converter;

import com.dcode.order_service.enumuration.PaymentMethod;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {

    @Override
    public String convertToDatabaseColumn(PaymentMethod paymentMethod) {
        if (paymentMethod == null) return null;
        return paymentMethod.getValue();
    }

    @Override
    public PaymentMethod convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return PaymentMethod.fromValue(dbData);
    }
}
