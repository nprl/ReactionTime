package com.example.arsh.reactiontime;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static android.widget.RelativeLayout.CENTER_VERTICAL;
import static java.util.Arrays.fill;

public class ActualGame extends AppCompatActivity {


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    Button b1, b2, b3, b4;
    String seq = "423142124323";
    String sequence = "";
    int count = 0;
    private long[] onset; //array of the onsets of the time when the rectangle changes color
    private long[] offset; //arrry of the offsets of the time when the user presses the key
    private long[] reactionTimes; // array of the reaction times. where at each index -- starting with 0 and ending at trials - 1
    private String[] bad;
    private String error = "";


    private String name, cond, stage;
    private int visit;

    private int runs = 15;

    //initialize basic vars
    long begSum = 0L, endSum = 0L,  totalSum = 0L;
    double begBad = 0, endBad = 0, totalBad = 0;
    //initialize array vars
    long[] avgSeqReaction = new long[runs];
    double[] avgSecAccuracy = new double[runs];

    long randFirstAvg = 0L;// = Math.round(endSum/(double)50);
    double randFirstAcc = 0.0; // = Math.round((50 - endBad)/(double)50); /// how to calc the accuracy

    long randLastAvg = 0L;// = Math.round(endSum/(double)50);
    double randLastAcc = 0.0; // = Math.round((50 - endBad)/(double)50); /// how to calc the accuracy

    long avgReaction = 0L;// = Math.round(totalSum/(double)sequence.length());
    double avgAccuracy = 0.0;// = (double)Math.round((sequence.length() - totalBad)/(double)sequence.length());

    private double interrupted = -1.0; //-1 if never interruped. if interruped then count.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_game);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
            //getWindow().setStatusBarColor(Color.BLACK);


        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
        {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                    {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility)
                        {
                            if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                            {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(ActualGame.this, fullscreen.class);
//                startActivity(i);

                interrupted = (double) count; //interrupted will equal the number of keys that have been pressed
                count = sequence.length() - 1; //in order to end and write files and stufff
                next();
            }
        });

        Bundle extras = getIntent().getExtras();

        String layout, size;

        if(extras != null)
        {
            name = extras.getString("name");
            visit = extras.getInt("visit");
            stage = extras.getString("stage");
            cond = extras.getString("cond");
            layout = extras.getString("layout");
            size = extras.getString("size");
            //b2.setHeight(10);
        } else {
            name = "default";
            visit = -1;
            stage = "default";
            cond = "default";
            layout = "row";
            size = "small";

        }

//        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(View.GONE);
//        decorView.setVisibility(View.GONE);
//
//
//        //may not need rest of the stuff
////                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////
//        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        //MUST INITIALIZE BEFORE USING B1..B4
        initializeButtons();

