
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class source3 {

    public static void main(String [] args)
    {
        //try
        //{
            CrawlerThreadRE arrThreads1 = new CrawlerThreadRE(0, 1);
            // CrawlerThreadRE arrThreads2 = new CrawlerThreadRE(1, 4);
            // CrawlerThreadRE arrThreads3 = new CrawlerThreadRE(2, 4);
            // CrawlerThreadRE arrThreads4 = new CrawlerThreadRE(3, 4);
            myCrawler.Current_Downloaded_File_Index = myCrawler.ContinueCrawler(); //get last url


            // String page = "URLs.txt";        //contains all our URLs till now
            // File urlFILE = new File(page);
            // FileReader fr = new FileReader(urlFILE);
            // BufferedReader br = new BufferedReader(fr);
            // String line;

            Thread t1 = new Thread(arrThreads1);
            // Thread t2 = new Thread(arrThreads2);
            // Thread t3 = new Thread(arrThreads3);
            // Thread t4 = new Thread(arrThreads4);
             t1.start();
            // t2.start();
            // t3.start();
            // t4.start();
        //}
            // for(int i = 0 ; i <= myCrawler.Current_URL_Index ; i++)
            // {
            //     br.readLine();           //skip lines to the last line read
            // }
            // while((line = br.readLine()) != null)
            // {
            //     myCrawler.download(line, page);
            //     if(myCrawler.Current_Downloaded_File_Index >= 5000)
            //         break;
            // }
            // fr.close();
        
        // catch(IOException e)
        // {
        //     System.out.println("error somewhere I don't know");
        //     e.printStackTrace();
        // }
    }
    
}
