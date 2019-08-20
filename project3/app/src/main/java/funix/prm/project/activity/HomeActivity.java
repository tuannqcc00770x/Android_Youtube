package funix.prm.project.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import funix.prm.project.R;
import funix.prm.project.connector.YoutubeConnector;
import funix.prm.project.model.Video;
import funix.prm.project.sqlite.SQLiteHelper;
import funix.prm.project.util.Constants;
import funix.prm.project.view.VideoAdapter;

import static funix.prm.project.util.Constants.DB_NAME;
import static funix.prm.project.util.Constants.ID;
import static funix.prm.project.util.Constants.USER;
import static funix.prm.project.util.Constants.VIDEO_DESCRIPTION;
import static funix.prm.project.util.Constants.VIDEO_ID;
import static funix.prm.project.util.Constants.VIDEO_TITLE;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = HomeActivity.class.getSimpleName();
    private Handler mHandler;
    ListView lvVideo;
    VideoAdapter adapter;
    String user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Intent intent = getIntent();
        if (intent.getStringExtra(ID).equals("")){
            user = intent.getStringExtra(USER);
        } else {
            user = intent.getStringExtra(ID);
        }
        actionBar.setTitle("Hi " + intent.getStringExtra(USER) + ", Welcome to FUNIX!");
        mHandler = new Handler();
        //TODO Khởi tạo và cài đặt sự kiện cho các thành phần views
        loadVideos(Constants.KEY_WORD_DEFAULT);
    }

    private void loadVideos(final String keyword) {
        new Thread() {
            public void run() {
                YoutubeConnector connector = new YoutubeConnector(HomeActivity.this);
                final List<Video> ls = connector.search(keyword);
                Log.e("Success", ""+ls.size());
                lvVideo = (ListView)findViewById(R.id.lv_video);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //TODO: Xử lý hiển thi thông tin video trên màn hình MH-3
                        lvVideo.setOnItemClickListener(
                                new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(HomeActivity.this,VideoPlayerActivity.class);
                                        intent.putExtra(VIDEO_ID,ls.get(position).getId());
                                        intent.putExtra(VIDEO_TITLE,ls.get(position).getTitle());
                                        intent.putExtra(VIDEO_DESCRIPTION,ls.get(position).getDescription());
                                        SQLiteHelper.getInstance(getBaseContext()).addVideo(ls.get(position),user);
                                        Log.e("ADD"," SUSCCESS " +ls.get(position).getId());
                                        startActivity(intent);
                                    }
                                }
                        );
                        notifyChange(ls);
                    }
                });
            }
        }.start();

    }

    private void notifyChange(List<Video> ls) {
        //TODO: Cập nhật thông tin danh sách video mỗi khi có sự thay đổi
        adapter = new VideoAdapter(getApplicationContext(),R.layout.video_row,ls);
        lvVideo.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_history:
                //TODO: Xử lý logic khi chọn vào history
                notifyChange(SQLiteHelper.getInstance(getBaseContext()).getVideosByUser(user));
                break;
            case R.id.action_logout:
                //TODO: Xử lý logic khi chọn vào log out
                Intent intent = new Intent(HomeActivity.this,SignInActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
