package owlbotdictionary;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidproject.owlbotdictionary.R;

public class Information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            String def = extras.getString("def");
            String type = extras.getString("type");
            String example = extras.getString("example");


            TextView txtDef = findViewById(R.id.def);
            TextView txtExample = findViewById(R.id.example);
            TextView txtType = findViewById(R.id.type);

            txtDef.setText(def);
            txtExample.setText(type);
            txtType.setText(example);

        }

    }
}