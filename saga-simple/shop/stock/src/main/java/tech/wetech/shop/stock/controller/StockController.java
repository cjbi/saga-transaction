package tech.wetech.shop.stock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.wetech.shop.stock.dto.StockReduceDTO;
import tech.wetech.shop.stock.service.StockService;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/hello")
    public String hello() {
        return "hello world!";
    }

    @PutMapping("/stock/reduce")
    public String reduce(StockReduceDTO stockReduceDTO) {
        stockService.reduce(stockReduceDTO);
        return "success";
    }

}
