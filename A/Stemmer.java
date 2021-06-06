package A;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Stemmer implements Serializable{
  private char[] b;
  private int i, i_end, j, k;
  private static final int INITIAL_SIZE = 40;

  /* unit of size whereby b is increased */
  public Stemmer() {
    b = new char[INITIAL_SIZE];
    i = 0;
    i_end = 0;
  }

  /**
   * Add a character to the word being stemmed. When you are finished adding characters, you can
   * call stem(void) to stem the word.
   */
  public void add(char ch) {
    if (i == b.length) {
      char[] new_b = new char[i + INITIAL_SIZE];
      for (int c = 0; c < i; c++) new_b[c] = b[c];
      b = new_b;
    }
    b[i++] = ch;
  }

  public void add(String w) {
    int wLen = w.length();
    if (i + wLen >= b.length) {
      char[] new_b = new char[i + wLen + INITIAL_SIZE];
      for (int c = 0; c < i; c++) new_b[c] = b[c];
      b = new_b;
    }
    for (int c = 0; c < wLen; c++) b[i++] = w.charAt(c);
  }

  /** return stemmed word */
  public String getStem() {
    return new String(b, 0, i_end).toLowerCase(Locale.ROOT);
  }

  /** Returns the length of the word resulting from the stemming process. */
  public int getResultLength() {
    return i_end;
  }

  /* isConsonant(i) is true <=> b[i] is a consonant. */

  private final boolean isConsonant(int i) {
    switch (b[i]) {
      case 'a':
      case 'e':
      case 'i':
      case 'o':
      case 'u':
        return false;
      case 'y':
        return (i == 0) ? true : !isConsonant(i - 1);
      default:
        return true;
    }
  }

  /*
   * measure() measures the number of consonant sequences between 0 and j. if c is
   * a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
   * presence, <c><v> gives 0 <c>vc<v> gives 1 <c>vcvc<v> gives 2 <c>vcvcvc<v>
   * gives 3 ....
   */

  private final int measure() {
    int n = 0;
    int i = 0;
    while (true) {
      if (i > j) return n;
      if (!isConsonant(i)) break;
      i++;
    }
    i++;
    while (true) {
      while (true) {
        if (i > j) return n;
        if (isConsonant(i)) break;
        i++;
      }
      i++;
      n++;
      while (true) {
        if (i > j) return n;
        if (!isConsonant(i)) break;
        i++;
      }
      i++;
    }
  }

  /* vowelinstem() is true <=> 0,...j contains a vowel */

  private final boolean vowelinstem() {
    int i;
    for (i = 0; i <= j; i++) if (!isConsonant(i)) return true;
    return false;
  }

  /* doubleConsonant(j) is true <=> j,(j-1) contain a double consonant. */

  private final boolean doubleConsonant(int j) {
    if (j < 1) return false;
    if (b[j] != b[j - 1]) return false;
    return isConsonant(j);
  }

  /*
   * cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant and
   * also if the second c is not w,x or y. this is used when trying to restore an
   * e at the end of a short word. e.g. cav(e), lov(e), hop(e), crim(e), but snow,
   * box, tray.
   */

  private final boolean cvc(int i) {
    if (i < 2 || !isConsonant(i) || isConsonant(i - 1) || !isConsonant(i - 2)) return false;
    {
      int ch = b[i];
      if (ch == 'w' || ch == 'x' || ch == 'y') return false;
    }
    return true;
  }

  private final boolean endsWith(String s) {
    int l = s.length();
    int o = k - l + 1;
    if (o < 0) return false;
    for (int i = 0; i < l; i++) if (b[o + i] != s.charAt(i)) return false;
    j = k - l;
    return true;
  }

  /*
   * replaceWith(s) sets (j+1),...k to the characters in the string s, readjusting
   * k.
   */

  private final void replaceWith(String s) {
    int l = s.length();
    int o = j + 1;
    for (int i = 0; i < l; i++) b[o + i] = s.charAt(i);
    k = j + l;
  }

  /* replaceIfMeasure(s) is used further down. */

  private final void replaceIfMeasure(String s) {
    if (measure() > 0) replaceWith(s);
  }

  /*
   * first() gets rid of plurals and -ed or -ing. e.g. caresses -> caress ponies
   * -> poni ties -> ti caress -> caress cats -> cat feed -> feed agreed -> agree
   * disabled -> disable matting -> mat mating -> mate meeting -> meet milling ->
   * mill messing -> mess meetings -> meet
   */

  private final void first() {
    // first-a
    if (b[k] == 's') {
      if (endsWith("sses")) k -= 2;
      else if (endsWith("ies")) {
        replaceWith("i");
      } else if (b[k - 1] != 's') k--;
    }

    // first-b
    if (endsWith("eed")) {
      if (measure() > 0) k--;
    } else if ((endsWith("ed") || endsWith("ing")) && vowelinstem()) {
      k = j;
      if (endsWith("at")) replaceWith("ate");
      else if (endsWith("bl")) replaceWith("ble");
      else if (endsWith("iz")) replaceWith("ize");
      else if (doubleConsonant(k)) {
        k--;
        {
          int ch = b[k];
          if (ch == 'l' || ch == 's' || ch == 'z') k++;
        }
      } else if (measure() == 1 && cvc(k)) replaceWith("e");
    }
    if (endsWith("y") && vowelinstem()) b[k] = 'i';
  }

  /*
   * second() maps double suffices to single ones. so -ization ( = -ize plus
   * -ation) maps to -ize etc. note that the string before the suffix must give
   * measure() > 0.
   */

  private final void second() {
    if (k == 0) return;

    if (endsWith("ational")) {
      replaceIfMeasure("ate");
      return;
    }
    if (endsWith("tional")) {
      replaceIfMeasure("tion");
      return;
    }
    if (endsWith("enci")) {
      replaceIfMeasure("ence");
      return;
    }
    if (endsWith("anci")) {
      replaceIfMeasure("ance");
      return;
    }
    if (endsWith("izer")) {
      replaceIfMeasure("ize");
      return;
    }
    if (endsWith("bli")) {
      replaceIfMeasure("ble");
      return;
    }
    if (endsWith("alli")) {
      replaceIfMeasure("al");
      return;
    }
    if (endsWith("entli")) {
      replaceIfMeasure("ent");
      return;
    }
    if (endsWith("eli")) {
      replaceIfMeasure("e");
      return;
    }
    if (endsWith("ousli")) {
      replaceIfMeasure("ous");
      return;
    }
    if (endsWith("ization")) {
      replaceIfMeasure("ize");
      return;
    }
    if (endsWith("ation")) {
      replaceIfMeasure("ate");
      return;
    }
    if (endsWith("ator")) {
      replaceIfMeasure("ate");
      return;
    }
    if (endsWith("alism")) {
      replaceIfMeasure("al");
      return;
    }
    if (endsWith("iveness")) {
      replaceIfMeasure("ive");
      return;
    }
    if (endsWith("fulness")) {
      replaceIfMeasure("ful");
      return;
    }
    if (endsWith("ousness")) {
      replaceIfMeasure("ous");
      return;
    }
    if (endsWith("aliti")) {
      replaceIfMeasure("al");
      return;
    }
    if (endsWith("iviti")) {
      replaceIfMeasure("ive");
      return;
    }
    if (endsWith("biliti")) {
      replaceIfMeasure("ble");
      return;
    }
    if (endsWith("logi")) {
      replaceIfMeasure("log");
      return;
    }
  }

  /* third() deals with -ic-, -full, -ness etc. similar strategy to second. */

  private final void third() {
    if (endsWith("icate")) {
      replaceIfMeasure("ic");
      return;
    }
    if (endsWith("ative")) {
      replaceIfMeasure("");
      return;
    }
    if (endsWith("alize")) {
      replaceIfMeasure("al");
      return;
    }
    if (endsWith("iciti")) {
      replaceIfMeasure("ic");
      return;
    }
    if (endsWith("ical")) {
      replaceIfMeasure("ic");
      return;
    }
    if (endsWith("ful")) {
      replaceIfMeasure("");
      return;
    }
    if (endsWith("ness")) {
      replaceIfMeasure("");
      return;
    }
    if (endsWith("er")) {
      replaceIfMeasure("");
      return;
    }
  }

  /* fourth() takes off -ant, -ence etc., in context <c>vcvc<v>. */

  private final void fourth() {
    if (k == 0) return;
    switch (b[k - 1]) {
      case 'a':
        if (endsWith("al")) break;
        return;
      case 'c':
        if (endsWith("ance")) break;
        if (endsWith("ence")) break;
        return;
      case 'e':
        if (endsWith("er")) break;
        return;
      case 'i':
        if (endsWith("ic")) break;
        return;
      case 'l':
        if (endsWith("able")) break;
        if (endsWith("ible")) break;
        return;
      case 'n':
        if (endsWith("ant")) break;
        if (endsWith("ement")) break;
        if (endsWith("ment")) break;
        if (endsWith("ent")) break;
        return;
      case 'o':
        if (endsWith("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
        if (endsWith("ou")) break;
        return;
      case 's':
        if (endsWith("ism")) break;
        return;
      case 't':
        if (endsWith("ate")) break;
        if (endsWith("iti")) break;
        return;
      case 'u':
        if (endsWith("ous")) break;
        return;
      case 'v':
        if (endsWith("ive")) break;
        return;
      case 'z':
        if (endsWith("ize")) break;
        return;
      default:
        return;
    }
    if (measure() > 1) k = j;
  }

  /* fifth() removes a final -e if measure() > 1. */

  private final void fifth() {
    j = k;
    if (b[k] == 'e') {
      int a = measure();
      if (a > 1 || a == 1 && !cvc(k - 1)) k--;
    }
    if (b[k] == 'l' && doubleConsonant(k) && measure() > 1) k--;
  }

  /** You can retrieve the result with getResultLength() or getStem(). */
  public void stem() {
    k = i - 1;
    if (k > 1) {
      first();
      second();
      third();
      fourth();
      fifth();
    }
    i_end = k + 1;
    i = 0;
  }

  public static String parser(String fileName) throws IOException {
    //TODO : change the directory of the folder to your directory3
    FileWriter myWriter = new FileWriter("D:\\java Folder\\DownPages\\"+fileName+".txt");
    StringBuilder sb = new StringBuilder();
    String line;
    //TODO : change the directory of the folder to your directory4
    FileReader reader = new FileReader( "D:\\java Folder\\DownPages\\" +fileName+ ".html");
    BufferedReader br = new BufferedReader(reader);
    while ((line = br.readLine()) != null) {
      sb.append(line);
    }

    Stemmer s = new Stemmer();

    // Parsing html code : remove tags and returns seperated words in string
    Document doc = Jsoup.parse(sb.toString());

    String title = doc.title();
    Elements headers = doc.select("h2,h3,h4,h5,h6");
    String headings = Jsoup.parse(headers.html()).text();

    doc.select("h1,h2,h3,h4,h5,h6").remove();
    String bodyParsed = doc.text();
    // remove Digits and Special Characters from bodyParsed string
    bodyParsed = bodyParsed.replaceAll("[\\d-+.^:,*!@#$%_·?©×\"'()<>{}؛÷،/:φ=]", "");
    //TODO : change the directory of the folder to your directory5
    List<String> stopwords = Files.readAllLines(Path.of(Paths.get("")
            .toAbsolutePath() + "\\" + "A/stopWords.txt"));
    myWriter.write(doc.title()+"\n");
    myWriter.write(bodyParsed.replaceAll("  "," ")+"\n");
    myWriter.write("*title\n");
    for (String word : title.split(" ")) {
      word = word.toLowerCase();
      if (!stopwords.contains(word) && word != "") {
        s.add(word);
        s.stem();
        myWriter.write(s.getStem() + "\n");
      }
    }

    myWriter.write("*headers\n");
    for (String word : headings.split(" ")) {
      word = word.toLowerCase();
      if (!stopwords.contains(word) && word != "") {
        s.add(word);
        s.stem();
        myWriter.write(s.getStem() + "\n");
      }
    }
    myWriter.write("*plainText\n");
    for (String word : bodyParsed.split(" ")) {
      word = word.toLowerCase();
      if (!stopwords.contains(word) && word != "") {
        s.add(word);
        s.stem();
        myWriter.write(s.getStem() + "\n");
      }
    }
    myWriter.close();
    return bodyParsed;
  }

  public static void main(String[] args) throws IOException {
    parser("0");
  }
   }
