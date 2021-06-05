package A;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.net.URL;
import java.net.URLConnection;


import java.util.HashSet;
import java.util.Scanner;
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
        File PrevURLs = new File("PrevURLs.txt");
        if(Current_URL_Index==0)
            PrevURLs.createNewFile();
        BufferedReader bf = new BufferedReader(new FileReader(PrevURLs));
        FileWriter fw = new FileWriter("PrevURLs.txt");
        String testURL;
        while((testURL = bf.readLine()) != null)
        {
            if(stringurl.equals(testURL))
            {
                System.out.println("URL already crawled !");
                fw.close();
                bf.close();
                return;
            }
        }
        bf.close();
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
            File myFile = new File("D:" + File.separator + "project_garbage"+ File.separator + NameOfFile); //hard coded to put in folder "DownPages"
            //TODO : change the directory of the folder to your directory
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
        FileWriter Writer = new FileWriter("D:" + File.separator + "project_garbage"+ File.separator + NameOfFile);
        //TODO : change the directory of the folder to your directory
        try
        {
            if(ReadRobots(stringurl) == 1)
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
                fw.append(stringurl + System.lineSeparator());
            }
            
        }
        catch(IOException e)
        {
            System.out.println("error occured");
            e.printStackTrace();
        }

        fw.close();
        Stream.close();
        Writer.close();
        

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
        
        String currentFile = url.getFile();
        String hostname = url.getHost();
        String HostURL =  url.getProtocol().concat("://").concat(hostname).concat("/robots.txt"); //root directory then robots.txt
        Document doc = Jsoup.connect(HostURL).get();
        String pgtext = doc.body().text();
        Scanner scanner = new Scanner(pgtext);                 
        //if end of file is reached before we reach our filename in a disallow (true)
        //otherwise (false)
        String buff = "lol";
        int i = 0;
        while(scanner.hasNextLine())
        {
            buff = scanner.nextLine();
        }
        String test1 = "Disallow: ".concat(currentFile);
        String test2 = "Disallow:".concat(currentFile);
        if(buff.contains(test1) || buff.contains(test2))
        {
            //System.out.println("Prohibited to crawl !");
            scanner.close();
            return 0;
        }
        //System.out.println("good to crawl");
        scanner.close();
        return 1;
        // do{
        //     //buff = Stream.readLine();
            
        //     buff = scanner.nextLine();
        //     // if (buff == null)
        //     // {
        //     //     flag = true;
        //     // }
        //     System.out.println(buff);
        // }
        // while(scanner.hasNextLine() && !(buff.contains("User-agent: *")) && !(buff.contains("User-agent:*")) ); //group of all crawlers
        
        
       

        // do{
        //     do{

        //         buff = scanner.nextLine();
        //         if ( buff == null)
        //         {
        //             flag = true;
        //         }
        //         System.out.println(buff);
        //     }
        //     while( !flag && scanner.hasNextLine() && !(buff.contains("Disallow: ")) && !(buff.contains("Disallow:"))); //if read disallow
    
        //     if( !flag && buff.contains(currentFile))
        //     {
        //          ///////////////////////// just for debugging//////////////////////
        //          System.out.println("Prohibited to crawl !");
                
        //          return 0;
        //     } 
       
        // }
        // while(!flag && scanner.hasNextLine() && !(buff.contains("User-Agent"))); //if not read disallow:/filename
        // Stream.close();
        
        // System.out.println("good to crawl");
        // return 1;
    
    }

    public static int ContinueCrawler ()
    {
        int i = 0;
        File testfile;
        do
        {
            String filename = Integer.toString(i).concat(".html");
            testfile = new File("D:" + File.separator + "project_garbage"+ File.separator  + filename);
            //TODO : change the directory of the folder to your directory
            i++;
        }
        while(testfile.exists());
        return i;
    }
}