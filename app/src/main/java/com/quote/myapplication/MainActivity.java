package com.quote.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private String API_URL = "https:/api.forismatic.com/api/1.0/?method=getQuote&format=json";

    private String QUOTE_TEXT_FIELD = "quoteText";
    private String QUOTE_AUTHOR_FIELD = "quoteAuthor";

    public TextView quote;
    public TextView author;
    public Button button;

    private boolean loading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quote = findViewById(R.id.quote);
        author = findViewById(R.id.author);
        button = findViewById(R.id.button);
    }

    public void getQuote(View v) {
        if (loading) return;
        setLoading(true);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadQuote();
            }
        }, 100);
    }

    public void loadQuote() {
        final int random = new Random().nextInt(999999);

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    URL url = new URL(API_URL + "&key=" + random);
                    HttpURLConnection  urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    StringBuilder buffer = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(), "utf-8"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    String response = buffer.toString();

                    JSONObject jObject = new JSONObject(response);
                    final String quoteText = jObject.getString(QUOTE_TEXT_FIELD);
                    final String quoteAuthor = jObject.getString(QUOTE_AUTHOR_FIELD);

                    Log.d("RESULT_RESULT", Integer.toString(random));
                    Log.d("RESULT_RESULT", response);

                    urlConnection.disconnect();

                    handler.post(new Runnable(){
                        public void run(){
                            quote.setText(quoteText);
                            author.setText(quoteAuthor);
                            setLoading(false);
                        }
                    });

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void setLoading(Boolean isLoading) {
        if (isLoading) {
            loading = true;
            quote.setText("Loading..." + "\u3000");
            author.setText("");
            button.setEnabled(false);
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    button.setEnabled(true);
                    loading = false;
                }
            }, 100);

        }

    }
}
