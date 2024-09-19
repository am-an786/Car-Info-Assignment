import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CricketDataApi {

    public static String getCricketData() throws Exception {
        String apiUrl = "https://api.cuvora.com/car/partner/cricket-data";
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("apiKey", "test-creds@2320");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public static void processCricketData(String jsonData) {
        
        JsonElement jsonElement = JsonParser.parseString(jsonData);

        if (!jsonElement.isJsonObject()) {
            System.out.println("Invalid data format. Expected a JSON object.");
            return;
        }

        JsonObject rootObject = jsonElement.getAsJsonObject();

       
        if (!rootObject.has("data") || !rootObject.get("data").isJsonArray()) {
            System.out.println("Invalid data format. Expected a 'data' array.");
            return;
        }

        JsonArray matches = rootObject.getAsJsonArray("data");

        int highestScore = 0;
        String highestScoringTeam = "";
        int matchesWith300Plus = 0;

        for (JsonElement matchElement : matches) {
            JsonObject match = matchElement.getAsJsonObject();

            String t1 = match.get("t1").getAsString();
            String t2 = match.get("t2").getAsString();
            String t1s = match.has("t1s") ? match.get("t1s").getAsString() : "0/0";
            String t2s = match.has("t2s") ? match.get("t2s").getAsString() : "0/0";

            int team1Score = parseScore(t1s);
            int team2Score = parseScore(t2s);

            
            if (team1Score > highestScore) {
                highestScore = team1Score;
                highestScoringTeam = t1;
            }
            if (team2Score > highestScore) {
                highestScore = team2Score;
                highestScoringTeam = t2;
            }

            
            if ((team1Score + team2Score) > 300) {
                matchesWith300Plus++;
            }
        }

        
        System.out.println("Highest Score: " + highestScore + " and Team Name is:" + highestScoringTeam);
        System.out.println("Number Of Matches with total 300 Plus Score: " + matchesWith300Plus);
    }

    private static int parseScore(String score) {
        
        try {
            return Integer.parseInt(score.split("/")[0]);
        } catch (Exception e) {
            return 0; 
        }
    }

    public static void main(String[] args) {
        try {
            
            String jsonData = getCricketData();
            
            processCricketData(jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
