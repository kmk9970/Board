package regist.practice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import regist.practice.Api_Client;
import regist.practice.domain.TransactionHistory;
import regist.practice.service.BoardService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ExcelController {
    private  final BoardService service;

    @GetMapping("/excelinsert")
    public String main() { // 1
        return "excelinsert";
    }

    @PostMapping("/excelinsert/read")
    public String readExcel(@RequestParam("file") MultipartFile file, Model model, HttpServletRequest request)

            throws IOException { // 2

        HttpSession session = request.getSession();
        String userId = String.valueOf(session.getAttribute("ID"));
        String extension = FilenameUtils.getExtension(file.getOriginalFilename()); // 3

        if (!extension.equals("xlsx") && !extension.equals("xls")) {
            throw new IOException("엑셀파일만 업로드 해주세요.");
        }

        Workbook workbook = null;

        if (extension.equals("xlsx")) {
            workbook = new XSSFWorkbook(file.getInputStream());
        } else if (extension.equals("xls")) {
            workbook = new HSSFWorkbook(file.getInputStream());
        }

        Sheet worksheet = workbook.getSheetAt(0);
        List<TransactionHistory> ans = service.getUserTrade(userId); //회원의 거래기록
        Map<String,String> memo = new HashMap<>();//DB 테이블에 있는 행을 저장함
        System.out.println("총 행 개수: "+worksheet.getPhysicalNumberOfRows());
        long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기

        for(int i = 0; i<ans.size();i++){
            String hashKey =  ans.get(i).getCoinName() +
                    ans.get(i).getState() + ans.get(i).getCoinDate();
            memo.put(hashKey,"A");
        }
        //넣을 때 역순으로 넣기

        int idx =  worksheet.getPhysicalNumberOfRows()-1;
//        for (int i = worksheet.getPhysicalNumberOfRows()-1; i >2 ; i--) { //

        //한번에 매매(같은 시간에 매매)됬지만 n번으로 분할되어 매매되었을 때 하나로 묶기
        Map<String,List<String>> unionDuple = new HashMap<>();  //엑셀데이터에서 같은값 하나로 묶기위해
        //
        while (idx >2){
            Row row = worksheet.getRow(idx--);
            String coinState = row.getCell(2).getStringCellValue();
            if (coinState.length() ==4){
                if  (coinState.substring(2).equals("매수")) {
                    coinState ="매수";
                }
                else if  (coinState.substring(2).equals("매도")){
                    coinState ="매도";
                }
            }
            String hashKey = row.getCell(1).getStringCellValue()+
                    coinState+
                    row.getCell(0).getStringCellValue();
            List<String> mapValues = new ArrayList<>();
            if(memo.containsKey(hashKey)){ //중복된 값이 있으면 넣지 않기
                continue;
            }
            String coinDate = row.getCell(0).getStringCellValue();
            String coinName = row.getCell(1).getStringCellValue();
            String tempTradeAmount = row.getCell(3).getStringCellValue();
            String tempTradePrice = row.getCell(4).getStringCellValue();
            String fee = row.getCell(6).getStringCellValue();
            String tempAfterPrice = row.getCell(7).getStringCellValue();

            tempTradeAmount = tempTradeAmount.split(" ")[0].replace(",",""); //거래량 뒤에 krw 문자열 제외
            tempTradePrice = tempTradePrice.split(" ")[0].replace(",","");  //체결가격 뒤에 krw 문자열 제외
            tempAfterPrice = tempAfterPrice.split(" ")[0].replace(",","");  // 정산금약 뒤에 krw 문자열 제외
            mapValues.add(coinDate);    //0
            mapValues.add(coinName);    //1
            mapValues.add(coinState);   //2
            mapValues.add(tempTradeAmount);   //3 !
            mapValues.add(tempTradePrice);      //4!
            mapValues.add(fee);         //5
            mapValues.add(tempAfterPrice); //6 !
            if(unionDuple.containsKey(hashKey)){ //중복된 값(같은 매매인데 n번 체결된것)
                //값 갱신 해주기
                List<String> temp = unionDuple.get(hashKey);
                Double TradeAmount = Double.valueOf(temp.get(3))+ Double.valueOf(mapValues.get(3));
                temp.set(3,String.valueOf(TradeAmount));

//                Double TradePrice = Double.valueOf(temp.get(4))+Double.valueOf(mapValues.get(4));
//                temp.set(4,String.valueOf(TradePrice));
                Double AfterPrice = 0.0;
                if(temp.get(6).substring(0,1).equals("+")){

                    AfterPrice= Double.valueOf(temp.get(6).substring(1))+
                            Double.valueOf(mapValues.get(6).substring(1));
                }
                else if(temp.get(6).substring(0,1).equals("-")){
                    AfterPrice= -Double.valueOf(temp.get(6).substring(1))-
                            Double.valueOf(mapValues.get(6).substring(1));
                }
                if(AfterPrice> 0.0 ){
                    temp.set(6,"+"+String.valueOf(AfterPrice));

                }else if(AfterPrice < 0.0){
                    temp.set(6,"-"+String.valueOf(AfterPrice));
                }else {
                    temp.set(6,"+0");
                }
                continue;
            }
            unionDuple.put(hashKey,mapValues);
        }
        for(List<String> li : unionDuple.values()){
            TransactionHistory e =new TransactionHistory();
            e.setUser(userId);
            e.setCoinDate( li.get(0));
            e.setCoinName( li.get(1));
            e.setState( li.get(2)); //상태
            e.setAmount( li.get(3));//거래수량
            e.setPrice( li.get(4)); //체결가격
            e.setFee( li.get(5)); //수수료
            e.setAfterTrade(li.get(6)); //정산 금액
            service.excelDataSave(e);
        }
        //service.flushAll();

        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
        System.out.println("시간차이(m) : "+secDiffTime);
        return "excelInsert";

    }



    //이거 자산 증가율이네?
    public String getProfit(HttpServletRequest request){ //(처음현금+팔아서 얻은 금액 - 살때 쓴 금액)/처음 가지고 있던 현금*100

        HashMap<String,Integer> buy=new HashMap<String,Integer>(); //매수
        HashMap<String,Integer> sell=new HashMap<String,Integer>(); //매도
        insertValue(service.GetexcelData("매수"),buy);//state가 매수인 애들만 불러오기
        insertValue(service.GetexcelData("매도"),sell); //state가 매도인 애들만 불러오기
        List<String> key=new ArrayList(buy.keySet());
        int total_profit=0;
        int total_invest=0;
        for (int i=0;i<key.size();i++){
            if((sell.get(key.get(i))!=null)&(buy.get(key.get(i))!=null)){
                int profit=sell.get(key.get(i)) - buy.get(key.get(i));
                System.out.println(key.get(i) + ":" + profit);
                total_profit+=profit;
                total_invest+=buy.get(key.get(i));
            }
            else{
                total_invest+=buy.get(key.get(i));
                continue;
            }
        }
        System.out.println("total profit:"+total_profit);
        System.out.println("total invest:"+total_invest);
        double temp=(double)(total_profit-total_invest)/5000*100;
        System.out.println("total profit rate:"+temp+"%");


        return "excel";
    }
// 11/21 -9.33

    @GetMapping("excelbydate")
    public String show_datepage(){
            return "excelbydate";
    }

    @PostMapping("/excelbydate/read") //현재 수정중
    public String getProfitByDate(String date1, String date2,HttpServletRequest request){ //날짜 데이터 2개를 입력받아 그 사이의 값을 구해주는 함수
        HttpSession session = request.getSession();
        HashMap<String,Integer> buy=new HashMap<String,Integer>(); //매수
        HashMap<String,Integer> sell=new HashMap<String,Integer>(); //매도
        int profit=0;
        int total_profit=0; //총 손익
        int total_invest=0; //사용금액
        insertValue(service.GetDataByDate("매수",date1,date2),buy);//state가 매수인 애들만 불러오기
        insertValue(service.GetDataByDate("매도",date1,date2),sell); //state가 매도인 애들만 불러오기

        int cash=getHoldedCash(String.valueOf(session.getAttribute("apiKey")), String.valueOf(session.getAttribute("apiSec")), date1);
        System.out.println("holded cash at that time:"+cash);

        List<String> key=new ArrayList(buy.keySet());
        //손익을 구한다
        for(int i=0;i<key.size();i++){
            total_invest+=buy.get(key.get(i));
        }
        key= new ArrayList(sell.keySet());
        for(int i=0;i<key.size();i++){
            total_profit+=sell.get(key.get(i));
        }

        System.out.println("total profit:"+total_profit); //코인 판매 수익
        System.out.println("total invest:"+total_invest); //코인 구매 비용
        double ProfitRate=(double)(cash+total_profit-total_invest)/cash*100;
        System.out.println("total profit rate:"+ProfitRate+"%");

        return "excelbydate";
    }

    public void insertValue(List<TransactionHistory> e,HashMap<String, Integer> h){ //해시테이블도 하나 받아와야함
        String temp;
        for(int i=0;i<e.size();i++){ //해시테이블에 값 넣기
            if(h.containsKey(temp=e.get(i).getCoinName())){ //있으면 업데이트
                h.replace(temp,h.get(temp)+Integer.parseInt(
                        e.get(i).getAfterTrade()
                                .replace(",","")
                                .replace(" KRW","")
                                .replace("+","")
                                .replace("-","")));
            }
            else {
                h.put(e.get(i).getCoinName(), Integer.parseInt( //없으면 생성
                        e.get(i).getAfterTrade()
                                .replace(",", "")
                                .replace(" KRW", "")
                                .replace("+", "")
                                .replace("-", "")
                ));
            }
        }
    }

    public int getHoldedCash(String apiKey, String apiSec, String date) //특정시점의 현금보유량 계산
    {
        if(date.equals("")){
            date="0000-01-01";
        }
        Double cash=getUserCash(apiKey,apiSec); //현재 보유 현금가져오고
        int sum=0; //손익을 계산하기 위한 변수
        HashMap<String,Integer> buy=new HashMap<String,Integer>(); //매수
        HashMap<String,Integer> sell=new HashMap<String,Integer>(); //매도
        insertValue(service.requestProftandLoss(date,"매수"),buy);//state가 매수인 애들만 불러오기
        insertValue(service.requestProftandLoss(date,"매도"),sell); //state가 매도인 애들만 불러오기

        List<String> key=new ArrayList<>(buy.keySet());
        for(int i=0;i<key.size();i++){
            sum+=buy.get(key.get(i));
        }
        key=new ArrayList<>(sell.keySet());
        for(int i=0;i<key.size();i++){
            sum-=sell.get(key.get(i));
        }
        //입력받은 시점부터 현재까지의 손익

        System.out.println("loss-profit:"+sum);
        System.out.println("cash:"+cash);
        return (int)Math.floor(cash)+sum;

    }
    public static Double getUserCash (String apiKey, String apiSec){
        String total_krw = "";
        String result="";
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

        return Double.parseDouble(total_krw);
    }

}
