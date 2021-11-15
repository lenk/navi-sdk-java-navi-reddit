import net.navibot.plugins.reddit.SubException;
import net.navibot.plugins.reddit.SubReddit;
import net.navibot.plugins.reddit.data.Comment;
import net.navibot.sdk.NaviPlugin;
import net.navibot.sdk.Trigger;
import net.navibot.sdk.data.ImageCard;
import net.navibot.sdk.data.Message;
import net.navibot.sdk.data.Response;
import net.navibot.sdk.data.UrlCard;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Main extends Plugin {

    public Main(PluginWrapper wrapper) {
        super(wrapper);
    }

    /**
     * root class that'll be processed for custom triggers,
     * IT'S NOT RECOMMENDED YOU MOVE THIS CLASS, build around and in it
     * but for best functionality - leave it in place!
     */

    @Trigger(keyword = "!reddit", description = "A Navi Plugin to search for the latest subreddit posts i.e !reddit r/dankmemes")
    @Extension
    public static class MyPlugin implements NaviPlugin {
        private final Pattern subRegex = Pattern.compile("^r/[a-zA-Z0-9_]{3,21}$", Pattern.CASE_INSENSITIVE);

        /**
         * insert all of your code here for the incoming message invoking your trigger
         * you can create http requests subject to review and many other fun stuff to respond with
         * responses can be made up of both a card and a text if provided or one or the other
         *
         * @param message incoming message
         * @return your response
         */
        public Response onMessage(Message message, HttpClient client, HashMap<String, String> storage) {
            String subReddit = message.body().substring(7).trim();

            if (!subRegex.matcher(subReddit).find()) {
                return new Response("Whoops! Invalid subreddit provided, make sure it's formatted -> r/YourSubReddit");
            }

            try {
                SubReddit reddit = SubReddit.from(subReddit, client);
                Comment first = reddit.stream().filter( c -> !"hosted:video".equals(c.getType())).findFirst().orElse(null);

                if (first != null) {
                    if (first.getThumbnail() != null && !first.getThumbnail().isEmpty()) {
                        return new Response(new ImageCard(first.getEncodedThumbnail(client), first.getThumbnail(), "Reddit"), first.getText());
                    }

                    return new Response(new UrlCard(first.getTitle(), "Click to view this reddit!", first.getUrl(), "Reddit"));
                }

                return new Response("No supported content were found to send!");

            } catch (SubException e) {
                return new Response(e.getMessage());
            } catch (IOException e) {
                return new Response("There was an issue fetching the subreddit, please try again later!");
            }
        }
    }
}