package com.dcode.product_service.service;

import java.util.List;
import java.util.Map;

public interface ISearchService {
    Map<String, List<?>> bulkSearchByKeywords(List<String> keywords);
}
