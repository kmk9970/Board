package regist.practice.service;

import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import regist.practice.Api_Client;
import regist.practice.domain.purchase_log;
import regist.practice.domain.sell_log;
import regist.practice.domain.user_info;
import regist.practice.repository.LoginRepository;
import regist.practice.repository.TradeRepository;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final LoginRepository loginRepository;
    public void WritePurchaseLog(String coin_name, String price,String user_id) throws JSONException, IOException {
        user_info user=loginRepository.getUserData(user_id).get(0);
        Api_Client api=new Api_Client(user.getApi_key(),user.getSec_key());
        purchase_log log=new purchase_log();
        log.setAmount(Double.parseDouble(tradeRepository.buy_coin(api,coin_name,Integer.parseInt(price))));
        if(log.getAmount()!=0){
            LocalDateTime now = LocalDateTime.now();
            String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.setCoin_name(coin_name);
            log.setPrice(tradeRepository.getCoinPrice(coin_name));
            log.setTrade_date(formattedDate);
            tradeRepository.WriteLog(log);
        }
    }

    public void WriteSellLog(String coin_name, String price,String user_id) throws IOException, JSONException {
        user_info user=loginRepository.getUserData(user_id).get(0);
        sell_log log=new sell_log();
        Api_Client api=new Api_Client(user.getApi_key(),user.getSec_key());
        log.setPrice(tradeRepository.getCoinPrice(coin_name));
        log.setAmount(Integer.parseInt(price)/Double.parseDouble(log.getPrice()));
        DecimalFormat decimalFormat = new DecimalFormat("#.####");
        if(tradeRepository.sell_coin(api,coin_name,decimalFormat.format(log.getAmount()))!=0) {
            LocalDateTime now = LocalDateTime.now();
            String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.setCoin_name(coin_name);
            log.setTrade_date(formattedDate);
            tradeRepository.WriteLog(log);
        }
        else return;
    }

}
