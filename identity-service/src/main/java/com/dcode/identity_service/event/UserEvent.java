package com.dcode.identity_service.event;



import com.dcode.identity_service.entity.UserEntity;
import com.dcode.identity_service.enumeration.EventType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class UserEvent {
    private UserEntity userEntity;
    private EventType eventType;
    private Map<? , ?> data;
}