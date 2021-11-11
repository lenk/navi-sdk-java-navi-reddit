package net.navibot.plugins.reddit;

import net.navibot.plugins.reddit.data.Comment;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;

public class SubReddit extends ArrayList<Comment> {

    public static SubReddit from(String sub) throws IOException, SubException {
        Connection.Response connection = Jsoup.connect("https://www.reddit.com/" + sub + "/new.json?sort=new")
                .ignoreContentType(true).ignoreHttpErrors(true).method(Connection.Method.GET).execute();

        if (connection.statusCode() != 200) {
            throw new SubException("There was an issue fetching the subreddit, it either doesn't exist or there was an error!");
        }

        JSONObject data = new JSONObject(connection.body()).optJSONObject("data");
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
