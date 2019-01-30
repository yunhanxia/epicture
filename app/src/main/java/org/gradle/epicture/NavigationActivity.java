package org.gradle.epicture;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

public class NavigationActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    Fragment fragment1 = new HomeActivity();
    Fragment fragment2 = new AccountActivity();
    Fragment fragment3 = new FavoriteActivity();
    Fragment fragment4 = new UploadActivity();
    Fragment fragment5 = new SearchActivity();
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = fragment1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment5, "5")
                .hide(fragment5)
                .addToBackStack(null)
                .commit();
        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment4, "4")
                .hide(fragment4)
                .addToBackStack(null)
                .commit();
        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment3, "3")
                .hide(fragment3)
                .addToBackStack(null)
                .commit();
        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment2, "2")
                .hide(fragment2)
                .addToBackStack(null)
                .commit();
        fragmentManager
                .beginTransaction()
                .add(R.id.fragment_container,fragment1, "1")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings) {
            clearCookie();
            Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId() == R.id.help) {
            Toast.makeText(this,"App created by Lothaire NOAH and Yuu XIA", Toast.LENGTH_SHORT).show();
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private boolean clearCookie() {
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                fragmentManager.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                return true;
            case R.id.navigation_account:
                fragmentManager.beginTransaction().hide(active).show(fragment2).commit();
                active = fragment2;
                return true;
            case R.id.navigation_favorite:
                fragmentManager.beginTransaction().hide(active).show(fragment3).commit();
                active = fragment3;
                return true;
            case R.id.navigation_upload:
                fragmentManager.beginTransaction().hide(active).show(fragment4).commit();
                active = fragment4;
                return true;
            case R.id.navigation_search:
                fragmentManager.beginTransaction().hide(active).show(fragment5).commit();
                active = fragment5;
                return true;
        }
        return false;
    }
}
