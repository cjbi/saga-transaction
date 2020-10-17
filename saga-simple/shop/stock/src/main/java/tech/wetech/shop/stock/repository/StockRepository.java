package tech.wetech.shop.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tech.wetech.shop.stock.entity.Stock;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("update Stock set amount=amount-:quantity where amount>=:quantity and goodsId=:goodsId")
    @Modifying
    int reduceStock(Long goodsId, Long quantity);

    @Query("select amount from Stock where goodsId=:goodsId")
    long findByGoodsId(Long goodsId);

}
