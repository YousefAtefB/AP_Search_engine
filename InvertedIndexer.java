import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

class InvertedIndexer
{
    class traits
    {
        int Position,Importance;
        public traits(int pos,int imp)
        {
            Position=pos;
            Importance=imp;
        }
    }

    class Posting
    {
        int FileId;
        ArrayList<traits> Ocurrences;

        public Posting(int num)
        {
            FileId=num;
            Ocurrences=new ArrayList<traits>();
        }

        public void add(int pos,int imp)
        {
            Ocurrences.add(new traits(pos,imp));
        }

    }

    int NumOfCurFile;
    String URLS[];
    ArrayList<HashMap<String,Posting>>PartialIndexer;   
    HashMap<String,ArrayList<Posting>> MainIndexer;
    Stemmer Stem;

    public InvertedIndexer(String NamesOfFiles[]) throws Exception
    {
        NumOfCurFile=0;
        URLS=new String[5001];
        Stem=new Stemmer();
        MainIndexer=new HashMap<String,ArrayList<Posting>>();
        PartialIndexer= new ArrayList<HashMap<String,Posting>>();
        for(int i=0;i<NamesOfFiles.length;i++)
            IndexFile(NamesOfFiles[i]);
    }

    public void IndexFile(String file) throws Exception
    {
        HashMap<String,Posting> CurPart=new HashMap<String,Posting>();

        String path= Paths.get("").toAbsolutePath().toString();

        BufferedReader Input=new BufferedReader(new FileReader(path+"\\"+file+".html"));
        URLS[NumOfCurFile]=Input.readLine();

        Stem.parser(file);

        Input=new BufferedReader(new FileReader(path+"\\"+file+".txt"));

        int WordPos=0, WordImp=0;
        for(String str=Input.readLine();str!=null;str=Input.readLine())
        {
            for(String word : str.split(" "))
            {
                WordPos++;
                Posting Value=CurPart.get(word);
                
                if(Value==null)
                {   
                    Value=new Posting(NumOfCurFile);
                    CurPart.put(word, Value);
                }

                Value.add(WordPos,WordImp);                
                
            }
        }

        AddToMainIndexer(CurPart);        
        PartialIndexer.add(CurPart);

        NumOfCurFile++;
        Input.close();
    }


    public void AddToMainIndexer (HashMap<String,Posting> CurPart)
    {
        for(Map.Entry<String,Posting> ver : CurPart.entrySet())
        {
            String Word=ver.getKey();
            Posting pos=ver.getValue(); 

            ArrayList<Posting> Value=MainIndexer.get(Word);
            if(Value==null)
            {
                Value=new ArrayList<Posting>();
                MainIndexer.put(Word, Value);
            }

            Value.add(pos);
        }
    }

    public String[] Query(String word)
    {
        String ret[];
        ArrayList<Posting> Value=MainIndexer.get(word);
        int num=Value.size();
        ret=new String[num];
        for(int i=0;i<num;i++)
            ret[i]=URLS[Value.get(i).FileId];
        return ret;    
    }

}

