package net.navibot.plugins.reddit;

import net.navibot.plugins.reddit.data.Comment;
import net.navibot.sdk.utils.SimpleHttpUtils;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class SubReddit extends ArrayList<Comment> {


    public static SubReddit from(String sub, HttpClient httpClient) throws IOException, SubException {
        HttpGet request = new HttpGet("https://reddit.com/" + sub + "/new.json?sort=new");
        String result = SimpleHttpUtils.getContent(httpClient, request);

        JSONObject data = new JSONObject(result).optJSONObject("data");
        JSONArray posts = data.optJSONArray("children");

        if (posts == null || posts.isEmpty()) {
            throw new SubException( "There doesn't seem to be any posts on this subreddit!");
        }

        SubReddit reddit = new SubReddit();

        for (int p = 0; p < posts.length(); p++) {
            reddit.add(Comment.from(posts.getJSONObject(p).getJSONObject("data")));
        }

        return reddit;
    }
}
