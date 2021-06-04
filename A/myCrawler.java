package A;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.File;
import java.io.FileWriter;

import java.net.URL;
import java.net.URLConnection;


import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
    


public class myCrawler
{
    static{
        //here we should initialize Current_Downloaded_File_Index from where it was left
        //same thing to be done with index of the urls to be crawled
        //basically a for loop
    }
    
    public static int Current_Downloaded_File_Index = 0;   //we download files with their index
        //example: 1.txt then 2.txt then 3.txt and so one,
        //and in the first line of each file, we put the url of this page so that
        //the indexer can link its conents to the url.


    public static int Current_URL_Index = 0; //we crawl URLS with their index

    public static void download(String stringurl, String Seed, String NameOfFile)throws IOException
    //NameOfFile is the name of the destination file which will hold the contents of our downloaded webpage
    {
        URL url = new URL(stringurl);
        InputStream Stream;
        try{
            URLConnection urlCon = url.openConnection();
            Stream = urlCon.getInputStream();
        }
        catch(Exception e)
        {
            return;
        }
        
        int i;
        NameOfFile = Integer.toString(Current_Downloaded_File_Index).concat(".html");
        Current_Downloaded_File_Index++;
        try{
            //TODO : change the directory of the folder to your directory1
            File myFile = new File("F:\\pages\\"+NameOfFile); //hard coded to put in folder "DownPages"
            if(myFile.createNewFile())
            {
                System.out.println("File " + NameOfFile + " Created");
            }
            else
            {
                IOException e = new IOException("File already exists");
                throw (e);
            }
        }
        catch (IOException e)
        {
            System.out.println("error occured");
            e.printStackTrace();
        }
        //TODO : change the directory of the folder to your directory2
        FileWriter Writer = new FileWriter("F:\\pages\\"+NameOfFile);
        try
        {
            AddUrl(Seed, stringurl);        //crawl the url for other urls and add them to urls.txt
            Current_URL_Index++;
            Writer.write(stringurl);        //(IMPORTANT)first line is always the url of the page
            Writer.append("\n");
            while((i = Stream.read()) != -1)
            {
                Writer.write((char)i);
            }
            Writer.write("\n \n");
            Writer.close();
            System.out.println("Page Downloaded !");
        }
        catch(IOException e)
        {
            System.out.println("error occured");
            e.printStackTrace();
        }
        

    }


    public static void AddUrl (String urlfile, String ourURL) throws IOException
    {
        //Document doc = Jsoup.connect(ourURL).userAgent("Mozilla").get();
        Document doc = Jsoup.connect(ourURL).get();
        Elements links = doc.select("a[href]");    //or "a"
        if(links.isEmpty())
        {
            return;
        }
        //string OutPut_Link = links.attr("href");
        File file = new File(urlfile);
        FileWriter Writer = new FileWriter(file);
        for(Element oneLink : links)
        {
            Writer.append(System.lineSeparator() + oneLink.attr("abs:href"));
            System.out.println("\n new URL : " + ourURL + " added! \n");
        }
        
        
        Writer.close();
        System.out.println("\n URL Parsed for other URLS !!\n");

    }


    public static int ReadRobots (String urlstring) throws IOException
    {
        URL url = new URL(urlstring);
        URLConnection urlCon = url.openConnection();
        InputStream Stream = urlCon.getInputStream();
        String currentFile = url.getFile();
        boolean flag = false;                   
        //if end of file is reached before we reach our filename in a disallow (true)
        //otherwise (false)
        char[] buff = new char [80];
        int i = 0;
        do{
            buff[i] = (char)Stream.read();
            if( i >=1 && buff[i] == 10 && buff[i-1] == 13)
            {
                i = 0;    //flush buffer
            }
            else if(buff[i] == -1) //end of file
            {
                flag = true;
                break;
            }
            else
            {
                i++;
            }
        }
        while(!buff.equals("User-agent: *") || !buff.equals("User-agent:*") || !flag); //group of all crawlers
        
        
       

        do{
            i = 0;
            do{
                buff[i] = (char)Stream.read();
                if(i == 0 && buff[i] == 'U')  //any line that doesnt begin with allow or disallow (specifying other user-agents)
                {
                    flag = true;
                    break;
                }
                if( i >=1 && buff[i] == 10 && buff[i-1] == 13)
                {
                    i = 0;    //flush buffer
                }
                else if(buff[i] == -1) //end of file
                {
                    flag = true;
                    break;
                }
                else
                {
                    i++;
                }
            }
            while(!buff.equals("Disallow: ") || !buff.equals("Disallow:") || !flag); //if read disallow
    
            buff[i] = (char)Stream.read();
            if( i >=1 && buff[i] == 10 && buff[i-1] == 13)
            {
                i = 0;    //flush buffer
            }
            else if(buff[i] == -1) //end of file
                {
                    flag = true;
                    break;
                }
            else
            {
                i++;
            }
            if(buff.equals(url.getFile()))
            {
                return 0;
            }
        }
        while(!buff.equals(url.getFile()) || !flag); //if read disallow:/filename
        return 1;

    }
}