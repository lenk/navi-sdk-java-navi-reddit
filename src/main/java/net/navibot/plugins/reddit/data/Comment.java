package net.navibot.plugins.reddit.data;

import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

public class Comment {
    private String thumbnail;
    private String type;
    private String text;
    private String title;
    private String sub;
    private String id;

    public static Comment from(JSONObject latestData) {
        Comment comment = new Comment();

        comment.type = latestData.optString("type");
        comment.text = latestData.optString("selftext");
        comment.title = latestData.optString("title");
        comment.sub = latestData.optString("subreddit_name_prefixed");
        comment.id = latestData.optString("id");
        comment.thumbnail = latestData.optString("url_overridden_by_dest");

        return comment;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public String getSub() {
        return sub;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return "https://www.reddit.com/" + sub + "/comments/" + id;
    }

    public String getEncodedThumbnail() throws IOException {

        if (thumbnail != null && !thumbnail.isEmpty()) {
            ImageIO.setUseCache(false);

            BufferedImage thumbnail = ImageIO.read(new URL(this.thumbnail));

            if (thumbnail != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(thumbnail, "JPG", out);

                return Base64.getEncoder().encodeToString(out.toByteArray());
            }
        }

        return null;
    }
}
