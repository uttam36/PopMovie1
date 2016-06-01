package com.example.uttam.popmovie1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

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

/**
 * Created by uttam on 8/5/16.
 */
public class ImageAdapter extends BaseAdapter {
    public Context mContext;
    static String[] image_url = new String[20];
    static String[][] Data = new String[image_url.length][4];

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return image_url.length;
    }

    public Object getItem(int position) {

        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 380));
        //    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }
        FetchData task = new FetchData(imageView);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        String sort_type = sharedPref.getString("Sort By","popular");

        task.execute(Integer.toString(position),sort_type);
        Log.v("Position ",Integer.toString(position));


        return imageView;

    }

    public class FetchData extends AsyncTask<String,Void,String>
    {
        private ImageView imageView;
        public FetchData(ImageView imageView)
        {
            this.imageView = imageView;
        }

        @Override
        protected void onPostExecute(String string)
        {
            if(string!=null)
            {
                String base = "http://image.tmdb.org/t/p/w185/";
                base+=string;
                Picasso.with(mContext)
                        .load(base)
                        .into(imageView);

            }
            super.onPostExecute(string);
        }
        @Override
        protected String doInBackground(String... params)
        {
            // params[0] is Integer denoting position of view and params[1] is either "popular" or "top_rated"

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            int position = Integer.parseInt(params[0]);
            int page_No = position/20 +1;

            String base_url = "https://api.themoviedb.org/3/movie/";
            base_url+=params[1]+"?page="+Integer.toString(page_No)+"&";
            
            String API_key = "api_key="+"PUT YOUR API KEY HERE";                /* PUT API KEY HERE */

            final String build = base_url+API_key;

            try
            {
                URL url = new URL(build.toString());
                Log.v("Build URL:---->",build.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                String movies_data = buffer.toString();
                Log.v("Json Data---->",movies_data);

                try {
                    return getPosterUrl(movies_data,position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ImageAdapter", "Error closing stream", e);
                    }
                }

            }
            return null;
        }



        public String getPosterUrl(String movies_data,int pos) throws JSONException
        {
            final String TMD_RESULTS = "results";
            final String TMD_POSTER_PATH = "poster_path";
            final String TMD_TITLE = "original_title";
            final String TMD_RATINGS = "vote_average";
            final String TMD_RELEASE_DATE = "release_date";
            final String TMD_DESCRIPTION = "overview";
            JSONObject movieJson = new JSONObject(movies_data);
            JSONArray movieArray = movieJson.getJSONArray(TMD_RESULTS);

            JSONObject result =  movieArray.getJSONObject(pos);
            Data[pos][0] = result.getString(TMD_TITLE);
            Data[pos][1] = result.getString(TMD_RATINGS);
            Data[pos][2] = result.getString(TMD_RELEASE_DATE);
            Data[pos][3] = result.getString(TMD_DESCRIPTION);
            Log.v("Poster Path:--->",result.getString(TMD_POSTER_PATH));
            image_url[pos] = "http://image.tmdb.org/t/p/w185/"+result.getString(TMD_POSTER_PATH);
            return result.getString(TMD_POSTER_PATH);
        }
    }
}
