package app.com.karas.android.popularmoviei.popularmovie;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImageListAdapter extends ArrayAdapter<TheMovieInfo> {
    private final String LOG_TAG = ImageListAdapter.class.getName();
    private Context context;
    private LayoutInflater inflater;
    private @LayoutRes int resource;
    private @IdRes int imageViewResourceId;
    private final Object mLock = new Object();

    public ImageListAdapter(Context context, @LayoutRes int resource, @IdRes int imageViewResourceId, @NonNull ArrayList<TheMovieInfo> imageUrls) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.imageViewResourceId = imageViewResourceId;
        addAll(imageUrls);

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(resource, parent, false);
        }
        ImageView image = (ImageView) convertView.findViewById(imageViewResourceId);

        Picasso
                .with(context)
                .load("http://image.tmdb.org/t/p/w185/" + getItem(position).poster_url)
                .into(image);

        return convertView;
    }
}
