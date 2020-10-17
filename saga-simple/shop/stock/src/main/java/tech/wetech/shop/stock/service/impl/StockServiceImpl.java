package tech.wetech.shop.stock.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.wetech.shop.stock.dto.StockReduceDTO;
import tech.wetech.shop.stock.exception.StockException;
import tech.wetech.shop.stock.repository.StockRepository;
import tech.wetech.shop.stock.service.StockService;

import javax.transaction.Transactional;

@Service
public class StockServiceImpl implements StockService {

    private final Logger log = LoggerFactory.getLogger(StockService.class);
    @Autowired
    private StockRepository stockRepository;

    @Override
    @Transactional
    public void reduce(StockReduceDTO stockReduceDTO) {
        long amount = stockRepository.findByGoodsId(stockReduceDTO.getGoodsId());
        log.info("商品【{}】当前剩余库存【{}】，即将扣除库存【{}】", stockReduceDTO.getGoodsId(), amount, stockReduceDTO.getQuantity());
        int rows = stockRepository.reduceStock(stockReduceDTO.getGoodsId(), stockReduceDTO.getQuantity());
        if (rows == 0) {
            throw new StockException("商品库存不足");
        }
    }
}
