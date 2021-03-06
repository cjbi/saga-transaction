package tech.wetech.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("account-two")
public interface AccountTwoClient {

    @PostMapping("/transfer/increase")
    String increaseAmount(@RequestParam("id") String id, @RequestParam("amount") double amount);

}