        if (size.equals("small") && layout.equals("row")) {
            //make vertical center of r2 true
            //or set content view
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) b1.getLayoutParams();
            params1 = changeSize(params1, 80);
            params1 = changeMargins(params1, 25, 25);
            params1.addRule(CENTER_VERTICAL);
            b1.setLayoutParams(params1);

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) b2.getLayoutParams();
            params2 = changeSize(params2, 80);
            params2 = changeMargins(params2, 25, 25);
            params2.addRule(CENTER_VERTICAL);
            b2.setLayoutParams(params2);

            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) b3.getLayoutParams();
            params3 = changeSize(params3, 80);
            params3 = changeMargins(params3, 25, 25);
            params3.addRule(CENTER_VERTICAL);
            b3.setLayoutParams(params3);

            RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) b4.getLayoutParams();
            params4 = changeSize(params4, 80);
            params4 = changeMargins(params4, 25, 25);
            params4.addRule(CENTER_VERTICAL);
            b4.setLayoutParams(params4);

        }


        if (size.equals("large") && layout.equals("row")) {
            //make vertical center of r2 true
            //or set content view
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) b1.getLayoutParams();
            params1 = changeSize(params1, 160);
            params1 = changeMargins(params1, 35, 35);
            params1.addRule(CENTER_VERTICAL);
            b1.setLayoutParams(params1);

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) b2.getLayoutParams();
            params2 = changeSize(params2, 160);
            params2 = changeMargins(params2, 35, 35);
            params2.addRule(CENTER_VERTICAL);
            b2.setLayoutParams(params2);

            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) b3.getLayoutParams();
            params3 = changeSize(params3, 160);
            params3 = changeMargins(params3, 35, 35);
            params3.addRule(CENTER_VERTICAL);
            b3.setLayoutParams(params3);

            RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) b4.getLayoutParams();
            params4 = changeSize(params4, 160);
            params4 = changeMargins(params4, 35, 35);
            params4.addRule(CENTER_VERTICAL);
            b4.setLayoutParams(params4);

        }

        if (size.equals("small") && layout.equals("arc")) {
            //make vertical center of r2 true
            //or set content view
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) b1.getLayoutParams();
            params1 = changeSize(params1, 80);
            params1 = changeMargins(params1, 100, 100);
            b1.setLayoutParams(params1);

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) b2.getLayoutParams();
            params2 = changeSize(params2, 80);
            params2 = changeMargins(params2, 153, 100);
            params2.addRule(RelativeLayout.ABOVE, R.id.R1);
            b2.setLayoutParams(params2);

            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) b3.getLayoutParams();
            params3 = changeSize(params3, 80);
            params3 = changeMargins(params3, 153, 100);
            params3.addRule(RelativeLayout.ABOVE, R.id.R1);
            b3.setLayoutParams(params3);

            RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) b4.getLayoutParams();
            params4 = changeSize(params4, 80);
            params4 = changeMargins(params4, 100, 100);
            b4.setLayoutParams(params4);



        }

        if (size.equals("large") && layout.equals("arc")) {
            //make vertical center of r2 true
            //or set content view
            RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) b1.getLayoutParams();
            params1 = changeSize(params1, 160);
            params1 = changeMargins(params1, 45, 45);
            b1.setLayoutParams(params1);

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) b2.getLayoutParams();
            params2 = changeSize(params2, 160);
            params2 = changeMargins(params2, 80, 45);
            params2.addRule(RelativeLayout.ABOVE, R.id.R1);
            b2.setLayoutParams(params2);

            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) b3.getLayoutParams();
            params3 = changeSize(params3, 160);
            params3 = changeMargins(params3, 80, 45);
            params3.addRule(RelativeLayout.ABOVE, R.id.R1);
            b3.setLayoutParams(params3);

            RelativeLayout.LayoutParams params4 = (RelativeLayout.LayoutParams) b4.getLayoutParams();
            params4 = changeSize(params4, 160);
            params4 = changeMargins(params4, 45, 45);
            b4.setLayoutParams(params4);

        }
        //THIS
        //sequence = seq;

//        Toast.makeText(this, sequence, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, sequence.length() + "", Toast.LENGTH_SHORT).show();

        //OR THIS
//

        if (cond.equals("TESTR") || cond.equals("TESTL")) {
            sequence = sequence + "24132413412432431243124132432412432134231241324321";
            for (int x = 0; x < runs; x++) { //b/c 15 times
                sequence = sequence + seq; // seq repeated 15 times
            }
            sequence = sequence + "12341243214213214231234231231423142341231231412432";
        } else if (cond.equals("ARSH")) {
            sequence = "11111111111111111111111111111111111111111111111111"
                + "111111111111" + "111111111111" + "111111111111"
                + "111111111111" + "111111111111" + "111111111111"
                + "111111111111" + "111111111111" + "111111111111"
                + "111111111111" + "111111111111" + "111111111111"
                + "111111111111" + "111111111111" + "111111111111"
                + "11111111111111111111111111111111111111111111111111";
        } else {
            Toast.makeText(this, "Please Restart and Try Again", Toast.LENGTH_SHORT).show();
        }



        onset = new long[sequence.length()];
        offset = new long[sequence.length()];
        reactionTimes = new long[sequence.length()];
        bad = new String[sequence.length()];

        fill(onset, 0);
        fill(offset, 0);
        fill(reactionTimes, 0);
        fill(bad, "");
        fill(avgSeqReaction, 0);
        fill(avgSecAccuracy, 0);


        //following necessary?
//        for (int x = 0; x < sequence.length(); x++) {
//            bad[x] = "";
//        }

        makeAllWhite();
        runGame();
    }




    /**
     * recursive method that is called every time we are waiting for a button to be pressed
     */
    protected void runGame() {

        //for (int x = 0; x < sequence.length(); x++) {
        final Button curButton = getButton(sequence.charAt(count));
        turnBlue(curButton);
        onset[count] = System.currentTimeMillis();
        //bad[count] = ""; //initialize

        //start timer for measuring reaction time

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bad[count] = bad[count] + "1";
        }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bad[count] = bad[count] + "2";
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bad[count] = bad[count] + "3";
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bad[count] = bad[count] + "4";
            }
        });

        curButton.setOnClickListener(new View.OnClickListener() { //override currentButton listener
            @Override
            public void onClick(View view) {

                offset[count] = System.currentTimeMillis();
                reactionTimes[count] = offset[count] - onset[count];

//                Snackbar.make(view, "curButton pressed", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                //Log.d(curButton.getText().toString(),bad[count]);
                makeAllWhite();
                next(); //do NOT use count after this.

            }
        });

        //other onclick listeners to record bad ones?

        //get this to work more
        //then write to files



