package tech.wetech.shop.pay.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * @author cjbi
 */
@Mapper
public interface UserWalletMapper {

    @Update("update t_user_wallet set money=money-#{price} where money>=#{price} and user_id=#{userId}")
    int payment(String userId, BigDecimal price);

    @Select("select money from t_user_wallet where user_id=#{userId}")
    BigDecimal selectMoneyByUserId(String userId);

}
