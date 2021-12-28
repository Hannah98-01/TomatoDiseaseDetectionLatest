package com.example.tomatodiseasedetectionlatest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button buttonDiagnose;
    private Button buttonHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buttonDiagnose=(Button) findViewById(R.id.diagnose);
        buttonDiagnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivityDiagnose();
            }
        });

        buttonHistory=(Button) findViewById(R.id.history);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivityHistory();
            }
        });

    }

    private void openActivityHistory() {
        Intent intent = new Intent(this,History_Activity.class);
        startActivity(intent);
    }

    private void openActivityDiagnose() {
        Intent intent = new Intent(this,Detection_Activity.class);
        startActivity(intent);
    }
}
