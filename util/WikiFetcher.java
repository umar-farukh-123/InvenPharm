package util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WikiFetcher 
{

    public static String fetchSummary(String query) 
    {
        try 
        {
            String encoded=URLEncoder.encode(query, "UTF-8");
            String apiURL="https://en.wikipedia.org/api/rest_v1/page/summary/" + encoded;

            
            HttpURLConnection conn=(HttpURLConnection) new URL(apiURL).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept","application/json");

            
            if(conn.getResponseCode()!=200) 
            {
                return "No information found for " + query;
            }

            BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder json=new StringBuilder();
            String line;

            while ((line=reader.readLine())!=null) 
            {
                json.append(line);
            }

            reader.close();
            conn.disconnect();

            // Extract only the summary (extract field)
            String content=json.toString();
            int start=content.indexOf("\"extract\":\"");
            if (start!=-1) 
            {
                start+=10;
                int end=content.indexOf("\",\"", start);
                if(end!=-1) 
                {
                    String summary=content.substring(start, end);
                    return summary.replace("\\n","\n").replace("\\\"", "\"");
                }
            }

            return "No summary available.";
        } 
        catch (Exception e) 
        {
            return "Error fetching summary: "+e.getMessage();
        }
    }
}
