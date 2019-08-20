package funix.prm.project.connector;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import funix.prm.project.R;
import funix.prm.project.model.Video;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import static funix.prm.project.util.Constants.API_KEY;

public class YoutubeConnector {

    private YouTube.Search.List mYouTubeList;

    public YoutubeConnector(Context context) {
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {

            @Override
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName(context.getString(R.string.app_name)).build();

        try {
            //TODO: Set info for youtube API
            mYouTubeList = youtube.search().list("id,snippet");
            mYouTubeList.setKey(API_KEY);
            mYouTubeList.setType("video");
            mYouTubeList.setFields("items(id,snippet(title,description,thumbnails))");

        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Video> search(String keyword) {
        //TODO: Get video depend on keyword
        mYouTubeList.setQ(keyword);
        mYouTubeList.setMaxResults((long)10);

        try {
            SearchListResponse response = mYouTubeList.execute();
            List<SearchResult> results = response.getItems();
            List<Video> items = new ArrayList<Video>();

            for (SearchResult result:results){
                Video item = new Video(result.getId().getVideoId(), result.getSnippet().getTitle(),
                        result.getSnippet().getDescription(),result.getSnippet().getThumbnails().getDefault().getUrl());
                items.add(item);
            }

            return items;

        } catch (IOException e) {
            Log.d("YoutubeConnector", "Could not search: " + e);
            return null; }
    }
}
