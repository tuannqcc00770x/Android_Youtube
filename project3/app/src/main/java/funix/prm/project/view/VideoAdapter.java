package funix.prm.project.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import funix.prm.project.R;
import funix.prm.project.model.Video;

public class VideoAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Video> videoList;

    public VideoAdapter(Context context, int layout, List<Video> videoList) {
        this.context = context;
        this.layout = layout;
        this.videoList = videoList;
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    private class ViewHolder{
        ImageView thumbnail;
        TextView title, description;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.thumbnail = convertView.findViewById(R.id.thumbnail);
            holder.title = convertView.findViewById(R.id.title);
            holder.description = convertView.findViewById(R.id.description);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Video video = videoList.get(position);

        Picasso.with(context).load(video.getThumbnailURL()).into(holder.thumbnail);
        holder.title.setText(video.getTitle());
        holder.description.setText(video.getDescription());

        return convertView;
    }
}