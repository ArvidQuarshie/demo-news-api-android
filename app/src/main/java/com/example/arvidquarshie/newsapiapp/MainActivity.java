package com.example.arvidquarshie.newsapiapp;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import adapters.ArticlesAdapter;
import model.Article;

public class MainActivity extends AppCompatActivity {
    private TextView getResponse;
    private ImageView image;
    private TextView mTxtDisplay;

    //    https://newsapi.org/v1/articles?source=the-next-web&sortBy=latest&apiKey=7b59453e5e9848d9aeeb923a1dd581d0
    private String urlApi = "https://newsapi.org/v1/articles?source=abc-news-au&sortBy=top&apiKey=7b59453e5e9848d9aeeb923a1dd581d0";
    public static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private ArticlesAdapter mAdapter;
    private List<Article> articleArrayList = new ArrayList<>();
    private String TITLE;
    private String DESCRIPTION;
    private String IMAGE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        getResponse = (TextView) findViewById(R.id.description);
        mTxtDisplay = (TextView) findViewById(R.id.title);
//        image = (ImageView)findViewById(R.id.urlToImage);

//        getResponse.setBackgroundColor(0xFF00CC00);
//        mTxtDisplay.setText("You are connected");


        // call AsynTask to perform network operation on separate thread

        if (isConnected()) {
            new HttpAsyncTask().execute(urlApi);
        } else {
            Toast.makeText(getBaseContext(), "Error" + "Please check Network Connection", Toast.LENGTH_LONG).show();
        }
        sampleData(TITLE, DESCRIPTION, IMAGE_URL);
        mAdapter = new ArticlesAdapter(articleArrayList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    public void sampleData(String TITLE, String DESCRIPTION, String IMAGE_URL) {
        Article article = new Article(TITLE, DESCRIPTION, "");
        articleArrayList.add(article);


    }

//    Get inputStream from the endpoint after passing the url

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            URL newsApiEndPoint = new URL(url);

            // make GET request to the given URL
            HttpsURLConnection myConnection =
                    (HttpsURLConnection) newsApiEndPoint.openConnection();
            inputStream = myConnection.getInputStream();


            if (inputStream != null)
                result = convertInputStreamToString(myConnection.getInputStream());
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    //Converts the fetched input Stream into a string for easy parsing.

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    //check if there is a network connection
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public interface ClickListener {
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!" + "You are connected to NEWS API:", Toast.LENGTH_LONG).show();
            try {
                org.json.JSONObject jsonObject = new org.json.JSONObject(result);
                ArrayList<Article> articleArrayList = new ArrayList<>();
                String articles = jsonObject.getString("articles");
                Log.v("connection articles", articles);
                Toast.makeText(getBaseContext(), " Welcome to Articles:" + "You are connected to NEWS API:", Toast.LENGTH_LONG).show();

                HashMap<String, String> articleHashMap = new HashMap<>();
                JSONArray jsonArray = new JSONArray(articles);

                for (int i = 0; i < jsonArray.length(); i++) {
                    org.json.JSONObject jObject = jsonArray.getJSONObject(i);
                    TITLE = jObject.getString("title");
                    DESCRIPTION = jObject.getString("description");
                    String url = jObject.getString("url");
                    IMAGE_URL = jObject.getString("urlToImage");

                    Log.v("results TITLE:", TITLE);
                    Log.v("results DESCRIPTION:", DESCRIPTION);
                    Log.v("results URL:", IMAGE_URL);
                    Log.v("results URL_IMAGE:", IMAGE_URL);

//                    articleHashMap.put("TITLE", TITLE);
//                    articleHashMap.put("DESCRIPTION",DESCRIPTION);

                    sampleData(TITLE, DESCRIPTION, "");


                }
                mTxtDisplay.setText(TITLE);
                getResponse.setText(DESCRIPTION);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v("connection", result);
//            mTxtDisplay.setText(result);
        }
    }
}


