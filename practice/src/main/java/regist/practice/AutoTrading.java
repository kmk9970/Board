package regist.practice;

//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
//import com.squareup.okhttp.ResponseBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import regist.practice.domain.user_info;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class AutoTrading { //user_id and coin names required
    //코인 보유량을 저장하는 해시맵
    public HashMap<String, Double> coinAmount;
    private static String key ="";
    private static String sec="";

    private static List<user_info> userData;
    private static  List<String> coinName;
    public  AutoTrading(List<user_info> u,List<String> cN){
      this .userData = u;
      this.coinName = cN;
    }
    //코인을 구매하는 함수
    public  String buy_coin(Api_Client api,String coinName) throws IOException, JSONException {

        String coinPrice = getCoinPrice(coinName);
        HashMap<String, String> rgParams = new HashMap<String, String>();
        rgParams.put("currency", coinName);
        String result1 = api.callApi("/info/balance", rgParams);
        //System.out.println(result);
        JSONObject json = new JSONObject(result1);
        String data = json.getString("data"); //string()쓰기

        JSONObject json1 = new JSONObject(data);
        String total_krw = json1.getString("total_krw").toString();  //보유 자산 구하기

        Double unit = Double.parseDouble(total_krw)/Double.parseDouble(coinPrice)*0.69;
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
    public  void sell_coin(Api_Client api,String coinName,String unit) throws IOException {
        if(getAsset(api).contains(coinName)) { //코인이 없으면 못판다.
            try {
                HashMap<String, String> rgParams = new HashMap<String, String>();
                rgParams.put("units", unit); //소수점 4자리 맞추기
                rgParams.put("order_currency", coinName); //매도 하려는 코인 이름
                rgParams.put("payment_currency", "KRW"); //매도하려는 통화
                String result = api.callApi("/trade/market_sell", rgParams);
            } catch (Exception e) {
                System.out.println("에러 " + e);
            }
        }else{
            return;
        }
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
    public  void autoTrade(String name,String key, String sec) throws IOException, JSONException, InterruptedException {

        Api_Client api_client =  new Api_Client(key,sec);

        List<String> userAsset = getAsset(api_client);
        for(int i = 0 ;i<userAsset.size();i+=2){
            String unit = userAsset.get(i);
            String cname = userAsset.get(i+1);
            //System.out.println("이름: "+name+" : "+unit+" "+cname);
            String [] temp = unit.split("\\."); //특수문자앞에는 \\를 붙혀야함
            unit = temp[0] +"."+temp[1].substring(0,4);//소수점 4자리까지만 자름
            sell_coin(api_client,cname,unit); //전량 매도
        }

        while(true){
            Random random = new Random();
            int num = random.nextInt(coinName.size());
            String randomCoin = coinName.get(num);
           String unit =  buy_coin(api_client,randomCoin);
           System.out.println(name + ": "+randomCoin+"매수 ");
           Thread.sleep(600000);
           sell_coin(api_client,randomCoin,unit);
            System.out.println(name + ": "+randomCoin+"매도 ");
        }
    }
    public void autoRandomTrade() {
        ExecutorService executor = Executors.newFixedThreadPool(userData.size());
        for (int i = 0; i < userData.size(); i++) {
            final int idx= i;
            executor.execute(() -> {
                try {
                    autoTrade(userData.get(idx).getIdentity(),userData.get(idx).getApi_key(),userData.get(idx).getSec_key());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    public static void main(String[] args) throws InterruptedException {

    }
}
