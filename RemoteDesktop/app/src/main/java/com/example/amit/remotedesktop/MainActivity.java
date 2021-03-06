package com.example.amit.remotedesktop;

import android.app.Presentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Socket clientSocket = null;
    public static ObjectInputStream objectInputStream = null;
    public static ObjectOutputStream objectOutputStream = null;
    static int visible = 0;

    Button con;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTitle(getResources().getString(R.string.connect));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        con = findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.textView1);


        SharedPreferences prefs = getSharedPreferences("ip", MODE_PRIVATE);
        String ip = prefs.getString("ip", null);
        if (ip != null) {
            String IP = prefs.getString("ip", "");
            editText.setText(IP);
        }
        if(visible == 1){
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            drawer.openDrawer(GravityCompat.START);
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public void onClick(View view) {
        EditText editText = (EditText)findViewById(R.id.textView1);
        EditText editText1 = (EditText)findViewById(R.id.textView2);
        String ip = "", port = "";
        ip = editText.getText().toString();
        port = editText1.getText().toString();
        new Connect(ip, port, this) {
            @Override
            public void receiveData(Object result) {
                clientSocket = (Socket)result;
            }
        }.execute();

     /*   if(clientSocket != null)
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
*/
        SharedPreferences.Editor editor = getSharedPreferences("ip", MODE_PRIVATE).edit();
        editor.putString("ip", ip);
        editor.apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            Intent intenti = new Intent(this, Info.class);
            startActivity(intenti);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_connect) {
            // Handle the camera action
        } else if (id == R.id.nav_mouse) {
            Intent intentm = new Intent(this, Mouse.class);
            visible = 1;
            startActivity(intentm);

        } else if (id == R.id.nav_keyboard) {
            Intent intentkey = new Intent(this, Keyboard.class);
            visible = 1;
            startActivity(intentkey);

        } else if (id == R.id.nav_presentation) {
            Intent intentp = new Intent(this, Ppt.class);
            visible = 1;
            startActivity(intentp);
        }
        else if (id == R.id.nav_apps) {
            Intent intenta = new Intent(this, Applications.class);
            visible = 1;
            startActivity(intenta);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void socketclose() {
        if (MainActivity.clientSocket != null) {
            try {
                MainActivity.clientSocket.close();
                MainActivity.objectOutputStream.close();
                MainActivity.clientSocket = null;
            } catch(Exception e2) {
                e2.printStackTrace();
            }
        }
    }


    public static void msg(String message) {
        if (MainActivity.clientSocket != null) {
            try {
                MainActivity.objectOutputStream.writeObject(message);
                MainActivity.objectOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
                socketclose();
            }
        }
    }
    public static void msg(int message) {
        if (MainActivity.clientSocket != null) {
            try {
                MainActivity.objectOutputStream.writeObject(message);
                MainActivity.objectOutputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
                socketclose();
            }
        }
    }
}
