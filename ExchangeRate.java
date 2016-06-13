/**
 * Created by dcs on 16-6-8.
 * 分析从今天开始过去30天时间里，中国人民银行公布的人民币汇率中间价，
 * 得到人民币对美元、欧元、港币的汇率，形成excel文件输出。
 */

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Number;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dcs on 16-6-11.
 */
public class ExchangeRate {
    /**
     * 获取网页源码
     * @ param destUrl 目标网址
     */

    private static String getJsCode(String destUrl)//目标网站需要验证，目前无法解决耶,正在努力尝试
    {
        StringBuffer buffer=null;
        BufferedReader bufferedReader=null;
        InputStream inputStream=null;
        InputStreamReader inputStreamReader=null;
        HttpURLConnection httpURLConnection=null;

        try
        {
            //建立连接
            URL url=new URL(destUrl);
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");

            //获取输入流
            inputStream=httpURLConnection.getInputStream();
            inputStreamReader=new InputStreamReader(inputStream,"utf-8");
            bufferedReader=new BufferedReader(inputStreamReader);

            //从输入流读取数据

            buffer=new StringBuffer();
            String str=null;
            while (null!=(str=bufferedReader.readLine()))
            {
                buffer.append(str);
            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(null!=bufferedReader)
            {
                try
                {
                    bufferedReader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            if(null!=inputStreamReader)
            {
                try
                {
                    inputStreamReader.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
            if(inputStream != null)
            {
                try
                {
                    inputStream.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if(httpURLConnection != null)
            {
                httpURLConnection.disconnect();
            }
        }

        return  buffer.toString();
    }


    /**
     * 从源码中提取出有用信息
     */
    private static List<String> getEffectiveInfo(String buffer)
   // private static List<String> getEffectiveInfo()
    {
       /* String buffer="<tbody> \n" +
                "          <tr> \n" +
                "           <td class=\"content\" colspan=\"2\" align=\"left\" valign=\"top\"> \n" +
                "            <div id=\"zoom\"> \n" +
                "             <p>中国人民银行授权中国外汇交易中心公布，2016年6月8日银行间外汇市场人民币汇率中间价为：1美元对人民币6.5593元，1欧元对人民币7.4503元，100日元对人民币6.1207元，1港元对人民币0.84458元，1英镑对人民币9.5366元，1澳大利亚元对人民币4.8845元，1新西兰元对人民币4.5691元，1新加坡元对人民币4.8533元，1瑞士法郎对人民币6.7978元，1加拿大元对人民币5.1474元，人民币1元对0.61724林吉特，人民币1元对9.8471俄罗斯卢布。</p>\n" +
                "             <p>　　</p>\n" +
                "             <p>　　</p>\n" +
                "             <p>　　</p>\n" +
                "             <p align=\"center\">　　中国外汇交易中心</p>\n" +
                "             <p align=\"center\">　　2016年6月8日</p> ";*/
        List<String >  effectiveInfo=null;
        if(null!=buffer)
        {
            effectiveInfo=new LinkedList<>();
            String rateForUSA=null;
            String rateForEurope=null;
            String rateForHokong=null;
            String allInfo=null;

            Pattern pattern1=Pattern.compile("1美元对人民币(.*?)元");
            Pattern pattern2=Pattern.compile("1欧元对人民币(.*?)元");
            Pattern pattern3=Pattern.compile("1港元对人民币(.*?)元");

            //取出有用字符串放在allInfo中再进行匹配

            //Pattern pattern=Pattern.compile("(.*)(<div id=\"zoom\">)(.*?)(</p>)(.*)");
            Pattern pattern=Pattern.compile("中国(.*)俄罗斯卢布。");
            Matcher matcher=pattern.matcher(buffer);
            if(matcher.find())
            {
                allInfo=matcher.group().toString();
                // System.out.println(allInfo);
                Matcher matcher1=pattern1.matcher(allInfo);
                if (matcher1.find())
                {
                    rateForUSA=matcher1.group().toString().substring(7,13);
                    // System.out.println(rateForUSA);
                    effectiveInfo.add(rateForUSA);
                }
                Matcher matcher2=pattern2.matcher(allInfo);
                if(matcher2.find())
                {
                    rateForEurope=matcher2.group().toString().substring(7,13);
                    //System.out.println(rateForEurope);
                    effectiveInfo.add(rateForEurope);
                }
                Matcher matcher3=pattern3.matcher(allInfo);
                if(matcher3.find())
                {
                    rateForHokong=matcher3.group().toString().substring(7,13);
                    //System.out.println(rateForHokong);
                    effectiveInfo.add(rateForHokong);
                }


            }
        }
        return effectiveInfo;
    }

    /**
     * 通过货币中间价网址来获得不同日期的中间价网址
     */
    //private static String getUrl(String buffer, String date)
    private static String getUrl(String buffer,String date)
    {
        /*String buffer= "        <td width=\"15\"></td> \n" +
                "        <td height=\"22\" align=\"left\"><font class=\"newslist_style\" style=\"margin-right:10px;\"><a href=\"/zhengcehuobisi/125207/125217/125925/3078093/index.html\" onclick=\"void(0)\" target=\"_blank\" title=\"2016年6月8日中国外汇交易中心受权公布人民币汇率中间价公告\">2016年6月8日中国外汇交易中心受权公布人民币汇率中间价公告</a></font><span class=\"hui12\">2016-06-08</span></td> \n" +
                "       </tr> \n" +
                "      </tbody> \n" +
                "       <tr> \n" +
                "        <td width=\"15\"></td> \n" +
                "        <td height=\"22\" align=\"left\"><font class=\"newslist_style\" style=\"margin-right:10px;\"><a href=\"/zhengcehuobisi/125207/125217/125925/3076916/index.html\" onclick=\"void(0)\" target=\"_blank\" title=\"2016年6月7日中国外汇交易中心受权公布人民币汇率中间价公告\">2016年6月7日中国外汇交易中心受权公布人民币汇率中间价公告</a></font><span class=\"hui12\">2016-06-07</span></td> \n" +
                "       </tr> \n" ;
               */

        String url=null;
        if(null!=buffer)
        {
            Pattern pattern=Pattern.compile("<a href=(.*?)"+date);
            Matcher matcher=pattern.matcher(buffer);

            Pattern pattern1=Pattern.compile("/zhengcehuobi(.*?).html");
            Matcher matcher1=null;
            if (matcher.find())
            {
                String findurl=matcher.group();
                matcher1=pattern1.matcher(findurl);
                if(matcher1.find())
                {
                    url=matcher1.group();
                    url="http://www"+url;
                }
            }
        }
        return url;
    }


    private static String dateSubOne(Date date,int n)
    {
        String datestr=null;
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR,-n);
        Date date1=calendar.getTime();
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日");
        datestr=format.format(date1);
        return datestr;
    }


    //创建一个工作表
    public static WritableWorkbook creatExcelbook(File file) throws BiffException {

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
            //Label dates=new Label(0,9,"2016年");
           // Number number=new Number(0,8,3.1232);

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

    //将获得的有用信息添加到excel文件中
    public static void excelWrite(List<String>list,int n,String filename)
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
            sheet1.addCell(new Label(0,n,list.get(3).toString()));
            sheet1.addCell(new Label(1,n,list.get(0).toString()));
            sheet1.addCell(new Label(2,n,list.get(1).toString()));
            sheet1.addCell(new Label(3,n,list.get(2).toString()));
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
     * main函数
     */

    public static void main(String[] args) {

        //货币中间价公告网址
        String pagestr="http://www.pbc.gov.cn/zhengcehuobisi/125207/125217/125925/index.html";
        String urlStr=null;
        Date date=new Date();
        String datestr=null;

        //创建一个工作表
        String fileName="/home/dcs/projects/huilv.xls";
        File file=new File(fileName);
        WritableWorkbook workbook= null;
        try {
            workbook = creatExcelbook(file);
        } catch (BiffException e) {
            e.printStackTrace();
        }

        //首先获得公告页面的信息
        String pageOfUrl=getJsCode(pagestr);

        String pageOfRate=null;
        List<String> effectiveInfo=new LinkedList<>();
        int n=0;
        while(n<30)
        {
            //获取时间string
            datestr=dateSubOne(date,n);
            //System.out.println(datestr);

            //获取特定日期的中间价额URL
            if(null!=pageOfUrl)
            {
                urlStr=getUrl(pageOfUrl,datestr);
            }
            //获取特定日期中间价的js源码
            if(null!=urlStr)
            {
                pageOfRate=getJsCode(urlStr);
            }
            //提取感兴趣的信息
            if(null!=pageOfRate)
            {
                effectiveInfo=getEffectiveInfo(pageOfRate);
            }

            effectiveInfo.add(datestr);
            //将获得的有用信息输出到EXCEl 中
            excelWrite(effectiveInfo,n+1,fileName);

            //将日期进行减1
            effectiveInfo.clear();
            n++;

        }

    }
}
