package regist.practice.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import regist.practice.domain.TransactionHistory;
import regist.practice.service.ContentService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ExcelController {
    private  final ContentService service;
    @GetMapping("/excel")
    public String main() { // 1
        return "excel";
    }


    @PostMapping("/excel/read")
    public String readExcel(@RequestParam("file") MultipartFile file, Model model)
            throws IOException { // 2


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
        List<TransactionHistory> ans = new ArrayList<>();
        System.out.println("총 행 개수: "+worksheet.getPhysicalNumberOfRows());
        long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
        for (int i = 3; i < worksheet.getPhysicalNumberOfRows(); i++) { // 4
            Row row = worksheet.getRow(i);

            TransactionHistory e =new TransactionHistory();
            e.setUser("조승빈"); ///이거 고치기
            e.setCoinName(row.getCell(1).getStringCellValue());
            e.setState(row.getCell(2).getStringCellValue());
            e.setAmount(row.getCell(3).getStringCellValue());
            e.setPrice(row.getCell(4).getStringCellValue());

            e.setFee(row.getCell(6).getStringCellValue());
            e.setAfterTrade(row.getCell(7).getStringCellValue());

            service.excelDataSave(e);
        }
        //service.flushAll();

        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
        System.out.println("시간차이(m) : "+secDiffTime);


        return "excel";

    }
}
