package com.dcode.identity_service.enumeration.converter;

import com.dcode.identity_service.enumeration.Authority;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Authority, String> {

    @Override
    public String convertToDatabaseColumn(Authority authority) {
      if(authority == null) {
          return null;
      }
      return authority.getAuthorityValue();
    }

    @Override
    public Authority convertToEntityAttribute(String dbData) {
        if(dbData == null) {
            return null;
        }
        return Stream.of(Authority.values())
                .filter(c -> c.getAuthorityValue().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
