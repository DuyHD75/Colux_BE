package com.dcode.order_service.entity.chat;

import com.dcode.order_service.entity.Auditable;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Builder
@Table(name = "room")
public class Room extends Auditable {
    @Column(name = "room_id", nullable = false, unique = true)
    private String roomId;

    @Column(name = "user_id", unique = true)
    private String userId;

    @Column(name = "fullName")
    private String fullName;

    @Column(name = "phoneNumber", unique = true)
    private String phoneNumber;

    @Column(name = "email", unique = true)
    private String email;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id", referencedColumnName = "id", unique = true)
    private Message lastMessage;
}

