
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dcs on 16-6-12.
 * 根据指定项目目录下（可以认为是java源文件目录）中，
 * 统计被import最多的类，前十个是什么。
 */
public class CountMostImport {

    private static String defaultPath="./";
    /**
     * 从java文件中读取内容
     * @param file
     */
    public static String getFileContent(File file)
    {
        String str=null;
        StringBuffer buffer=new StringBuffer();
        InputStream inputStream=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;

        try
        {
            inputStream=new FileInputStream(file);
            inputStreamReader=new InputStreamReader(inputStream);
            bufferedReader=new BufferedReader(inputStreamReader);

           while(null!=(str=bufferedReader.readLine()))
           {
               buffer.append(str);
           }
        }
        catch (FileNotFoundException e)
        {
           e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            try{
                bufferedReader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try{

                inputStreamReader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            try{
                inputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return buffer.toString();
    }

    /**
     *
     * @param map
     * @param buffer
     */
    public static void getImportNumber(Map<String,Integer>map,String buffer)
    {
        if(null!=buffer)
        {
            //System.out.println("+++");
            String matchstr=null;
            Pattern pattern=Pattern.compile("import(.*?);");
            Matcher matcher=pattern.matcher(buffer);

            while(matcher.find())
            {
               // System.out.println(buffer);
                for(int i=0;i<matcher.groupCount();i++)
                {
                    matchstr=matcher.group(i);
                   /* System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
                    System.out.println(buffer);
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++");*/
                    //System.out.println("*********************************");
                   // System.out.println(matchstr);
                   // System.out.println("*********************************");
                    if(!map.containsKey(matchstr))
                    {
                        map.put(matchstr,1);
                    }
                    else
                    {
                        map.put(matchstr,map.get(matchstr)+1);
                    }
                }
            }
        }
    }

    /**
     *使用递归方法建立哈希表
     * @param file
     * @param map
     */
    public static void getImportCount(File file,Map<String,Integer>map)
    {
        String buffer=null;
        File[] list=null;
        if(null!=file)
        {
            if(file.isFile())
            {
                //System.out.println(file.toString());
                buffer=getFileContent(file);
                //System.out.println("+++++++++++++++++++++++");
               // System.out.println(buffer);
                //System.out.println("+++++++++++++++++++++++");
                getImportNumber(map,buffer);
            }
            else
            {


                list=file.listFiles();
                if(list!=null)
                {
                    for (int i=0;i<list.length;i++)
                    {
                        getImportCount(list[i],map);
                    }
                }

            }
        }
    }


    public static ArrayList<Map.Entry<String,Integer>> getMaxImport(Map<String,Integer>map)
    {
        String str=null;
        ArrayList<Map.Entry<String,Integer>> result=null;
        Set<Map.Entry<String,Integer>> set=map.entrySet();
        List<Map.Entry<String,Integer>> list=new ArrayList<Map.Entry<String, Integer>>(set);
        list.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
                if(obj1.getValue()<obj2.getValue())
                    return 1;
                else if(obj1.getValue()> obj2.getValue())
                    return -1;
                return 0;
            }
        });
       // str=list.get(0).getKey().toString();
        result=new ArrayList<Map.Entry<String, Integer>>();
        for(int i=0;i<list.size();i++)
        {
            result.add(list.get(i));
            if(9==i)
                break;;
        }
        return result;
    }


    public static void main(String[] args) {
        //String filePath="/home/dcs/IdeaProjects1/test1/src/";
        String filePath=CountMostImport.defaultPath;
        Map<String,Integer>map=new HashMap<String, Integer>();
        List<Map.Entry<String,Integer>>list=null;
        

        File initFile=new File(filePath);
        getImportCount(initFile,map);

        list=getMaxImport(map);
        if(list==null)
            System.out.println("heihie");
        for(int i=0;i<list.size();i++)
        {
            System.out.println("class: "+list.get(i).getKey()+"   value:+"+list.get(i).getValue());
        }
    }
}
