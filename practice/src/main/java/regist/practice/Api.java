package regist.practice;

import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.persistence.EntityManager;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.HashMap;

public class Api {
    public static void main(String[] args) throws IOException, JSONException {

//        Api_Client api=new Api_Client("a79601606b3395da7111c73baf25769f","b3d5890e6a5c39d89065cb3d1fcf0e75");
//        HashMap<String, String> rgParams = new HashMap<String, String>();
//
//        api.callApi("/info/balance",rgParams);
//
//        try {
//            String result = api.callApi("/info/balance", rgParams);
//            System.out.println(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        OkHttpClient client =new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("https://api.bithumb.com/info/account")
//                .post(null)
//                .addHeader("accept", "application/json")
//                .addHeader("content-type", "application/x-www-form-urlencoded")
//                .addHeader("api-client-type", "0")
//                .addHeader("Api-Key", "a79601606b3395da7111c73baf25769f")
//                .addHeader("Api-Nonce", Long.toString(System.currentTimeMillis()))
//                .addHeader("Api-Sign", "상세 가이드 참고")
//                .build();
//        Response response = client.newCall(request).execute();
//        ResponseBody body=response.body();
//
//        String endpoint="/info/balance&order_currency=BTC&payment_currency=KRW";
//
//
//        JSONObject json = new JSONObject(body.string());
//        System.out.println("hi"+json.getString("status"));
//        String temp=json.getString("data");
//        JSONObject json2 = new JSONObject(temp);
//        System.out.println("hi"+json2.getString("opening_price"));

        String total_krw = "";
        String result="";
        String apiKey = "";
        String apiSec = "";
        Api_Client call_api = new Api_Client(apiKey,apiSec);
        try {

            HashMap<String, String> rgParams = new HashMap<String, String>();
            rgParams.put("currency", String.valueOf("BTC"));
            result = call_api.callApi("/info/balance", rgParams);

            JSONObject json = new JSONObject(result);
            String data = json.getString("data"); //string()쓰기

            JSONObject json1 = new JSONObject(data);
            total_krw = json1.getString("total_krw").toString();
        }
        catch (Exception e) {

        }

        System.out.println("krw:"+total_krw);

    }
}
