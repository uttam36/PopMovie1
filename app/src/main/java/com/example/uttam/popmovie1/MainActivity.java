package com.example.uttam.popmovie1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    static String[] image_url = new String[20];
    static String[][] Data = new String[image_url.length][4];
    static ImageView[] imageViews = new ImageView[20];
    public Context mContext=this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        for(int position=0;position<20;position++) {
            imageViews[position] = new ImageView(mContext);
            imageViews[position].setLayoutParams(new GridView.LayoutParams(300, 380));
            imageViews[position].setPadding(4, 4, 4, 4);
        }
                FetchData task = new FetchData();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
                String sort_type = sharedPref.getString("Sort By", "popular");

                task.execute(sort_type);

            Log.v("Position ",Integer.toString(0));




        GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(new ImageAdapter(this));


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id)
            {
                Intent intent = new Intent(getApplicationContext(),MovieDetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT,Integer.toString(position));
                startActivity(intent);
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }


    @Override
    public void onResume()
    {

            FetchData task = new FetchData();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            String sort_type = sharedPref.getString("Sort By", "popular");

            task.execute(sort_type);


        super.onResume();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.settings)
        {
            startActivity(new Intent(this,SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchData extends AsyncTask<String,Void,Void>
    {
        private ImageView imageView;


        @Override
        protected Void doInBackground(String... params)
        {
            // params[0] is either "popular" or "top_rated"

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String base_url = "https://api.themoviedb.org/3/movie/";
            base_url+=params[0]+"?";

            String API_key = "api_key="+"PUT API KEY HERE";                /* PUT API KEY HERE */

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
                    getPosterUrl(movies_data);
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



        public void getPosterUrl(String movies_data) throws JSONException
        {
            final String TMD_RESULTS = "results";
            final String TMD_POSTER_PATH = "poster_path";
            final String TMD_TITLE = "original_title";
            final String TMD_RATINGS = "vote_average";
            final String TMD_RELEASE_DATE = "release_date";
            final String TMD_DESCRIPTION = "overview";
            JSONObject movieJson = new JSONObject(movies_data);
            JSONArray movieArray = movieJson.getJSONArray(TMD_RESULTS);

            for(int i=0;i<20;i++) {
                JSONObject result = movieArray.getJSONObject(i);
                Data[i][0] = result.getString(TMD_TITLE);
                Data[i][1] = result.getString(TMD_RATINGS);
                Data[i][2] = result.getString(TMD_RELEASE_DATE);
                Data[i][3] = result.getString(TMD_DESCRIPTION);
                Log.v("Poster Path:--->", result.getString(TMD_POSTER_PATH));
                image_url[i] = "http://image.tmdb.org/t/p/w185/" + result.getString(TMD_POSTER_PATH);
            }

        }
    }
}
