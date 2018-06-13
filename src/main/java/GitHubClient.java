
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class GitHubClient {
    public static String apiURL = "https://api.github.com";


    private static void printReposToFile() throws IOException, JSONException {
        List<String> repoNames = getRepoNames(1000);
        System.out.println(repoNames);
        System.out.println(repoNames.size());
        JSONArray arr = new JSONArray(repoNames);

        String json = new Gson().toJson(repoNames);
        PrintWriter writer = new PrintWriter("repos.txt", "UTF-8");
        writer.println(json);
        writer.close();
    }


    private static String getResponse(String s) throws IOException {
//        System.out.println(s);
        return getResponse(s, "application/json");
    }

    public static String getResponse(String request, String accept) throws IOException {
        URL url = new URL(request);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        conn.setRequestProperty("Accept", accept);
        conn.setRequestProperty("Accept", accept);
        String userCredentials = "ArturGudiev:1234567890github";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userCredentials.getBytes()));
        conn.setRequestProperty("Authorization", basicAuth);
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        String result = "";
        while ((output = br.readLine()) != null) {
            result += output;
        }
        conn.disconnect();
        return result;
    }

    private static List<Contributor> getContributorsFromRepo(String repoName) throws IOException, JSONException {
//        System.out.println("============repo======" + repoName);
        List<Contributor> contributors = new ArrayList<>();
        String response = getResponse("https://api.github.com/repos/" + repoName + "/stats/contributors", "application/vnd.github.hellcat-preview+json");
        JSONArray arr = new JSONArray(response);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject contributorObject = (JSONObject) arr.get(i);
            String login = (String) contributorObject.getJSONObject("author").get("login");
            JSONArray weeks = contributorObject.getJSONArray("weeks");

            int sum = 0;
            for (int j = 0; j < weeks.length(); j++) {
                Integer additions = (Integer) weeks.getJSONObject(j).get("a");
                sum += additions;
            }
            Contributor cont = new Contributor(login, sum);
            contributors.add(cont);
//            System.out.println(cont);
        }
        return contributors;
    }


    private static List<String> getRepoNames(int count) throws IOException, JSONException {
        Set<String> names = new HashSet<String>();
        Calendar cal = new GregorianCalendar(2015, 1, 1);  // allocate with the specified date
        SimpleDateFormat ft =
                new SimpleDateFormat("yyyy-MM-dd");
        while (true) {
            String url = "https://api.github.com/search/repositories?q=language:java" +
                    "+created:>=" + ft.format(cal.getTime()) + "T00:00:00Z" +
                    "+is:public" +
                    "&sort=created" +
                    "&order=asc" +
                    "&per_page=100";
            for (int i = 1; i <= 10; i++) {
                names.addAll(getRepoNames(url, i));
//                System.out.println(names.size() + " " + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.MONTH));
                if (names.size() >= count) {
                    return new ArrayList<String>(names);
                }
            }
            cal.add(Calendar.MONTH, 6);
        }
//        }
    }

    private static List<String> getRepoNames(String apiURL, int page) throws IOException, JSONException {
        List<String> names = new ArrayList<String>();
        String response1 = getResponse(apiURL + "&page=" + page);

        JSONObject object = new JSONObject(response1);
        JSONArray items = (JSONArray) object.get("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject object1 = (JSONObject) items.get(i);
            names.add((String) object1.get("full_name"));
        }
        return names;
    }


    public static void main(String[] args) throws JSONException, IOException {
        int count = 100;
        List<String> repoNames = getRepoNames(count);
        List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        for (int i = 0; i < count; i++) {
            try {
                String repoName = repoNames.get(i);
                List<Contributor> contributorsFromRepo = getContributorsFromRepo(repoName);
                String jsonString = "{ \"repository\": \"" + repoName + "\", \"contributors\" : " + contributorsFromRepo + "}";
                System.out.println(jsonString);
                jsonObjects.add(new JSONObject(jsonString));
            } catch (Exception e) {

            }

        }
        JSONArray arr = new JSONArray(jsonObjects);
        System.out.println(arr);
    }


}

class GitHubAccount {
    int id;
    String login;

    @Override
    public String toString() {
        return "GitHubAccount{" +
                "id='" + id + '\'' +
                ", login='" + login + '\'' +
                '}';
    }

    public GitHubAccount() {
    }

    public GitHubAccount(int id, String login) {
        this.id = id;
        this.login = login;
    }

}

class Contributor {
    public Contributor() {
    }

    public Contributor(String login, int additions) {
        this.login = login;
        this.additions = additions;
    }

    String login;
    int additions;

    @Override
    public String toString() {
        return "{" +
                "\"login\":\"" + login + '\"' +
                ", \"additions\":" + additions +
                '}';
    }
}
