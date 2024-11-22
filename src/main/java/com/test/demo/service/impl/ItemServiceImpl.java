package com.test.demo.service.impl;

import com.test.demo.dao.ItemDao; // ItemDao 인터페이스 import
import com.test.demo.service.ItemService; // ItemService 인터페이스 import
import com.test.demo.vo.ItemVo; // ItemVo 클래스 import
import org.springframework.beans.factory.annotation.Autowired; // Autowired import
import org.springframework.stereotype.Service; // Service import

import java.util.List; // List import
import java.util.Map; // Map import

@Service // 스프링 서비스 계층으로 등록
public class ItemServiceImpl implements ItemService {

    @Autowired // 의존성 주입
    private ItemDao itemDao;

    @Override
    public List<ItemVo> getItems(Map<String, Object> params) {
        return itemDao.selectItems(params);
    }

    @Override
    public ItemVo getItemDetail(int itemId) {
        return itemDao.selectItemDetail(itemId);
    }

    @Override
    public void registerItem(ItemVo item) {
        itemDao.insertItem(item);
    }

    @Override
    public void updateItem(int itemId, ItemVo item) {
        item.setId(itemId);
        itemDao.updateItem(item);
    }

    @Override
    public void deleteItem(int itemId) {
        itemDao.deleteItem(itemId);
    }
}
