package com.example.androidapp;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.Fragment.FriendsFragment;
import com.example.Fragment.MeFragment;
import com.example.Fragment.MessageFragment;
import com.example.Fragment.NotificationFragment;
import com.example.activity.FindAndAddFriendActivity;
import com.example.helper.BottomNavigationViewHelper;
import com.example.listener.EndListenerThread;
import com.example.listener.InitListener;
import com.jaeger.library.StatusBarUtil;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private TextView mainTitle;
    private DrawerLayout drawerLayout;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        //底部导航栏选中事件
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_nav_message:
                    replaceFragment(new MessageFragment());
                    mainTitle.setText(item.getTitle());
                    return true;
                case R.id.bottom_nav_friends:
                    replaceFragment(new FriendsFragment());
                    mainTitle.setText(item.getTitle());
                    return true;
                case R.id.bottom_nav_notification:
                    replaceFragment(new NotificationFragment());
                    mainTitle.setText(item.getTitle());
                    return true;
                case R.id.bottom_nav_me:
                    replaceFragment(new MeFragment());
                    mainTitle.setText(item.getTitle());
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivityStart", "onCreate");
        new InitListener().InitListener();            //启动所有需要开启的监听

        StatusBarUtil.setColor(MainActivity.this, getResources().getColor(R.color.smallBlue));
        replaceFragment(new MessageFragment());
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        //设置图标下面文字选中和没选中的颜色
        navigation.setItemTextAppearanceActive(R.style.bottom_selected_text);
        navigation.setItemTextAppearanceInactive(R.style.bottom_normal_text);

        //设置图标选中的颜色
        ColorStateList csl=(ColorStateList)getResources().getColorStateList(R.color.navigation_menu_item_color);
        navigation.setItemIconTintList(csl);


        //设置底部导航栏监听事件

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mainTitle = (TextView) findViewById(R.id.mainTitle);
        navigation.setSelectedItemId(R.id.bottom_nav_message);
        mainTitle.setText(R.string.bottom_nav_message);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.user_image);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        //设置默认选中item
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();        //关闭滑动菜单
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //点击home图标，打开滑动菜单
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.add_friends_item:
                Intent intent = new Intent(this, FindAndAddFriendActivity.class);
                startActivity(intent);

            default:
                break;
        }
        return true;
    }

    //添加menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }


    //显示图标和文字
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment, fragment);
        transaction.commit();
    }

    //当活动销毁时，关闭子线程

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //结束所有该结束的子线程
        new EndListenerThread().endListenerThread();
    }
}
