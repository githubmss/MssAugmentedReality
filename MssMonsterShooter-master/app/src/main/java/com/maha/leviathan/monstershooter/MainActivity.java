package com.maha.leviathan.monstershooter;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.view.BeyondarGLSurfaceView;
import com.beyondar.android.view.OnTouchBeyondarViewListener;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    //    Variabel untuk menampilkan dunia AR dalam aplikasi
    BeyondarFragmentSupport mBeyondarFragment;
    World world;

    //    Variabel untuk widget pada layout yang telah dibuat sebelumnya
    TextView skorText, countDown, info, Textdurasi, skorFinal;
    ImageView cross;
    Button bMenu;
    RelativeLayout skorFinalLayout;
    int isPauseCount = 0;
    //    Variabel untuk keperluan operasional game,
    //    mulai dari mengatur posisi monster berdasarkan radius, mengatur gameplay, dll
    Timer spawn = new Timer();
    Random random = new Random();
    double radiusInDegrees = 20 / 111000f, w, t;
    int skorInt = 0, mulai = 0, count = 5, durasi, id = 0;

    //    Variabel array yang berisi monster-monster yang akan ditampilkan
    int[] img = {R.drawable.monster1,
            R.drawable.monster2,
            R.drawable.monster3,
            R.drawable.monster4,
            R.drawable.monsterboss
    };

    //    Variabel arraylist yang digunakan pada event touch listener
    ArrayList<GeoObject> geoObjArr = new ArrayList<GeoObject>();
    ArrayList<GeoObject> geoObjMenu = new ArrayList<GeoObject>();
    private Button btnPause;
    private Button btnPlay;
    private Button btnHighScore;
    private Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        btnPlay = (Button) findViewById(R.id.btnpplay);
        btnPause = (Button) findViewById(R.id.btnpause);
        btnHighScore = (Button) findViewById(R.id.btn_high_score);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnExit.setVisibility(View.GONE);
        btnHighScore.setVisibility(View.GONE);
        btnPlay.setVisibility(View.GONE);
        btnPause.setVisibility(View.GONE);
        skorText = (TextView) findViewById(R.id.textViewSkor);
        skorFinal = (TextView) findViewById(R.id.textViewSkorFinal);
        skorFinalLayout = (RelativeLayout) findViewById(R.id.skorFinal);
        cross = (ImageView) findViewById(R.id.imageView);
        countDown = (TextView) findViewById(R.id.textViewCountDown);
        info = (TextView) findViewById(R.id.textView2);
        Textdurasi = (TextView) findViewById(R.id.textView);
        bMenu = (Button) findViewById(R.id.buttonBToMenu);
        bMenu.setVisibility(View.INVISIBLE);
        countDown.setText("");
        skorText.setText("0");
        skorFinal.setText("");
        skorFinalLayout.setVisibility(View.INVISIBLE);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int xMax = size.x;
        final int yMax = size.y;

        final MediaPlayer gun = MediaPlayer.create(this, R.raw.gun);
        final MediaPlayer tada = MediaPlayer.create(this, R.raw.tada);
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);
        world = new World(this);
        world.setDefaultImage(R.mipmap.ic_launcher);
        world.setGeoPosition(-8.691782, 115.223726);
        mBeyondarFragment.setWorld(world);

        bMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDown.setText("");
                backToMenu();
            }
        });


        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (durasi != 0) {
                    isPauseCount = durasi;
                }
                btnHighScore.setVisibility(View.VISIBLE);
                durasi = 0;
                skorFinalLayout.setVisibility(View.INVISIBLE);
                btnExit.setVisibility(View.VISIBLE);
                bMenu.setVisibility(View.INVISIBLE);

                btnPlay.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.GONE);


            }
        });
        btnHighScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_conectivity);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                TextView txtMessageTitle = (TextView) dialog.findViewById(R.id.txt_message_title);
                txtMessageTitle.setText("High Score:" + new AppPreferences(MainActivity.this).getPrefrenceInt("scorecount"));
                Button btnok = (Button) dialog.findViewById(R.id.btn_ok);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.gravity = Gravity.CENTER;
                wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.show();
                btnok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnExit.setVisibility(View.GONE);
                mulai = 1;
                count = 0;
                durasi = isPauseCount;
                isPauseCount = 0;
                btnHighScore.setVisibility(View.GONE);
                btnPlay.setVisibility(View.GONE);
                btnPause.setVisibility(View.VISIBLE);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Textdurasi.setText("0");
                skorFinal.setText("0");
                skorText.setText("0");
                isPauseCount = 0;
                btnHighScore.setVisibility(View.GONE);
                btnPause.setVisibility(View.GONE);
                btnPlay.setVisibility(View.GONE);
                countDown.setText("");
                backToMenu();
                btnExit.setVisibility(View.GONE);

            }
        });

        mBeyondarFragment.setOnTouchBeyondarViewListener(new OnTouchBeyondarViewListener() {
            @Override
            public void onTouchBeyondarView(MotionEvent motionEvent, BeyondarGLSurfaceView beyondarGLSurfaceView) {
                try {
                    gun.reset();
                    gun.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.gun));
                    gun.prepare();
                    gun.start();


                } catch (IOException e) {
                    e.printStackTrace();
                }

                v.vibrate(50);

                ArrayList<BeyondarObject> geoObjects = new ArrayList<BeyondarObject>();


                Log.e("INI RESOLUSI X", String.valueOf(xMax / 2));
                Log.e("INI RESOLUSI Y", String.valueOf(yMax / 2));

                beyondarGLSurfaceView.getBeyondarObjectsOnScreenCoordinates(xMax / 2, yMax / 2, geoObjects);

                if (geoObjects.isEmpty() == false) {
                    if (geoObjects.get(0).getId() == 1000001 || geoObjects.get(0).getId() == 1000002) {
                        for (int i = 0; i < geoObjMenu.size(); i++) {
                            geoObjMenu.get(i).setGeoPosition(0, 0);
                        }
                        startGame();
                    } else if (geoObjects.get(0).getId() == 1000003 || geoObjects.get(0).getId() == 1000004) {
                        finish();
                    } else if (geoObjects.get(0).getId() == 1000005 || geoObjects.get(0).getId() == 1000006) {
                        cross.setVisibility(View.INVISIBLE);
                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_conectivity);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        TextView txtMessageTitle = (TextView) dialog.findViewById(R.id.txt_message_title);
                        txtMessageTitle.setText("High Score:" + new AppPreferences(MainActivity.this).getPrefrenceInt("scorecount"));
                        Button btnok = (Button) dialog.findViewById(R.id.btn_ok);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                        wmlp.gravity = Gravity.CENTER;
                        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog.show();
                        btnok.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                cross.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                            }
                        });
                    } else {
                        if (mulai == 1) {
                            final GeoObject g = (GeoObject) geoObjects.get(0);
//                            g.setGeoPosition(0,0);
                            g.setImageResource(R.drawable.monsterboss1);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    g.setGeoPosition(0, 0);
                                }
                            }, 300);


                            skorInt++;
                            if (skorInt > new AppPreferences(MainActivity.this).getPrefrenceInt("scorecount")) {

                                new AppPreferences(MainActivity.this).setPrefrenceInt("scorecount", skorInt);
                            }

                            skorText.setText(String.valueOf(skorInt));
                        }
                    }
                }
            }
        });

        spawn.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mulai == 1) {
                            cross.setVisibility(View.VISIBLE);
                            Log.e("Mulai", "1");
                            if (count == 0) {
                                Log.e("Stop", "stop" + count + "durasi" + durasi);
                                if (durasi == 0) {
                                    Log.e("durasi " + durasi, "durasi " + durasi);
                                    Textdurasi.setText(String.valueOf(durasi));
                                    for (int i = 0; i < geoObjArr.size(); i++) {


                                        if (isPauseCount == 0) {
                                            geoObjArr.get(i).setImageResource(R.drawable.up);
                                        }
                                    }
                                    bMenu.setVisibility(View.VISIBLE);
                                    try {
                                        tada.reset();
                                        tada.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tada));
                                        tada.prepare();
                                        tada.start();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    if (isPauseCount != 0) {
                                        pauseGame();
                                    } else {
                                        stopGame();
                                    }

                                } else {
                                    btnPause.setVisibility(View.VISIBLE);
                                    Log.e("comming", "comming" + durasi);
                                    countDown.setText("");
                                    info.setText("");
                                    Textdurasi.setText(String.valueOf(durasi));
                                    durasi--;

                                    if (durasi % 2 == 0) {
                                        summonMons();
                                    } else {
                                        summonMons();
                                        summonMons();
                                    }
                                }
                            } else {
                                Log.e("end", "end" + String.valueOf(durasi) + "count" + count);
                                cross.setVisibility(View.INVISIBLE);
                                Textdurasi.setText(String.valueOf(durasi));
                                count--;
                                countDown.setText(" " + String.valueOf(count) + " ");
                                info.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.e("Mulai", "0");
                        }
                    }
                });
            }
        }, 0, 1000);

        geoMenu();
    }

    @Override
    protected void onStop() {
        spawn.cancel();
        super.onStop();
    }

    public void newObjAug(double lat, double lng, String al, int stat, int mon) {
        GeoObject go = new GeoObject(id);
        String altitudenya = "";
        if (stat == 0) {
            altitudenya = altitudenya + "-";
        }
        altitudenya = altitudenya + "0.0000" + al;
        go.setGeoPosition(lat, lng, Double.parseDouble(altitudenya));

        go.setImageResource(img[mon]);
        geoObjArr.add(go);
        world.addBeyondarObject(go);
    }

    public void summonMons() {
        w = radiusInDegrees * Math.sqrt(random.nextDouble());
        Log.e("w", String.valueOf(w));

        t = 2 * Math.PI * random.nextDouble();
        Log.e("t", String.valueOf(t));

        double lat1 = w * Math.cos(t);
        double lat2 = w * Math.sin(t);

        double new_lat1 = lat1 / Math.cos(-8.691782);

        double foundLongitude = new_lat1 + 115.223726;

        double foundLatitude = lat2 + -8.691782;

        int al = random.nextInt(8 - 0 + 1);
        int stat = random.nextInt(1 - 0 + 1);
        //ganti ini kalo nambah monster
        int mon = random.nextInt(4 - 0 + 1);
        Log.e("AL", String.valueOf(al));
        Log.e("STAT", String.valueOf(stat));
        Log.e("foundLongitude", "foundLongitude" + foundLongitude + "foundLongitude" + foundLongitude + "al" + al + "stat" + stat + "mon" + mon);
        newObjAug(foundLatitude, foundLongitude, String.valueOf(al), stat, mon);
    }

    public void geoMenu() {


        //  Menu start kedua
        GeoObject str2 = new GeoObject(1000002);
        str2.setGeoPosition(-8.691824, 115.223724, 0.00005);
        str2.setImageResource(R.drawable.start);
        geoObjMenu.add(str2);
        world.addBeyondarObject(str2);


        //  Menu exit kedua
        GeoObject stp2 = new GeoObject(1000004);
        stp2.setGeoPosition(-8.691824, 115.223724, 0.000017);
        stp2.setImageResource(R.drawable.exit);
        geoObjMenu.add(stp2);
        world.addBeyondarObject(stp2);


        GeoObject stp3 = new GeoObject(1000005);
        stp3.setGeoPosition(-8.691828, 115.223725, 0.000035);
        stp3.setImageResource(R.drawable.highscore);
        geoObjMenu.add(stp3);
        world.addBeyondarObject(stp3);

        GeoObject stp4 = new GeoObject(1000006);
        stp4.setGeoPosition(-8.691828, 115.223726, 0.000035);
        stp4.setImageResource(R.drawable.highscore);
        geoObjMenu.add(stp4);
        world.addBeyondarObject(stp4);

        GeoObject str3 = new GeoObject(1000008);
        str3.setGeoPosition(-8.692000, 115.224080, 0.00008);
        str3.setImageResource(R.drawable.play);
        geoObjMenu.add(str3);
        world.addBeyondarObject(str3);
    }

    public void startGame() {
        mulai = 1;
        durasi = 45;
        count = 3;
        skorInt = 0;
    }

    public void stopGame() {
        cross.setVisibility(View.INVISIBLE);
        skorFinal.setText(String.valueOf(skorInt));
        skorFinalLayout.setVisibility(View.VISIBLE);
        skorText.setText("0");
        mulai = 0;
        isPauseCount = 0;
        btnPause.setVisibility(View.GONE);
        btnPlay.setVisibility(View.GONE);
    }


    public void pauseGame() {
        cross.setVisibility(View.INVISIBLE);
        skorFinal.setText(String.valueOf(skorInt));
        skorFinalLayout.setVisibility(View.INVISIBLE);
        Textdurasi.setText(isPauseCount + "");
        mulai = 0;
        bMenu.setVisibility(View.INVISIBLE);
    }


    public void backToMenu() {
        for (int i = 0; i < geoObjArr.size(); i++) {
            geoObjArr.get(i).setGeoPosition(0, 0);
        }
        geoObjMenu.get(0).setGeoPosition(-8.691824, 115.223724, 0.00002);
        geoObjMenu.get(1).setGeoPosition(-8.691824, 115.223724, 0.000005);
        geoObjMenu.get(2).setGeoPosition(-8.691828, 115.223725, 0.000038);
        geoObjMenu.get(3).setGeoPosition(-8.691828, 115.223725, 0.000038);
        bMenu.setVisibility(View.INVISIBLE);
        skorFinalLayout.setVisibility(View.INVISIBLE);
        cross.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
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


}
