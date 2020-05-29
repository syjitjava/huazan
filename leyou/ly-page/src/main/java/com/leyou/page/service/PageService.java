package com.leyou.page.service;

import java.util.Map;

public interface PageService {
    Map<String,Object> loadDate(Long spuId);

    void createItemHtml(Long id);

    void deleteHtml(Long spuId);
}
