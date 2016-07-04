package app.com.karas.android.popularmoviei.popularmovie;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent detailIntent = getActivity().getIntent();
        Bundle bundle = detailIntent.getExtras();
        TheMovieInfo info = bundle.getParcelable(TheMovieInfo.class.getName());
        TextView titleView = (TextView) rootView.findViewById(R.id.detail_movie_title);
        titleView.setText(info.original_title);
        TextView overviewView = (TextView) rootView.findViewById(R.id.detail_movie_overview);
        overviewView.setText(info.overview_text);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.detail_movie_release_date);
        releaseDateView.setText(info.release_date);
        TextView voteRateView = (TextView) rootView.findViewById(R.id.detail_movie_vote_rate);
        voteRateView.setText(Double.toString(info.vote_average));

        ImageView image = (ImageView) rootView.findViewById(R.id.detail_movie_poster);

        Picasso
                .with(getActivity())
                .load("http://image.tmdb.org/t/p/w780/" + info.poster_url)
                .into(image);

        return rootView;
    }
}
