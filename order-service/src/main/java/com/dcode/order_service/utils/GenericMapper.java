package com.dcode.order_service.utils;

import com.dcode.order_service.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;

public interface GenericMapper<E , I, O> {

        E requestToEntity(I request) throws ResourceNotFoundException;

        O entityToResponse(E entity);

        List<O> entityToResponse(List<E> entities);

        E partialUpdate(E entity, I request);
}
