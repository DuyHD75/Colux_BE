package com.dcode.order_service.utils;

import com.dcode.order_service.dto.chat.request.MessageRequest;
import com.dcode.order_service.dto.chat.response.MessageResponse;
import com.dcode.order_service.entity.chat.Message;
import com.dcode.order_service.entity.chat.Room;
import com.dcode.order_service.exception.ResourceNotFoundException;
import com.dcode.order_service.repository.IRoomRepository;
import io.github.perplexhub.rsql.RSQLJPASupport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageUtils implements GenericMapper<Message, MessageRequest, MessageResponse> {

    private final IRoomRepository roomRepository;

    @Override
    public Message requestToEntity(MessageRequest request) throws ResourceNotFoundException {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));

        return Message.builder()
                .content(request.getContent())
                .status(request.getStatus())
                .userId(request.getUserId())
                .room(room)
                .build();
    }

    @Override
    public MessageResponse entityToResponse(Message entity) {

        if(entity == null) {
            return null;
        }
        MessageResponse.UserResponse userResponse = new MessageResponse.UserResponse(
              entity.getUserId(), entity.getFullName(), entity.getPhoneNumber(), entity.getEmail()
        );

        return MessageResponse.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .content(entity.getContent())
                .status(entity.getStatus())
                .user(userResponse)
                .build();
    }

    @Override
    public List<MessageResponse> entityToResponse(List<Message> entities) {
        List<MessageResponse> messageResponses = entities.stream()
                .map(entity -> entityToResponse(entity))
                .collect(Collectors.toList());

        return messageResponses;
    }

    @Override
    public Message partialUpdate(Message entity, MessageRequest request) {
        return null;
    }

    public static <T> Specification<T> parse(String search, List<String> searchFields) {
        if (search == null || search.isBlank() || searchFields == null || searchFields.size() == 0) {
            return RSQLJPASupport.toSpecification(null);
        }

        return searchFields.stream()
                .map(field -> field + "=like='" + search.trim() + "'")
                .collect(Collectors.collectingAndThen(Collectors.joining(","), RSQLJPASupport::toSpecification));
    }
}
