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
        String data = json.getString("data"); //string()����

        JSONObject json1 = new JSONObject(data);
        String total_krw = json1.getString("total_krw").toString();  //���� �ڻ� ���ϱ�

        if( Double.parseDouble(total_krw)*0.69<price){
            return "0";
        }

        Double unit = price/Double.parseDouble(coinPrice)*0.69;
        if(Double.parseDouble(coinPrice)*unit>Double.parseDouble(total_krw)*0.69&Double.parseDouble(total_krw)<500){
            return "0";
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.####");

        // �߶󳻱�
        String formattedNumber = decimalFormat.format(unit);

        HashMap<String, String> rgParams1 = new HashMap<String, String>();
        rgParams1.put("units", formattedNumber); //�Ҽ��� 4�ڸ� ���߱�=���μ���
        rgParams1.put("order_currency", coinName); //�ż� �Ϸ��� ���� �̸�
        rgParams1.put("payment_currency", "KRW"); // �ż��Ϸ��� ��ȭ
        String result = api.callApi("/trade/market_buy", rgParams1);
        System.out.println("���"+ result);
        return formattedNumber;
    }
    public int sell_coin(Api_Client api,String coinName,String unit) throws IOException {
        List<String> holdcoin=getAsset(api);

        if(holdcoin.contains(coinName)) { //������ ������ ���Ǵ�.
            try {
                int index=holdcoin.indexOf(coinName);
                if(Double.parseDouble(holdcoin.get(index-1))<Double.parseDouble(unit)){//not enough coin
                    return 0;
                }
                HashMap<String, String> rgParams = new HashMap<String, String>();
                rgParams.put("units", unit); //�Ҽ��� 4�ڸ� ���߱�
                rgParams.put("order_currency", coinName); //�ŵ� �Ϸ��� ���� �̸�
                rgParams.put("payment_currency", "KRW"); //�ŵ��Ϸ��� ��ȭ
                String result = api.callApi("/trade/market_sell", rgParams);
                return 1;
            } catch (Exception e) {
                System.out.println("���� " + e);
            }
        }
        return 0;
    }
    public List<String> getAsset(Api_Client api_client){ //������ ���� ���� �ҷ�����
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
                String data = json.getString("data"); //string()����

                JSONObject json1 = new JSONObject(data);
                total_krw = json1.getString("total_krw").toString();  //���� �ڻ� ���ϱ�
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

    //������ ������ �˾ƿ��� �Լ�
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
        //��� �����̸��� �ҷ����� �Լ�
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
        Iterator jsonKeys=  dataJson.keys();  //json�� ��� Ű������ �ҷ����� �ڵ�(���� �̸��� Ű��)?>��� ���� �̸� �ҷ�����
        List<String> coinName = new ArrayList<>();
        while(jsonKeys.hasNext())
        {
            String cN = jsonKeys.next().toString();
            coinName.add(cN); // Ű �� ����
        }
//        for(int i = 0 ; i<coinName.size();i++){
//            System.out.println(coinName.get(i));
//        }
        return coinName ;
    }


}
