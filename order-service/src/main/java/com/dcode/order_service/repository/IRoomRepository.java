package com.dcode.order_service.repository;

import com.dcode.order_service.entity.chat.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IRoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    Optional<Room> findByUserId(String userId);

    Optional<Room> findByPhoneNumber(String phoneNumber);

    Optional<Room> findByRoomId(String roomId);

    Optional<Room> findByEmail(String email);

    Optional<Room> findByUserIdOrPhoneNumber(String userId, String phoneNumber);

    Optional<Room> findByUserIdAndEmailAndPhoneNumber(String userId, String email, String phoneNumber);
}