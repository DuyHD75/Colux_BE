package com.dcode.user_service.event;



import com.dcode.user_service.entity.UserEntity;
import com.dcode.user_service.enumeration.EventType;
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
