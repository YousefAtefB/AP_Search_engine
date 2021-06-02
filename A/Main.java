package A;
public class Main {
    static String   arr[]= new String[]{"0", "1","2","3","4","5"};
    public  static InvertedIndexer II= new InvertedIndexer(arr);;

    static public void main(String args[]) throws Exception
    {
        String  arr[]= new String[]{"0", "1","2","3","4","5"};
        InvertedIndexer II = new InvertedIndexer(arr);
        String str[]=II.QueryUrl("cancel");
        for(int i=0;i<str.length;i++)
            System.out.println(str[i]);
    }
}
