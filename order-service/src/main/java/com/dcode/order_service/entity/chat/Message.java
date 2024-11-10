package com.dcode.order_service.entity.chat;

import com.dcode.order_service.entity.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Builder
@Table(name = "message")
public class Message extends Auditable {
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "fullName")
    private String fullName;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "room_id", nullable = false)
    @JsonBackReference
    private Room room;

    @OneToOne(mappedBy = "lastMessage", cascade = CascadeType.ALL)
    private Room roomFlat;
}