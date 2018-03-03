package com.fatihkirbas.catchthekenny;

import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    MediaPlayer media;
    TextView scoreText;
    TextView timeText;
    TextView bestScore;
    ImageView imagaView1;
    ImageView imagaView2;
    ImageView imagaView3;
    ImageView imagaView4;
    ImageView imagaView5;
    ImageView imagaView6;
    ImageView imagaView7;
    ImageView imagaView8;
    ImageView imagaView9;
    int score;
    ImageView[] imageArray;
    Handler handler;
    Runnable runnable;
    Button restartButton, quitButton, soundButton;
    CountDownTimer dt;
    boolean isFinished = false;
    int best_Score = 0;
    SharedPreferences sharedPref;
    Vibrator titresim;

    //Bu metot uygulama alta alındığında yapılacak listeleri yerine getirir.
    protected void onPause() {
        super.onPause();
        media.pause();
    }
    //Bu metot uygulama geri açıldığında devam edilecek özellikleri yerine getirir.
    protected void onResume(){
        media.start();
        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagaView1 = (ImageView) findViewById(R.id.imageView);
        imagaView2 = (ImageView) findViewById(R.id.imageView2);
        imagaView3 = (ImageView) findViewById(R.id.imageView3);
        imagaView4 = (ImageView) findViewById(R.id.imageView4);
        imagaView5 = (ImageView) findViewById(R.id.imageView5);
        imagaView6 = (ImageView) findViewById(R.id.imageView6);
        imagaView7 = (ImageView) findViewById(R.id.imageView7);
        imagaView8 = (ImageView) findViewById(R.id.imageView8);
        imagaView9 = (ImageView) findViewById(R.id.imageView9);
        restartButton = (Button) findViewById(R.id.restart);
        quitButton = (Button) findViewById(R.id.quit);
        scoreText = (TextView) findViewById(R.id.textScore);
        timeText = (TextView) findViewById(R.id.textTime);
        bestScore = (TextView) findViewById(R.id.bestScore);
        soundButton = (Button) findViewById(R.id.sound);

        imageArray = new ImageView[]{imagaView1, imagaView2, imagaView3, imagaView4, imagaView5, imagaView6, imagaView7, imagaView8, imagaView9};

        //Titreşim etkinleştirme metodu yanlız AndroidManifest içerisinde izin almak ve main içerisinde titreşim tanımlamak gerekiyor.
        titresim = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);

        media = MediaPlayer.create(MainActivity.this, R.raw.gameover);
        media.start();
        media.setLooping(true);

        //SharedPreference değer kaydetme metodu.
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        best_Score = sharedPref.getInt("en_yuksek_skor", 0);

        hideImages();
        score = 0;
        dt = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long milliUntilFinished) {

                timeText = (TextView) findViewById(R.id.textTime);
                timeText.setText("Time: " + milliUntilFinished / 1000);

            }

            @Override
            public void onFinish() {
                timeText = (TextView) findViewById(R.id.textTime);
                timeText.setText("Time's Off");
                handler.removeCallbacks(runnable);
                isFinished = true;
                restartButton.setVisibility(View.VISIBLE);
                quitButton.setVisibility(View.VISIBLE);
                bestScore.setVisibility(View.VISIBLE);
                if (score > best_Score) {
                    best_Score = score;

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("en_yuksek_skor", best_Score);
                    editor.commit();
                }
                bestScore.setText("The Best Score: " + best_Score);

            }
        }.start();

        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (media.isPlaying()) {
                    media.pause();
                    soundButton.setBackgroundResource(R.drawable.soundoff);
                } else {
                    media.start();
                    soundButton.setBackgroundResource(R.drawable.soundon);
                }
            }
        });
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFinished = false;
                restartButton.setVisibility(View.INVISIBLE);
                hideImages();
                score = 0;
                scoreText.setText(getResources().getString(R.string.score) + ": " + score);
                dt.start();
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Exit");
                builder.setMessage("Do you want to exit?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Good bye", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Okey! Good luck!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        AdView adview = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        adview.loadAd(adRequest);
    }

    public void increaseScore(View view) {
        if (!isFinished) {
            scoreText = (TextView) findViewById(R.id.textScore);

            score++;

            scoreText.setText(getResources().getString(R.string.score) + ": " + score);

            titresim.vibrate(80);
            Log.i("Titresim", "Titresim...");
        }
    }

    public void hideImages() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                for (ImageView image : imageArray) {
                    image.setVisibility(View.INVISIBLE);
                }

                Random r = new Random();
                int i = r.nextInt(imageArray.length);
                imageArray[i].setVisibility(View.VISIBLE);

                handler.postDelayed(this, 500);
            }
        };

        handler.post(runnable);

    }
}

