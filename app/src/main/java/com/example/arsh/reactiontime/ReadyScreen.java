package com.example.arsh.reactiontime;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ReadyScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        final String name, stage, cond, size, layout;
        final int visit;

        if(extras != null)
        {
            name = extras.getString("name");
            visit = extras.getInt("visit");
            stage = extras.getString("stage");
            cond = extras.getString("cond");

            layout = extras.getString("layout");
            size = extras.getString("size");


        } else {
            name = "default";
            visit = -1;
            stage = "default";
            cond = "default";
            layout = "default";
            size = "default";

        }

//        Log.d("name", name);
//        Log.d("visit", visit + " readyScreen");
//        Log.d("stage", stage);
//        Log.d("cond", cond);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "replace with your own text", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent i = new Intent(ReadyScreen.this, ActualGame.class);
                i.putExtra("name", name);
                i.putExtra("stage", stage);
                i.putExtra("cond", cond);
                i.putExtra("visit", visit);
                i.putExtra("layout", layout);
                i.putExtra("size",size);

                startActivity(i);

            }
        });
    }

//    private void createSequence() {
//
//
//    }


}
