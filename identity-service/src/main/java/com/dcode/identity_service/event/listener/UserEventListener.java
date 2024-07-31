package com.dcode.identity_service.event.listener;


import com.dcode.identity_service.event.UserEvent;
import com.dcode.identity_service.service.impl.EmailServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserEventListener {

    private final EmailServiceImpl emailServiceImpl;

    @EventListener
    public void onUserEvent(UserEvent event) {
        switch (event.getEventType()) {
            case REGISTRATION ->
                    emailServiceImpl.sendNewAccountRegistrationEmail(event.getUserEntity().getFirstName(), event.getUserEntity().getEmail(), (String) event.getData().get("key"));
            case RESET_PASSWORD ->
                    emailServiceImpl.sendPasswordResetEmail(event.getUserEntity().getFirstName(), event.getUserEntity().getEmail(), (String) event.getData().get("key"));
            default -> {
            }
        }
    }

}
