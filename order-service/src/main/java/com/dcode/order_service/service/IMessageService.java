package com.dcode.order_service.service;

import com.dcode.order_service.dto.chat.ListResponse;
import com.dcode.order_service.dto.chat.request.MessageRequest;
import com.dcode.order_service.dto.chat.response.MessageResponse;
import com.dcode.order_service.entity.chat.Message;
import com.dcode.order_service.exception.ResourceNotFoundException;
import com.dcode.order_service.utils.MessageUtils;
import com.dcode.order_service.utils.GenericMapper;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface IMessageService {
    ListResponse<MessageResponse> findAll(int page, int size, String sort, String filter, String search, boolean all);

    MessageResponse findById(Long id) throws ResourceNotFoundException;

    MessageResponse save(MessageRequest request) throws ResourceNotFoundException;

    MessageResponse save(Long id, MessageRequest request) throws ResourceNotFoundException;

    void delete(Long id);

    void delete(List<Long> ids);


    default ListResponse<MessageResponse> defaultFindAll(
            int page, int size, String sort, String filter, String search, boolean all,
            List<String> searchFields, JpaSpecificationExecutor<Message> repository, GenericMapper<Message, MessageRequest, MessageResponse> mapper
    ) {
        Specification<Message> sortable = RSQLJPASupport.toSort(sort);
        Specification<Message> filterable = RSQLJPASupport.toSpecification(filter);
        Specification<Message> searchable = MessageUtils.parse(search, searchFields);
        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);
        Page<Message> entities = repository.findAll(sortable.and(filterable).and(searchable), pageable);
        List<MessageResponse> entityResponses = mapper.entityToResponse(entities.getContent());
        return new ListResponse<>(entityResponses, entities);
    }

    default MessageResponse defaultFindById(
            Long id,
            JpaRepository<Message, Long> repository,
            GenericMapper<com.dcode.order_service.entity.chat.Message, MessageRequest, MessageResponse> mapper,
            String resourceName) throws ResourceNotFoundException {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(() -> new ResourceNotFoundException(resourceName, "id", id));
    }

    default <E> MessageResponse defaultSave(MessageRequest request,
                                            JpaRepository<Message, Long> repository,
                                            GenericMapper<Message, MessageRequest, MessageResponse> mapper) throws ResourceNotFoundException {
        Message entity = mapper.requestToEntity(request);
        entity = repository.save(entity);
        return mapper.entityToResponse(entity);
    }

    default MessageResponse defaultSave(Long id, MessageRequest request,
                                        JpaRepository<Message, Long> repository,
                                        GenericMapper<Message, MessageRequest, MessageResponse> mapper,
                                        String resourceName) throws ResourceNotFoundException {
        return repository.findById(id)
                .map(existingEntity -> mapper.partialUpdate(existingEntity, request))
                .map(repository::save)
                .map(mapper::entityToResponse)
                .orElseThrow(() -> new ResourceNotFoundException(resourceName, "id", id));
    }

}
