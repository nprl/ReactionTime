package com.example.arsh.reactiontime;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class WelcomeScreen extends AppCompatActivity {

    protected String layout;
    protected String size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                EditText nameEdit = (EditText) findViewById(R.id.editText);
                EditText condEdit = (EditText) findViewById(R.id.editCond);
                EditText stageEdit = (EditText) findViewById(R.id.editStage);
                EditText visitEdit = (EditText) findViewById(R.id.editVisit);

//
//                Snackbar.make(view, "Replace with your own action" + nameEdit.getText(), Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();


                Intent i = new Intent(WelcomeScreen.this, ReadyScreen.class); ////////////////////////this is how you do the new thing
                i.putExtra("name", nameEdit.getText().toString());

                String cond = condEdit.getText().toString(); //condEdit.getText().toString()
                if (cond.equals("TESTR") || cond.equals("TESTL") || cond.equals("ARSH")) {
                    i.putExtra("cond", cond);
                } else {
                    Snackbar.make(view, "cond should be TESTR or TESTL", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }


                i.putExtra("stage", stageEdit.getText().toString());
                String visit = visitEdit.getText().toString();
                int v = Integer.parseInt(visit);
                i.putExtra("visit", v);


                //check if layout or arc null;
                if (layout == null) {
                    //select layout
                    Snackbar.make(view, "please select a layout", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    i.putExtra("layout",layout);
                }

                if (size == null) {
                    //select size
                    Snackbar.make(view, "please select a size", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    i.putExtra("size",size);
                }


                if (layout != null && size != null && (cond.equals("TESTR") || cond.equals("TESTL") || cond.equals("ARSH")) ) {
                    startActivity(i);
                }


                //Log.d("editText", " is : " + nameEdit.getText());


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onLayoutClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioRow:
                if (checked)

                    layout = "row";
                    break;
            case (R.id.radioArc):
                if (checked)
                    layout = "arc";
                    break;
        }
    }

    public void onSizeClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioSmall:
                if (checked)
                    size = "small";
                break;
            case (R.id.radioLarge):
                if (checked)
                    size = "large";
                break;
        }
    }
}
