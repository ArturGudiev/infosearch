
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GitHubClient {

    public static List<GitHubAccount> getUsersFromGitHub(int numberOfUsers) throws JSONException {
        List<GitHubAccount> users = new ArrayList<GitHubAccount>();
        int since = 0;
        while (true) {
            try {

                URL url = new URL("https://api.github.com/users?since=" + since);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

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

                JSONArray jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String login = jsonobject.getString("login");
                    int id = Integer.parseInt(jsonobject.getString("id"));
                    users.add(new GitHubAccount(id, login));
                    if (users.size() >= numberOfUsers) {
                        return users;
                    }
                }
                since = users.get(users.size() - 1).id;
                conn.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws JSONException {
        List<GitHubAccount> accountsFromGitHub = getUsersFromGitHub(100);
        for (GitHubAccount account : accountsFromGitHub) {
            System.out.println(account);
        }
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
