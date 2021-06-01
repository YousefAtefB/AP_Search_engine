
public class Main {
    static public void main(String args[]) throws Exception
    {
        String arr[]=new String[1];
        arr[0]="0";
        InvertedIndexer II = new InvertedIndexer(arr);
        String str[]=II.Query("cancel");
        for(int i=0;i<str.length;i++)
            System.out.println(str[i]);
    }
}
