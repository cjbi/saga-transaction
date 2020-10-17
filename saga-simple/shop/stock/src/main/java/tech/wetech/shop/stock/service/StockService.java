package tech.wetech.shop.stock.service;

import tech.wetech.shop.stock.dto.StockReduceDTO;

public interface StockService {

    void reduce(StockReduceDTO stockReduceDTO);

}
