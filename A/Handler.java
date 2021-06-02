package A;

import javax.servlet.http.*;
import java.io.IOException;

public class Handler extends HttpServlet  {
    public void doGet(HttpServletRequest Req,HttpServletResponse Res) throws IOException {
        String word = Req.getParameter("search");
        Stemmer s = new Stemmer();
        s.add(word);
        s.stem();
        try {
           String Urls[] = Main.II.QueryUrl(s.getStem());
          String Titles[] = Main.II.QueryTitle(s.getStem());
            Res.setContentType("text/html");
            String page = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\"\n" +
                    "        integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">\n" +
                    "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">\n" +
                    "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\">\n" +
                    "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\">\n" +
                    "    <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\">\n" +
                    "    <link href=\"https://fonts.googleapis.com/css2?family=Ubuntu&display=swap\" rel=\"stylesheet\">\n" +
                    "    <link rel=\"stylesheet\" href=\"results.css\">\n" +
                    "    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\"\n" +
                    "        integrity=\"sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q\"\n" +
                    "        crossorigin=\"anonymous\"></script>\n" +
                    "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" integrity=\"sha38\"></script>\n" +
                    "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n" +
                    "    <script src=\"app.js\"></script>\n" +
                    "    <title>Results page of " +word+"</title>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <div class=\"container-fluid\">\n" +
                    "        <div class=\"row\">\n" +
                    "            <div class=\"col\">\n" +
                    "                <h1>Results Page</h1>\n" +
                    "                <form class=\"SearchBoxR\" action=\"/action_page\" method=\"GET\">\n" +
                    "                    <input class=\"SearchBarR\" type=\"text\" placeholder=\"\" name=\"search\">\n" +
                    "                    <button class=\"searchBtnR\" type=\"submit\"><i class=\"fa fa-search\"></i></button>\n" +
                    "                    </input>\n" +
                    "                </form>\n";
            if(Urls!=null) {
                for(int i=0;i<Urls.length;i++) {
                    page += "                <a target="+'"'+"_blank"+'"'+"href="+Urls[i]+">"+Titles[i]+"</a>\n" +
                            "                <p>In addition to organic search results, search engine results pages (SERPs) usually include paid\n" +
                            "                    search\n" +
                            "                    and\n" +
                            "                    pay-per-click (PPC) ads. Thanks to search engine ...</p>\n";
                }
            }
            else
            {
                page+="<h1> No Results Found</h1>";
            }
                   page+= "            </div>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";

            Res.getWriter().println(page);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
    }
}
