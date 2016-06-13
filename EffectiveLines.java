/**
 * Created by dcs on 16-6-8.
 *
 *一、统计一个Java文件的有效行数。
 1、有效不包括空行
 2、不考虑代码见有多行注释的情况
 */
import java.io.*;

public class EffectiveLines
{

    public static void main(String[] args)
    {
        //首先判断一个文件是否为文件，是否为java文件
        // String path=args[0];
        String path="/home/dcs/JavaCodes/test.java";
        // String path="/home/dcs/IdeaProjects/test1/src/CountLines.java";
        if(path.matches(".*\\.java$"))// 匹配字符串是否符合一般命名规则，以.java结尾，不要求文件命名规则时可省略
        {
            int effectiveLines=0;
            int blankLines=0;
            int singleNoteLines=0;
            int mutiNoteLines=0;

            File f=new File(path);
            if(f.isFile())//鉴于linux中的文件命名方式，判断一下是否为文件类型
            {
                FileReader fileReader=null;
                BufferedReader bufferedReader=null;
                try {

                    fileReader=new FileReader(f);
                    bufferedReader=new BufferedReader(fileReader);
                }
                catch (FileNotFoundException e)
                {
                    System.out.println("File not Found");
                    e.printStackTrace();
                }

                String str=null;
                if(null!=bufferedReader)
                {
                    try{
                        while(null!=(str=bufferedReader.readLine()))
                        {
                            if(str.matches("^\\s*$")) //blank lines
                            {
                                blankLines++;
                            }
                            else if(str.matches("\\s*//*/.*")) //single note lines
                            {
                                singleNoteLines++;
                            }
                            else if(str.matches("^\\s*//*\\*.*$")) //muti note lines
                            {
                                mutiNoteLines++;
                                if(str.matches("\\s*(/\\*.*\\*/)*\\s*$"))//多行注释在一行中,可能有多个
                                {
                                }
                                else //多行注释位于多行
                                {
                                    do{
                                        str=bufferedReader.readLine();
                                        mutiNoteLines++;
                                    }
                                    while(!str.matches(".*\\*/.*"));

                                    if(!str.matches(".*\\*/\\s*$"))//此处将*/后可能出现的非空白字符视为有效代码行，不考虑接连的// 和多行注释
                                    {
                                        mutiNoteLines--;
                                        effectiveLines++;
                                        System.out.println(str);
                                    }
                                }
                            }
                            else
                            {
                                effectiveLines++;
                                System.out.println(str);
                            }

                            //只考虑有效行,不考虑多行注释时可用以下判断
                            /*if((!str.matches("^\\s*$"))&&(!str.matches("\\s*//**//*//*.*")))
                               {
                                     effectiveLines++;
                                     System.out.println(str);
                                }*/

                        }
                    }
                    catch (IOException e)
                    {
                        System.out.println("IOException has been found!");
                        e.printStackTrace();
                    }
                }

                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");

                System.out.println("The efictive lines of this File is: "+effectiveLines);
                               /* System.out.println("Blank lines: "+blankLines);
                                System.out.println("Single note lines: "+singleNoteLines);
                                System.out.println("Muti note lines: "+mutiNoteLines);*/
            }
            else
            {
                System.out.println("Not a File");
            }
        }
        else
        {
            System.out.println(" To make sure the file's name is ended with ： .java");
        }
    }

}
