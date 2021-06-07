package A;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CrawlerThreadRE implements Runnable{

    public int Rank;
    public int noThreads;

    public CrawlerThreadRE (int r, int n)
    {
        this.Rank = r;
        this.noThreads = n;
    }

    public void run()
    {
        try
        {
        String page = "A/URLs.txt";
        File urlFILE = new File(page);
        FileReader fr = new FileReader(urlFILE);
        BufferedReader br = new BufferedReader(fr);
        String line;

        /////////////////initial iteration to read all the seed//////////////////
        int i = 0;
        while(i < 5000)
                {
                    line = br.readLine();

//                    System.out.println(line.indexOf('h'));
//                    System.out.println(line.substring(0,line.indexOf('h')));

                    //if(i % noThreads == Rank && (line != null) && (line.charAt(0) == 'h') )  //case 1: line is a multiple of Rank and no prefix (tag)
                    if(i%noThreads==Rank && line!=null)
                    {
                        // then crawl it but not break from the initial while loop
                        int indx=line.indexOf('h');
                        if(indx==-1)continue;
                        line=line.substring(indx);
                        myCrawler.download(line, page, Rank);
                        if(myCrawler.Current_Downloaded_File_Index.get() >= 5000)
                            break;
                    }
//                    else if ((line != null) && (line.charAt(0) != 'h')&&( Integer.parseInt(line.substring(0,line.indexOf('h')))== Rank) ) //case 2 : line has the tag of the thread
//                    {
//                        // then crawl it and break from the initial while loop
//                        //but first, remove the tag from the line
//                        String urlLine = line.substring(1);
//                        myCrawler.download(urlLine, page, Rank);
//                        if(myCrawler.Current_Downloaded_File_Index.get() >= 5000)
//                            break;
//
//                        break;
//                    }
                    i++;
                }

//                while(myCrawler.Current_Downloaded_File_Index.get() < 5000)
//                {
//                    line = br.readLine();
//                    if((line != null) && (line.charAt(0) != 'h') && ( Integer.parseInt(line.substring(0,line.indexOf('h')))== Rank))             //if line has the tag
//                    {
//                        String urlLine = line.substring(1);
//                        myCrawler.download(urlLine, page, Rank); //crawl it
//                    }
//                }
//
//                fr.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
}
