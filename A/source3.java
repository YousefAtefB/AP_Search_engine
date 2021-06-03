package A;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class source3 {

    public static void main(String [] args)
    {
        try
        {
            String page = "A/URLs.txt";        //contains all our URLs till now
            File urlFILE = new File(page);
            FileReader fr = new FileReader(urlFILE);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String ourPage = "result.txt"; // holds the content of the downloaded pages
            while((line = br.readLine()) != null)
            {
                myCrawler.download(line, page, ourPage);
                if(myCrawler.Current_Downloaded_File_Index >= 5000)
                    break;
            }
            fr.close();
        }
        
        catch(IOException e)
        {
            System.out.println("error somewhere I don't know");
            e.printStackTrace();
        }
    }
    
}
