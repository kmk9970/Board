package regist.practice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import regist.practice.service.TradeService;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class TradeController {

    private final TradeService service;

    @GetMapping("CoinTrade")
    public String ShowTradePage(){
        return "CoinTrade";
    }

    @PostMapping("CoinTrade/buycoin")
    public String WritePurchaseLog(String coin_name, String price, HttpServletRequest request){
        HttpSession session = request.getSession();
        try {
            service.WritePurchaseLog(coin_name,price,String.valueOf(session.getAttribute("ID")));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/CoinTrade";
    }

    @PostMapping("CoinTrade/sellcoin")
    public String WriteSellLog(String coin_name, String price,HttpServletRequest request){
        HttpSession session = request.getSession();
        try {
            service.WriteSellLog(coin_name,price,String.valueOf(session.getAttribute("ID")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/CoinTrade";
    }
}
