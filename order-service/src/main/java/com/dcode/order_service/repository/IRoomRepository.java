package com.dcode.order_service.repository;

import com.dcode.order_service.entity.chat.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IRoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
//    Optional<Room> findByUserUsername(String username);
}
