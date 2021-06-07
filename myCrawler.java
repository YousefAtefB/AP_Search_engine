package A;

import java.io.*;

import java.io.FileWriter;

import java.net.URL;
import java.net.URLConnection;


import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static AtomicInteger Current_Downloaded_File_Index = new AtomicInteger();   //we download files with their index
    //example: 1.txt then 2.txt then 3.txt and so one,
    //and in the first line of each file, we put the url of this page so that
    //the indexer can link its conents to the url.


    public static AtomicInteger Current_URL_Index = new AtomicInteger(); //we crawl URLS with their index

    public static AtomicInteger Total_Num_Of_URLS = new AtomicInteger(); //we crawl URLS with their index


    public static void download(String stringurl, String Seed, int Tag)throws IOException
    //NameOfFile is the name of the destination file which will hold the contents of our downloaded webpage
    {
        File PrevURLs = new File("PrevURLs.txt");
        PrevURLs.createNewFile();
        BufferedReader bf = new BufferedReader(new FileReader(PrevURLs));
        FileWriter fw = new FileWriter("PrevURLs.txt", true);
        String testURL;

        while((testURL = bf.readLine()) != null)
        {
            if(stringurl.equals(testURL))
            {
                System.out.println("URL already crawled !");
                System.out.println(testURL);
                bf.close();
                fw.close();
                return;
            }
        }

        fw.append(stringurl + System.lineSeparator());
        fw.close();
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

        File myFile=null;
        synchronized (myCrawler.class) {
            String NameOfFile = Integer.toString(Current_Downloaded_File_Index.get()).concat(".html");

            Current_Downloaded_File_Index.incrementAndGet();

            myFile = new File("D:" + File.separator + "java Folder" + File.separator + "DownPages" + File.separator + NameOfFile); //hard coded to put in folder "DownPages"
            //TODO : change the directory of the folder to your directory

            try {
                if (myFile.createNewFile()) {
                    System.out.println("File " + NameOfFile + " Created");
                } else {
                    IOException e = new IOException("File already exists");
                    throw (e);
                }
            } catch (IOException e) {
                System.out.println("error occured");
                e.printStackTrace();
            }
        }
        FileWriter Writer = new FileWriter(myFile, true);
        try
        {
            if(ReadRobots(stringurl) == 1)
            {
                AddUrl(Seed, stringurl, Tag);        //crawl the url for other urls and add them to urls.txt
                Current_URL_Index.incrementAndGet();
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


    public static void AddUrl (String urlfile, String ourURL, int tag) throws IOException
    {
        //Document doc = Jsoup.connect(ourURL).userAgent("Mozilla").get();
        Document doc = Jsoup.connect(ourURL).get();
        Elements links = doc.select("a[href]");    //or "a"
        if(links.isEmpty() || Total_Num_Of_URLS.get()>=5000)
        {
            return;
        }
        //string OutPut_Link = links.attr("href");
        File file = new File(urlfile);
        FileWriter Writer = new FileWriter(file, true);
        for(Element oneLink : links)
        {
            Writer.append(System.lineSeparator()  +tag+oneLink.attr("abs:href"));
            Total_Num_Of_URLS.incrementAndGet();
            //System.out.println("\n new URL : " + ourURL + " added! \n");
        }


        Writer.close();
        System.out.println("\n URL Parsed for other URLS !!\n");

    }


    public static int ReadRobots (String urlstring) throws IOException
    //TODO : debug this function for special web pages like google , it doesn't allow google.com to be crawled !!
    //because we are checking for https://google.com/ , the filename is "/" which is in every line in the robots.txt :(
    //we should just check if there is a (whole line) like Disallow: / (but i couldn't scan the end of line char).
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


        int cutIndex = buff.indexOf("User-agent: *");
        buff = buff.substring(cutIndex + 1);  //now the text begins with "ser-agents: *"
        int cutSuffix = buff.indexOf("User-agent:");
        buff = buff.substring(0, cutSuffix); //now the test is only the user agents * block
        String test1 = "Disallow: ".concat(currentFile);
        String test2 = "Disallow:".concat(currentFile);


        if(currentFile.equals("/"))     // root directory of the page
        {
            String testDisallowAll1 = "Disallow: / Disallow";
            String testDisallowAll2 = "Disallow: / Allow";
            String testDisallowAll3 = "Disallow: / U";
            String testDisallowAll4 = "Disallow: / #";
            if(buff.contains(testDisallowAll1) || buff.contains(testDisallowAll2) || buff.contains(testDisallowAll3) || buff.contains(testDisallowAll4))
            {
                System.out.println("Prohibited to crawl !");
                scanner.close();
                return 0;
            }
            System.out.println("good to crawl !");
            scanner.close();
            return 1;
        }


        else
        {
            if(buff.contains(test1) || buff.contains(test2))
            {
                System.out.println("Prohibited to crawl !");
                scanner.close();
                return 0;
            }
            //System.out.println("good to crawl");
            scanner.close();
            return 1;
        }
    }

    public static int ContinueCrawler ()
    {
        int i = 0;
        File testfile;
        do
        {
            String filename = Integer.toString(i).concat(".html");
            testfile = new File("D:" + File.separator + "java Folder" + File.separator + "DownPages" + File.separator + filename);
            i++;
        }
        while(testfile.exists());
        return i - 1;
    }

    public static void FullContinueCrawler() throws IOException {
        int i=0;
        while(true)
        {
            String dir="D:" + File.separator + "java Folder" + File.separator + "DownPages" + File.separator;
            File testfile= new File(dir+Integer.toString(i)+".html");
            if(!testfile.exists())
            {
                Current_Downloaded_File_Index.set(i);
                break;
            }
            i++;
        }

        String page = "A/URLs.txt";
        File urlFILE = new File(page);
        FileReader fr = new FileReader(urlFILE);
        BufferedReader br = new BufferedReader(fr);

        Total_Num_Of_URLS.set(0);
        for(String line=br.readLine();line!=null;line=br.readLine())
            Total_Num_Of_URLS.incrementAndGet();

    }

}