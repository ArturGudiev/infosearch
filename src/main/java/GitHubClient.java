
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class GitHubClient {
    public static String apiURL = "https://api.github.com";




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


    private static List<String> getRepoNames(int count, int year) throws IOException, JSONException {
        Set<String> names = new HashSet<String>();
        Calendar cal = new GregorianCalendar(year, 1, 1);  // allocate with the specified date
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
                System.out.println(url);
                System.out.println(names.size() + " " + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.MONTH));
                if (names.size() >= count) {
                    return new ArrayList<String>(names);
                }
            }
            cal.add(Calendar.MONTH, 1);
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


    private static JSONArray getContributors(String filename) throws IOException, JSONException {
//        int count = 100;
//        List<String> repoNames = getRepoNames(count);
        List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        String s = FileUtils.readFileToString(new File(filename));
        JSONArray repoNames = new JSONArray(s);
        Set<String> users = new HashSet<String>();
        for (int i = 0; i < repoNames.length(); i++) {
            try {
                String repoName = (String) repoNames.get(i);
                List<Contributor> contributorsFromRepo = getContributorsFromRepo(repoName);
                List<String> collect = contributorsFromRepo.stream().map(el -> el.login).collect(Collectors.toList());//toArray(String[]::new);
                users.addAll(collect);
                String jsonString = "{ \"repository\": \"" + repoName + "\", \"contributors\" : " + contributorsFromRepo + "}";
                System.out.println(jsonString);
                jsonObjects.add(new JSONObject(jsonString));
            } catch (Exception e) {}
            System.out.println(i + " " + users.size()   );
        }
        return new JSONArray(jsonObjects);
    }

    private static void printReposToFile(int year) throws IOException, JSONException {
        List<String> repoNames = getRepoNames(2500, year);
        System.out.println(repoNames);
        System.out.println(repoNames.size());
        JSONArray arr = new JSONArray(repoNames);

        String json = new Gson().toJson(repoNames);
        PrintWriter writer = new PrintWriter("C:\\Programming\\Scala\\infosearch\\data\\repos"+year+".json", "UTF-8");
        writer.println(json);
        writer.close();
    }

    private static Set<String> jsonArrayToStringSet(JSONArray array) throws JSONException {
        Set<String> strings = new HashSet<String>();
        for (int i = 0; i < array.length(); i++) {
            strings.add((String) array.get(i));
        }
        return strings;
    }

    private static Set<String> getStringsFromFile(String filename) throws IOException, JSONException {
        String names = FileUtils.readFileToString(new File(filename));
        return jsonArrayToStringSet(new JSONArray(names));
    }

    public static void main(String[] args) throws JSONException, IOException {
//        System.out.println(arr);
//        printReposToFile(2010);
//        printReposToFile(2011);
//        printReposToFile(2012);
//        printReposToFile(2013);
//        printReposToFile(2014);
//        printReposToFile(2015);
//        printReposToFile(2016);
//        printReposToFile(2017);
//        printReposToFile(2018);
//        Set<String> set0 = getStringsFromFile("C:\\Programming\\Scala\\infosearch\\data\\repos2010.json");
//        Set<String> set1 = getStringsFromFile("C:\\Programming\\Scala\\infosearch\\data\\repos2011.json");
//        Set<String> set2 = getStringsFromFile("C:\\Programming\\Scala\\infosearch\\data\\repos2012.json");
//        Set<String> set3 = getStringsFromFile("C:\\Programming\\Scala\\infosearch\\data\\repos2013.json");
//        Set<String> set4 = getStringsFromFile("C:\\Programming\\Scala\\infosearch\\data\\repos2014.json");
//        Set<String> set5 = getStringsFromFile("C:\\Programming\\Scala\\infosearch\\data\\repos2015.json");
//        Set<String> set6 = getStringsFromFile("C:\\Programming\\Scala\\infosearch\\data\\repos2016.json");
//        Set<String> set7 = getStringsFromFile("C:\\Programming\\Scala\\infosearch\\data\\repos2017.json");
//        Set<String> set8 = getStringsFromFile("C:\\Programming\\Scala\\infosearch\\data\\repos2018.json");
//        set0.addAll(set1);
//        set0.addAll(set2);
//        set0.addAll(set3);
//        set0.addAll(set4);
//        set0.addAll(set5);
//        set0.addAll(set6);
//        set0.addAll(set7);
//        set0.addAll(set8);

//        String json = new Gson().toJson(set0);
//
//        FileUtils.write(new File("C:\\Programming\\Scala\\infosearch\\data\\1111.json"), json);
//        System.out.println(set0.size());
        JSONArray contributors = getContributors("C:\\Programming\\Scala\\infosearch\\data\\1111.json");
        FileUtils.write(new File("C:\\Programming\\Scala\\infosearch\\data\\contributors3.json"), contributors.toString());
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
