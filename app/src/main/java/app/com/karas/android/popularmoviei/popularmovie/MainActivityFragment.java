package app.com.karas.android.popularmoviei.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivityFragment extends Fragment {
    ImageListAdapter mAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_test_http) {
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new FetchMovieInfoTask(getActivity(), mAdapter).execute();
            } else {
                Toast toast = Toast.makeText(getActivity(), "Can't access network.", Toast.LENGTH_SHORT);
                toast.show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView grid = (GridView) rootView.findViewById(R.id.rpt_movie_list);

        mAdapter = new ImageListAdapter(
                getActivity(),
                R.layout.grid_item_movie,
                R.id.grid_item_movie_poster_image,
                new ArrayList<TheMovieInfo>()
        );

        grid.setAdapter(mAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageListAdapter adapter = (ImageListAdapter)parent.getAdapter();
                TheMovieInfo movieInfo = adapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra(TheMovieInfo.class.getName(), movieInfo);
                startActivity(detailIntent);
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new FetchMovieInfoTask(getActivity(), mAdapter).execute();
        } else {
            Toast toast = Toast.makeText(getActivity(), "Can't access network.", Toast.LENGTH_SHORT);
            toast.show();
        }

        return rootView;
    }
}

class FetchMovieInfoTask extends AsyncTask<Void, Void, String> {
    Context mContext;
    public ImageListAdapter mAdapter = null;
    private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();
    FetchMovieInfoTask(Context context, ImageListAdapter adapter) {
        mContext = context;
        mAdapter = adapter;
    }
    @Override
    protected String doInBackground(Void... params) {
        String result = "";
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            String urlString = "http://api.themoviedb.org/3/movie/";
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            String sortType = prefs.getString(
                    mContext.getString(R.string.pref_sort_type_key),
                    mContext.getString(R.string.pref_sort_type_default_value)
            );

            URL url = new URL(urlString + sortType + "?api_key=" + BuildConfig.THE_MOVIE_DB_API_KEY);
            Log.i(LOG_TAG, url.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(LOG_TAG, "The response is: " + response);
            InputStream inputStream = conn.getInputStream();
            if (inputStream == null) {
                return null;
            }

            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            result = buffer.toString();
        } catch (IOException e) {
            return "Unable to retrieve service.";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject moiveJson = new JSONObject(result);
            JSONArray movieArray = moiveJson.getJSONArray("results");
            ArrayList<TheMovieInfo> movieStringArray = new ArrayList<TheMovieInfo>();
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieInstance = movieArray.getJSONObject(i);
                //movieStringArray.add(movieInstance.getString("original_title") + "\n" + movieInstance.getInt("id") + "\n" + movieInstance.getString("release_date"));
                TheMovieInfo movieInfo = new TheMovieInfo(
                        movieInstance.getLong("id"),
                        movieInstance.getString("original_title"),
                        movieInstance.getString("poster_path"),
                        movieInstance.getString("overview"),
                        movieInstance.getDouble("vote_average"),
                        movieInstance.getString("release_date")
                );
                movieStringArray.add(movieInfo);
            }
            mAdapter.clear();
            mAdapter.addAll(movieStringArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

class TheMovieInfo implements Parcelable {
    public Long id;
    public String original_title;
    public String poster_url;
    public String overview_text;
    public double vote_average;
    public String release_date;
    public TheMovieInfo(Long _id, String title, String poster, String overview, double vote_rate, String date) {
        id =_id;
        original_title = title;
        poster_url = poster;
        overview_text = overview;
        vote_average = vote_rate;
        release_date = date;
    }

    protected TheMovieInfo(Parcel in) {
        id = in.readLong();
        original_title = in.readString();
        poster_url = in.readString();
        overview_text = in.readString();
        vote_average = in.readDouble();
        release_date = in.readString();
    }

    public static final Creator<TheMovieInfo> CREATOR = new Creator<TheMovieInfo>() {
        @Override
        public TheMovieInfo createFromParcel(Parcel in) {
            return new TheMovieInfo(in);
        }

        @Override
        public TheMovieInfo[] newArray(int size) {
            return new TheMovieInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(original_title);
        dest.writeString(poster_url);
        dest.writeString(overview_text);
        dest.writeDouble(vote_average);
        dest.writeString(release_date);
    }
}