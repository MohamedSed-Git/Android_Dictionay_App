package owlbotdictionary;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.androidproject.owlbotdictionary.R;

public class MainActivity extends AppCompatActivity {

    public static final String WORD ="word" ;
    public static final String DEFINE = "define";
    public static final String PRONUNCIATION = "pronunciation";
    EditText ed;
    Button search;
    private String stringURL;
    String editSearch;
    TextView showWord;
    TextView def;
    TextView pro;
    DataBase dataBase = new DataBase(MainActivity.this);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        search = findViewById(R.id.search_button);
        ed = findViewById(R.id.search_text);
        String get = ed.getText().toString();


        SharedPreferences  pref = getSharedPreferences("mypreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("type", get);
        editor.apply();

        String hi =pref.getString("type","no");
        ed.setText(hi);

//        editor.putString("key_name",get); // Storing string
//        editor.apply();
//        String key = pref.getString("key_name", null); // getting String


        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        search.setOnClickListener(click -> {
            editSearch = ed.getText().toString();
            myToolbar.getMenu().add(0, 1, 0, editSearch)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            runForcast(editSearch);


        });
    }
    @Override
    public boolean onOptionsItemSelected( MenuItem item) {

        switch (item.getItemId()) {
            case R.id.clear:
                pro.setVisibility(View.INVISIBLE);
                def.setVisibility(View.INVISIBLE);
                showWord.setVisibility(View.INVISIBLE);
                ed.setText("");
                break;
            case R.id.help:
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Help")
                        .setMessage("Type in the search box below and press search button. click save button if you want to sve "+ search)
                        .show();

        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void runForcast(String search) {
        ProgressDialog progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);//you can cancel it by pressing back button
        progressBar.setMessage("Processing...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();//displays the progress bar


            Executor newThread = Executors.newSingleThreadExecutor();
            newThread.execute( () -> {
                try {

                    stringURL = "https://owlbot.info/api/v4/dictionary/"
                            + URLEncoder.encode(editSearch, "UTF-8");

                    URL url = new URL(stringURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("Authorization", "Token 16ee0955a4a3550d113eb768cbcb1eaba7934384");
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    String text = (new BufferedReader(
                            new InputStreamReader(in, StandardCharsets.UTF_8)))
                            .lines()
                            .collect(Collectors.joining("\n"));
                    Log.d(" connected",text);
                    JSONObject theDocument = new JSONObject(text);

                    String word = theDocument.getString("word");
                    String pronunciation = theDocument.getString("pronunciation");

                    //have some problems here
//                    JSONArray pobject = theDocument.getJSONArray("pronunciation");
//                    String pronunciation = pobject.getString("pronunciation");
                    JSONArray definitions = theDocument.getJSONArray("definitions");
                    JSONObject position0 = definitions.getJSONObject(0);
                    String define = position0.getString("definition");



//                    JSONObject mainObject = theDocument.getJSONObject("pronunciation");
//                    String value = mainObject.getString("pronunciation");
                    runOnUiThread(() -> {

                        pro = findViewById(R.id.pronunciation);
                        pro.setText("Pronunciation: " +pronunciation );
                        pro.setVisibility(View.VISIBLE);

                        def = findViewById(R.id.definition);
                        def.setText("Definition: " + define);
                        def.setVisibility(View.VISIBLE);

                        showWord = findViewById(R.id.word);
                        showWord.setText("Word: " + word);
                        showWord.setVisibility(View.VISIBLE);
                        progressBar.hide();
//                        dialog.hide();

                        boolean checkInsert = dataBase.insert(word, define, pronunciation);
                        if(checkInsert == true){
                            Toast.makeText(MainActivity.this, "Definition inserted into database", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Definition not inserted into database", Toast.LENGTH_SHORT).show();
                        }

                    });
                } catch(IOException | JSONException ioe) {
                    Log.e("Connection Error", ioe.getMessage());
                }
            });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, " onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, " onPause");
        String get = ed.getText().toString();

        //Save the shared preference while the activity is paused
        //Search term is saved and will be called back in the onCreate method.
        SharedPreferences myPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        editor.putString("type", get);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, " onStop");

        String get = ed.getText().toString();

        //Save the shared preference while the activity is paused
        //Search term is saved and will be called back in the onCreate method.
        SharedPreferences myPref = getSharedPreferences("myPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPref.edit();
        editor.putString("type", get);
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, " onDestroy");
    }
}
