package regist.practice;

import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.persistence.EntityManager;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

public class Api {
    public static void main(String[] args) throws IOException, JSONException {
        EntityManager EM;
//        OkHttpClient client =new OkHttpClient();
//        Request requset = new Request.Builder()
//                .url("https://api.bithumb.com/public/ticker/BTC_KRW")
//                .get()
//                .addHeader("accept","application.json")
//                .build();
//        Response response = client.newCall(requset).execute();
//        ResponseBody body=response.body();
//
//        JSONObject json = new JSONObject(body.string());
//        System.out.println("hi"+json.getString("data"));
//        String temp=json.getString("data");
//        JSONObject json2 = new JSONObject(temp);
//        System.out.println("hi"+json2.getString("opening_price"));

        OkHttpClient client =new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.bithumb.com/info/account")
                .post(null)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Api-Key", "a79601606b3395da7111c73baf25769f")
                .addHeader("Api-Nonce", Long.toString(System.currentTimeMillis()))
                .addHeader("Api-Sign", "상세 가이드 참고")
                .build();
        Response response = client.newCall(request).execute();
        ResponseBody body=response.body();

        JSONObject json = new JSONObject(body.string());
        System.out.println("hi"+json.getString("status"));
//        String temp=json.getString("data");
//        JSONObject json2 = new JSONObject(temp);
//        System.out.println("hi"+json2.getString("opening_price"));
    }
}
