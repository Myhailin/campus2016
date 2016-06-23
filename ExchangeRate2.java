import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by dcs on 16-6-18.
 * 分析从今天开始过去30天时间里，中国人民银行公布的人民币汇率中间价，
 * 得到人民币对美元、欧元、港币的汇率，形成excel文件输出。
 */
public class ExchangeRate2 {

    //创建一个工作表
    public static WritableWorkbook creatExcelbook(File file)  {

        WritableWorkbook workbook=null;
        try
        {
            //c创建工作簿
            workbook= Workbook.createWorkbook(file);
            //创建新页
            WritableSheet sheet=workbook.createSheet("FirstSheet",0);
            Label datesheet=new Label(0,0,"日期");
            sheet.addCell(datesheet);
            Label rateUSA=new Label(1,0,"对美元汇率");
            sheet.addCell(rateUSA);
            Label rateEurope=new Label(2,0,"对欧元汇率");
            sheet.addCell(rateEurope);
            Label rateHongkong=new Label(3,0,"对港币汇率");
            sheet.addCell(rateHongkong);


            workbook.write();
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (WriteException e)
        {
            e.printStackTrace();
        }

        return workbook;
    }

    /**
     * 日期减1
     * @param date
     * @param n
     * @return
     */
    private static String dateSubOne(Date date, int n)
    {
        String datestr=null;
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR,-n);
        Date date1=calendar.getTime();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
        datestr=format.format(date1);
        return datestr;
    }


    //将获得的有用信息添加到excel文件中，一次写入一条
    public static void excelWrite(Elements useInfo, int n, String filename)
    {
        //WritableSheet sheet=workBook.getSheet(0);
        //获得EXcel文件
        Workbook wb=null;
        WritableWorkbook workBook=null;

        try {
            wb=Workbook.getWorkbook(new File(filename));
            Sheet sheet=wb.getSheet(0);
            //获取行
            int len=sheet.getRows();
            //System.out.println("当前表的行数为："+len);
            //根据wb创建一个操作对象
            workBook=Workbook.createWorkbook(new File(filename),wb);
            //根据wb创建一个工作对象
            WritableSheet sheet1=workBook.getSheet(0);
            //从最后一行开始添加
            Elements elements=useInfo.get(1).getElementsByTag("td");
            sheet1.addCell(new Label(0,n,elements.get(0).text()));
            sheet1.addCell(new Label(1,n,elements.get(1).text()));
            sheet1.addCell(new Label(2,n,elements.get(2).text()));
            sheet1.addCell(new Label(3,n,elements.get(4).text()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        catch (WriteException e) {
            e.printStackTrace();
        }
        try {
            //sheet.addCell(number);

            workBook.write();
            //System.out.println(n);
            workBook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 在表格中写入多条数据
     * @param useInfo
     * @param filename
     */
    public static void excelWrite1(Elements useInfo, String filename)
    {
        //WritableSheet sheet=workBook.getSheet(0);
        //获得EXcel文件
        Workbook wb=null;
        WritableWorkbook workBook=null;

        try {
            wb=Workbook.getWorkbook(new File(filename));
            Sheet sheet=wb.getSheet(0);
            //获取行
            int len=sheet.getRows();
            //System.out.println("当前表的行数为："+len);
            //根据wb创建一个操作对象
            workBook=Workbook.createWorkbook(new File(filename),wb);
            //根据wb创建一个工作对象
            WritableSheet sheet1=workBook.getSheet(0);
            //从最后一行开始添加
            for(int i=1;i<useInfo.size()&&i<=30;i++)
            {
                Elements elements=useInfo.get(i).getElementsByTag("td");
                sheet1.addCell(new Label(0,i,elements.get(0).text()));
                sheet1.addCell(new Label(1,i,elements.get(1).text()));
                sheet1.addCell(new Label(2,i,elements.get(2).text()));
                sheet1.addCell(new Label(3,i,elements.get(4).text()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        catch (WriteException e) {
            e.printStackTrace();
        }
        try {
            //sheet.addCell(number);

            workBook.write();
            //System.out.println(n);
            workBook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得网址上中间价的表格内容
     * @param param 请求参数
     * @param destUrl 请求网址
     * @return
     */
    public static Elements getExchangeRate(String param, String destUrl) {


        Document document = null;
        try {
            document = Jsoup.connect(destUrl + param).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(document==null)
            return null;
        else
        {
            Element element = document.getElementById("InfoTable");

            if(null==element)
            {
                System.out.println(param.substring(53)+"该日期没有中间价记录");
                return null;
            }
            else
            {
                Elements useInfo = element.getElementsByTag("tr");

                return useInfo;
            }


        }

    }



    public static void main(String[] args) {
        String destUrl="http://www.safe.gov.cn/AppStructured/view/project_RMBQuery.action?";
        String param=null;
        Date date=new Date();
        //创建一个工作表
        String fileName="/home/dcs/projects/huilv.xls";
        File file=new File(fileName);
        WritableWorkbook workbook= null;
            workbook = creatExcelbook(file);


        //多读取一些数据，有些日期没有中间价，容易连接超时
       /* int dateRange=40;
        String dateStart=dateSubOne(date,dateRange);
        String dateEnd=dateSubOne(date,0);
        param="projectBean.startDate=" +dateStart+ "&projectBean.endDate=" + dateEnd;
        Elements useInfo=getExchangeRate(param,destUrl);
        if (useInfo!=null)
        {
            excelWrite1(useInfo,fileName);
        }*/

        //每次读取一条数据，可能有点低
        int i=0;
        int n=0;
        Elements useInfo=null;
        while(true)
        {
            String datestr=dateSubOne(date,n);
            param="projectBean.startDate=" +datestr+ "&projectBean.endDate=" + datestr;
            useInfo=getExchangeRate(param,destUrl);
            if (useInfo!=null)
            {
                i++;
                excelWrite(useInfo,i,fileName);
            }
            if (30==i)
            {
                break;
            }
            n++;
        }
    }
}
