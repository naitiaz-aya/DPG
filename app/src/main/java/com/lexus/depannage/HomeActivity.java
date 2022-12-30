package com.lexus.depannage;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.lexus.depannage.Fragments.HomeFragment;
import com.lexus.depannage.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    private SessionManager sessionManager;
    private DrawerLayout drawer;
    private long back_pressed;
    ImageView menuBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork == null){
            Intent i = new Intent(HomeActivity.this, NoInternetActivity.class);
            startActivity(i);
            finish();
        }

        menuBtn = (ImageView) findViewById(R.id.menu_bar);
        //setSupportActionBar(binding.appBarHome.toolbar);
        sessionManager = new SessionManager(this);
        if(!sessionManager.isLogin()){
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }



        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
//        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_home){
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_home, new HomeFragment()).commit();
                    drawer.close();
                }
                if(item.getItemId() == R.id.nav_settings){
                    Toast.makeText(HomeActivity.this, "Edit User Profile", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(HomeActivity.this, EditUserProfileActivity.class);
                    startActivity(i);
                    finish();
                    drawer.close();

                }
                if(item.getItemId() == R.id.nav_switch){
                    Toast.makeText(HomeActivity.this, "Switch to driver", Toast.LENGTH_SHORT).show();
                    drawer.close();
                }
                if(item.getItemId() == R.id.nav_history){
                    Toast.makeText(HomeActivity.this, "History", Toast.LENGTH_SHORT).show();
                    drawer.close();

                }
                if(item.getItemId() == R.id.nav_signout){

                    sessionManager.editor.clear();
                    sessionManager.editor.commit();
                    Toast.makeText(HomeActivity.this, "Good Bye 👋", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                    drawer.close();

                }


                return true;
            }
        });


        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.open();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_switch :
                Toast.makeText(this, "Switch to driver", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_settings :
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_signout :
                sessionManager.editor.clear();
                sessionManager.editor.commit();
                Toast.makeText(HomeActivity.this, "Good Bye 👋", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;

            default:
                return  super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if(back_pressed +  100 > System.currentTimeMillis()){
            super.onBackPressed();
        }else{
            Toast.makeText(this, "Press once again to exit", Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}