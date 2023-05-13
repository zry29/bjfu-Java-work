import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;


import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Label;


public class TermFrequency {

    public static void Keyword(List KeywordList) throws IOException {
        String fileName = "Keywords.txt";
        int KeywordLength = 0;

        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();

        while (line != null) {
            if (line.length() > KeywordLength) {
                KeywordLength = line.length();
            }
            line = bufferedReader.readLine();
        }
        for (int i = KeywordLength; i > 0; i--) {
            fileReader = new FileReader(fileName);
            bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();

            while (line != null) {
                if (line.length() == i) {
                    KeywordList.add(line);
                }
                line = bufferedReader.readLine();
            }
        }

        bufferedReader.close();
        fileReader.close();
    }

    public static void OutputResult(List KeywordList) throws IOException, BiffException, WriteException {
        Workbook workbook = Workbook.getWorkbook(new File("SourceText.xls"));
        Sheet sheet = workbook.getSheet(0);

        String[] a = new String[1000007];
        int StockNumberMax = 0;
        for (int i = 1; i < sheet.getRows(); i++) {
            Cell c1 = sheet.getCell(0, i);
            String s1 = c1.getContents();
            int inum1 = Integer.parseInt(s1);
            if (inum1 > StockNumberMax) {
                StockNumberMax = inum1;
            }
            for (int j = 2; j < sheet.getColumns(); j++) {
                Cell cell = sheet.getCell(j, i);
                String s = cell.getContents();
                s = s.replace("\\s*", "");
                s = s.replace("\n", "");
                if (a[inum1] == null) {
                    a[inum1] = s;
                } else {
                    a[inum1] = a[inum1] + s;
                }
            }
        }
        workbook.close();
        

        File file = new File("Result.xls");
        file.createNewFile();
        WritableWorkbook workbook1 = Workbook.createWorkbook(file);
        WritableSheet sheet1 = workbook1.createSheet("result", 0);
        String[] titles = {"股票代号", "文本长度", "关键词个数", "关键词长度", "关键词密度", "关键词列表"};
        Label label = null;
        for (int m = 0; m < titles.length; m++) {
            label = new Label(m, 0, titles[m]);
            sheet1.addCell(label);
        }

        int StockKeywordLength;
        int r = 0;
        for (int i = 1; i <= StockNumberMax; i++) {
            if (a[i] != null) {
                r++;
                StockKeywordLength = 0;
                List<String> StockKeywordList = new ArrayList<String>();
                for (int j = 0; j < a[i].length(); j++) {
                    for (int k = 0; k < KeywordList.size(); k++) {
                        String key = (String) KeywordList.get(k);
                        if (j + key.length() <= a[i].length()) {
                            String stockstring = a[i].substring(j, j + key.length());
                            if (key.equals(stockstring)) {
                                StockKeywordList.add(key);
                                StockKeywordLength += key.length();
                                j = j + key.length() - 1;
                                break;
                            }
                        }
                    }
                }

                String resultnumber = String.format("%06d", i);
                label = new Label(0, r, resultnumber);
                sheet1.addCell(label);
                String resultlength = String.valueOf(a[i].length());
                label = new Label(1, r, resultlength);
                sheet1.addCell(label);
                String resultkeywordnumber = String.valueOf(StockKeywordList.size());
                label = new Label(2, r, resultkeywordnumber);
                sheet1.addCell(label);
                String resultkeywordlength = String.valueOf(StockKeywordLength);
                label = new Label(3, r, resultkeywordlength);
                sheet1.addCell(label);
                double frequency = (double) StockKeywordLength / (double) a[i].length();
                String resultfrequency = String.valueOf(frequency);
                label = new Label(4, r, resultfrequency);
                sheet1.addCell(label);
                String resultlist = String.valueOf(StockKeywordList);
                resultlist = resultlist.replace(" ", "");
                resultlist = resultlist.replace("[", "");
                resultlist = resultlist.replace("]", "");
                label = new Label(5, r, resultlist);
                sheet1.addCell(label);

                /*检查
                System.out.println(StockKeywordList.size());
                System.out.println(StockKeywordLength);
                for (int kk = 0; kk < StockKeywordList.size(); kk++) {
                    System.out.println(StockKeywordList.get(kk));
                }
                System.out.println("===========================");
                StockKeywordList.clear();*/
            }
        }
        workbook1.write();
        workbook1.close();

    }

    public static void test() throws IOException, BiffException, WriteException {
        List<String> KeywordList = new ArrayList<String>();
        Keyword(KeywordList);
        OutputResult(KeywordList);
    }

    public static void main(String[] args) throws IOException, BiffException, WriteException {
        test();
    }
}
