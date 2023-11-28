package regist.practice.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;
import regist.practice.Api_Client;
import regist.practice.domain.purchase_log;
import regist.practice.domain.sell_log;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TradeRepository {
    private final EntityManager EM;

    private static  List<String> coinName;

    static {
        try {
            coinName = allCoin();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void WriteLog(purchase_log log){
        EM.persist(log);
    }
    public void WriteLog(sell_log log){
        EM.persist(log);
    }

    public  String buy_coin(Api_Client api, String coinName,int price) throws IOException, JSONException {
        String coinPrice = getCoinPrice(coinName);
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("currency", coinName);
        String result1 = api.callApi("/info/balance", rgParams);
        //System.out.println(result);
        JSONObject json = new JSONObject(result1);
        String data = json.getString("data"); //string()쓰기

        JSONObject json1 = new JSONObject(data);
        String total_krw = json1.getString("total_krw").toString();  //보유 자산 구하기

        if( Double.parseDouble(total_krw)*0.69<price){
            return "0";
        }

        Double unit = price/Double.parseDouble(coinPrice)*0.69;
        if(Double.parseDouble(coinPrice)*unit>Double.parseDouble(total_krw)*0.69&Double.parseDouble(total_krw)<500){
            return "0";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.####");

        // 잘라내기
        String formattedNumber = decimalFormat.format(unit);

        HashMap<String, String> rgParams1 = new HashMap<String, String>();
        rgParams1.put("units", formattedNumber); //소수점 4자리 맞추기=코인수량
        rgParams1.put("order_currency", coinName); //매수 하려는 코인 이름
        rgParams1.put("payment_currency", "KRW"); // 매수하려는 통화
        String result = api.callApi("/trade/market_buy", rgParams1);
        System.out.println("결과"+ result);
        return formattedNumber;
    }
    public int sell_coin(Api_Client api,String coinName,String unit) throws IOException {
        List<String> holdcoin=getAsset(api);

        if(holdcoin.contains(coinName)) { //코인이 없으면 못판다.
            try {
                int index=holdcoin.indexOf(coinName);
                if(Double.parseDouble(holdcoin.get(index-1))<Double.parseDouble(unit)){//not enough coin
                    return 0;
                }
                HashMap<String, String> rgParams = new HashMap<String, String>();
                rgParams.put("units", unit); //소수점 4자리 맞추기
                rgParams.put("order_currency", coinName); //매도 하려는 코인 이름
                rgParams.put("payment_currency", "KRW"); //매도하려는 통화
                String result = api.callApi("/trade/market_sell", rgParams);
                return 1;
            } catch (Exception e) {
                System.out.println("에러 " + e);
            }
        }
        return 0;
    }
    public List<String> getAsset(Api_Client api_client){ //유저가 가진 코인 불러오기
        List<String> myCoin = new ArrayList<>();
        for(int i= 0;i<coinName.size();i++) {
            String result = "";
            String total_krw = "";
            String total_currency = "";
            try {

                HashMap<String, String> rgParams = new HashMap<String, String>();
                rgParams.put("currency", coinName.get(i));
                result = api_client.callApi("/info/balance", rgParams);
                //System.out.println(result);
                JSONObject json = new JSONObject(result);
                String data = json.getString("data"); //string()쓰기

                JSONObject json1 = new JSONObject(data);
                total_krw = json1.getString("total_krw").toString();  //보유 자산 구하기
                total_currency = json1.get("total_" + coinName.get(i).toLowerCase()).toString();
                // System.out.println(total_krw + "    " + total_currency);
                if (Double.valueOf(total_currency) > 0) {
                    System.out.println(total_krw + "    " + total_currency);
                    myCoin.add(total_currency);
                    myCoin.add(coinName.get(i));
                }
            } catch (Exception e) {
            }
        }
        return myCoin;
    }

    //코인의 가격을 알아오는 함수
    public String getCoinPrice(String coinName) throws IOException, JSONException {
        String URL = "https://api.bithumb.com/public/transaction_history/"+coinName+"_KRW";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        JSONObject json = new JSONObject(body.string());
        JSONArray dataArray= json.getJSONArray("data");

        JSONObject json1 = new JSONObject(dataArray.get(dataArray.length()-1).toString());
        String coinPrice = json1.get("price").toString();
        return coinPrice;
    }


    public static List<String> allCoin() throws IOException, JSONException { //
        //모든 코인이름을 불러오는 함수
        String URL = "https://api.bithumb.com/public/ticker/ALL_KRW";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        JSONObject json = new JSONObject(body.string());


        String data = json.get("data").toString();

        JSONObject dataJson = new JSONObject(data);
        Iterator jsonKeys=  dataJson.keys();  //json에 모든 키값들을 불러오는 코드(코인 이름이 키값)?>모든 코인 이름 불러오기
        List<String> coinName = new ArrayList<>();
        while(jsonKeys.hasNext())
        {
            String cN = jsonKeys.next().toString();
            coinName.add(cN); // 키 값 저장
        }
//        for(int i = 0 ; i<coinName.size();i++){
//            System.out.println(coinName.get(i));
//        }
        return coinName ;
    }


}
