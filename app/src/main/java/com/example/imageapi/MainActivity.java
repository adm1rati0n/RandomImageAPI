package com.example.imageapi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private final static String IMAGE_URL = "https://api.thecatapi.com/v1/images/search";
    private String loadedImageUrl;
    private ImageView imageView;
    Button updBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updBtn = findViewById(R.id.updateBtn);
        updBtn.setOnClickListener(view -> {
            new ImageLoader().execute();
        });
        imageView = findViewById(R.id.imageView);

        new ImageLoader().execute();
    }
    private void updateImage(String imageUrl) {
        Glide
                .with(this)
                .load(imageUrl)
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(imageView);
    }

    private class ImageLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String jsonString = getJson(IMAGE_URL);

            try {
                JSONObject mainObj = new JSONArray(jsonString).getJSONObject(0);
                loadedImageUrl = mainObj.getString("url");
            } catch(JSONException exception) {
                exception.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            imageView.setImageResource(R.drawable.loading);
        }

        @Override
        protected void onPostExecute(Void unused) {
            updateImage(loadedImageUrl);
        }
    }

    private String getJson(String urlString) {
        String data = "";

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(),
                        StandardCharsets.UTF_8
                ));
                data = reader.readLine();
                connection.disconnect();
            }
        } catch(IOException exception) {
            exception.printStackTrace();
        }

        return data;
    }
}