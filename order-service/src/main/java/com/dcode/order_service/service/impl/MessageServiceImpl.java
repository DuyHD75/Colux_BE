package com.dcode.order_service.service.impl;

import com.dcode.order_service.constant.SearchFields;
import com.dcode.order_service.dto.chat.ListResponse;
import com.dcode.order_service.dto.chat.request.MessageRequest;
import com.dcode.order_service.dto.chat.response.MessageResponse;
import com.dcode.order_service.entity.chat.Message;
import com.dcode.order_service.entity.chat.Room;
import com.dcode.order_service.exception.ResourceNotFoundException;
import com.dcode.order_service.repository.IMessageRepository;
import com.dcode.order_service.repository.IRoomRepository;
import com.dcode.order_service.service.IMessageService;
import com.dcode.order_service.utils.MessageUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements IMessageService {

    private final IMessageRepository messageRepository;
    private final IRoomRepository roomRepository;
    private final MessageUtils chatUtils;


    @Override
    public ListResponse<MessageResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.MESSAGE, messageRepository, chatUtils);
    }

    @Override
    public MessageResponse findById(Long id) throws ResourceNotFoundException {
        return defaultFindById(id, messageRepository, new MessageUtils(roomRepository), "Message");
    }

    @Override
    public MessageResponse save(MessageRequest request) throws ResourceNotFoundException {
        Message message = chatUtils.requestToEntity(request);

        Message messageAfterSave = messageRepository.save(message);

        roomRepository.findByRoomId(request.getRoomId())
                .ifPresent(room -> {
                    room.setUpdatedAt(LocalDateTime.now());
                    room.setLastMessage(messageAfterSave);
                    roomRepository.save(room);
                });

        return chatUtils.entityToResponse(messageAfterSave);
    }

    @Override
    public MessageResponse save(Long id, MessageRequest request) throws ResourceNotFoundException {
        return defaultSave(id, request, messageRepository, chatUtils, "Message");
    }

    @Override
    public void delete(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        messageRepository.deleteAllById(ids);
    }
}