//            Log.d("hey", "i've arrived");
//            while (pressed == 0) {
//                onClick();
//            }
            //pressed = 0;

            //Log.d("hey", "i've arrived");
            //curButton.setBackgroundColor(Color.WHITE);

            //wait for press
        //}

    }

    /**
     * sees if there is another rectange left in the sequence
     * if so, it turns it blue if not, it ends
     */
    private void next() {
        if (count + 1 == sequence.length()) {
            Toast.makeText(this, "stop clicking", Toast.LENGTH_SHORT).show();
            //end game
            b1.setClickable(false);
            b2.setClickable(false);
            b3.setClickable(false);
            b4.setClickable(false);

            calculateSegments(); //add back
            writeAll();
            //Log.d("Game", "over!");
            Toast.makeText(this, "#done", Toast.LENGTH_SHORT).show();

        } else {
            count++;
            runGame();
        }
    }

    private void writeAll() {

        //verifyStoragePermissions(this); // is this necessary???
        String path =
                Environment.getExternalStorageDirectory() + File.separator + "ReactionTime" + File.separator;
        //Environment.getExternalStorageDirectory() + File.separator  + "ReactionTime"; //phone

        // Create the ReactionTime folder. (will be in same dirrectory as documents folder)
        File folder = new File(path);
        folder.mkdirs();
        try
        {
            /**
             * STUFF FOR MASTER FILE FOLLOWS:
             */
            // Create the srtt.txt file.
            final File file = new File(folder, "SRTT.txt");
            FileWriter myOutWriter = new FileWriter(file, true); //append = true.
            writeBlocks(myOutWriter); //to master

        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
            error = "master file append failed";

        }

        String indivFileName = name + ".txt";
        try {
            final File indiv = new File(folder, indivFileName);
            writeBlocks(new FileWriter(indiv, true)); //to indiv
            writeIndivs(new FileWriter(indiv, true));
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            error = "indiv file append failed. " + name;
        }
    }

    private void writeIndivs(FileWriter indivWriter) throws IOException{
        indivWriter.append("trial\tRxnTime\tExpectd\tActual\t#missed\n");

        for (int x = 0; x < sequence.length(); x++) {
            indivWriter.append(x+ "\t" + reactionTimes[x] + "\t" + sequence.charAt(x) + "\t"
                    + bad[x]  + "\t" + bad[x].length()+"\n");
        }

        indivWriter.append("\n");
        indivWriter.flush();
        indivWriter.close();
    }

    private void writeBlocks(FileWriter myOutWriter) throws IOException {
        myOutWriter.append("ID:\tvisit\ttime\tCond"
                + "\tBlock\tAvgRxnT\tAccuracy\n");

        myOutWriter.append(name +   "\t" + visit+ "\t" + stage +   "\t" + cond
                + "\tRandBeg\t"+randFirstAvg +"\t"+ randFirstAcc+     "\n");

        for (int x = 0; x < runs; x++) {
            myOutWriter.append(name +   "\t" + visit+ "\t" + stage +   "\t" + cond
                    + "\tRep+"+x+"\t"+avgSeqReaction[x] +"\t"+ avgSecAccuracy[x]+ "\n");
        }

        myOutWriter.append(name +   "\t" + visit+ "\t" + stage +   "\t" + cond
                + "\tRandEnd\t"+randLastAvg +"\t"+ randLastAcc+     "\n");

        myOutWriter.append(name +   "\t" + visit+ "\t" + stage +   "\t" + cond
                + "\toverall\t"+avgReaction +"\t"+ avgAccuracy+     "\n");

        myOutWriter.append("\n");
        // Save your stream, don't forget to flush() it before closing it.
        myOutWriter.flush();
        myOutWriter.close();
    }


    private void calculateSegments() {

        if (interrupted == -1) {

            //beginging 50
            for (int x = 0; x < 50; x++) {
                begSum += reactionTimes[x];
                begBad += bad[x].length();
            }

            randFirstAvg = Math.round(begSum / (double) 50);
            randFirstAcc = Math.round((100.0 * (50.0 - begBad) / 50.0)) / 100.0; /// how to calc the accuracy

            //sequences (15 runs)
            for (int x = 0; x < runs; x++) {
                long seqSum = 0L;
                int seqBad = 0;
                for (int y = 0; y < seq.length(); y++) {
                    seqSum += reactionTimes[50 + (seq.length() * x) + y];
                    seqBad += bad[50 + (seq.length() * x) + y].length();
                }
                avgSeqReaction[x] = Math.round(seqSum / (double) seq.length());
                avgSecAccuracy[x] = Math.round(100.0 * ((seq.length() - seqBad) / (double) seq.length())) / 100.0;

                totalSum += seqSum;
                totalBad += seqBad;
            }

            //last 50
            for (int x = 0; x < 50; x++) {
                endSum += reactionTimes[50 + runs * seq.length() + x];
                endBad += bad[50 + runs * seq.length() + x].length();
            }

            randLastAvg = Math.round(endSum / (double) 50);
            randLastAcc = Math.round(100.0 * ((50 - endBad) / (double) 50)) / 100.0; /// how to calc the accuracy


            //overall
            totalSum += begSum + endSum;
            totalBad += begBad + endBad;

            avgReaction = Math.round(totalSum / (double) sequence.length());
            avgAccuracy = Math.round(100.0 * ((sequence.length() - totalBad) / (double) sequence.length())) / 100.0;


        } else { //interrupted


            //beginging 50
            double end = 0.0;
            if (interrupted > 50.0) {
                end = 50.0;
            } else {
                end = interrupted;
            }
            for (int x = 0; x < end; x++) {
                begSum += reactionTimes[x];
                begBad += bad[x].length();
            }
            randFirstAvg = Math.round(begSum / end);
            randFirstAcc = Math.round((100.0 * (end - begBad) / end)) / 100.0; /// how to calc the accuracy


            //sequences (15 runs)
            for (int x = 0; x < runs; x++) {
                if (interrupted > 50 + x*seq.length()) {

                    if(interrupted >= 50 + (x+1)*seq.length()) {
                        end = seq.length();
                    } else {
                        end = interrupted - 50 - (x*seq.length());
                    }
                    long seqSum = 0L;
                    int seqBad = 0;
                    for (int y = 0; y < end; y++) {
                        seqSum += reactionTimes[50 + (seq.length() * x) + y];
                        seqBad += bad[50 + (seq.length() * x) + y].length();
                    }
                    avgSeqReaction[x] = Math.round(seqSum / end);
                    avgSecAccuracy[x] = Math.round(100.0 * ((end - seqBad) / end)) / 100.0;

                    totalSum += seqSum;
                    totalBad += seqBad;
                }
            }

            //last 50
            if (interrupted > 50 + runs*seq.length()) {

                end = interrupted - 50.0 - (double)(runs*seq.length());

                for (int x = 0; x < end; x++) {
                    endSum += reactionTimes[50 + runs * seq.length() + x];
                    endBad += bad[50 + runs * seq.length() + x].length();
                }

                randLastAvg = Math.round(endSum / end);
                randLastAcc = Math.round(100.0 * ((end - endBad) / end)) / 100.0; /// how to calc the accuracy
            }

            //overall
            totalSum += begSum + endSum;
            totalBad += begBad + endBad;

            avgReaction = Math.round(totalSum / interrupted);
            avgAccuracy = Math.round(100.0 * ((interrupted - totalBad) / interrupted)) / 100.0;


        }

    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    /**
     * makes all the buttons white
     */
    private void makeAllWhite() {
        b1.setBackgroundColor(Color.WHITE);
        b2.setBackgroundColor(Color.WHITE);
        b3.setBackgroundColor(Color.WHITE);
        b4.setBackgroundColor(Color.WHITE);
    }

    /**
     * return button depending on which character is next in the sequence
     * @param b character of the sequence
     * @return button that should turn blue
     */
    protected Button getButton(char b) {
        if (b == '1') {
            return b1;
        } else if (b == '2') {
            return b2;
        } else if (b == '3') {
            return b3;
        } else { //if its 4
            return b4;
        }

    }

    /**
     * Create all the buttons so we can use the b1..b4 variables
     */
    protected void initializeButtons () {
        b1 = (Button) findViewById(R.id.R1);
        b2 = (Button) findViewById(R.id.R2);
        b3 = (Button) findViewById(R.id.R3);
        b4 = (Button) findViewById(R.id.R4);
    }

    /**
     * makes a button blue
     * @param b the button you want to make blue
     */
    protected void turnBlue(Button b) {
        b.setBackgroundColor(Color.CYAN);
    }

    private RelativeLayout.LayoutParams changeSize(RelativeLayout.LayoutParams params, int size) {
        params.height = size;
        params.width = size;
        return params;
    }


    private RelativeLayout.LayoutParams changeMargins(RelativeLayout.LayoutParams params, int x, int y) {
        params.leftMargin = x;
        params.rightMargin = x;
        params.topMargin = y;
        params.bottomMargin = y;
        return params;
    }

//    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


}
