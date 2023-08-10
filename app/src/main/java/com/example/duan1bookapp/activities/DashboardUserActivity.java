package com.example.duan1bookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.duan1bookapp.R;
import com.example.duan1bookapp.adapters.ViewPagerAdapter;
import com.example.duan1bookapp.databinding.ActivityDashboardUserBinding;
import com.example.duan1bookapp.models.ModelCategory;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class DashboardUserActivity extends AppCompatActivity {

    //to show in tabs
    public ArrayList<ModelCategory> categoryArrayList;



    //view binding
    private ActivityDashboardUserBinding binding;

    //firebase auth
    private FirebaseAuth firebaseAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        setUpViewPager();


        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId=item.getItemId();
                switch (itemId){
                    case R.id.ic_home:
                        binding.viewpager.setCurrentItem(0);
                        break;
                    case R.id.ic_manga:
                        binding.viewpager.setCurrentItem(1);
                        break;
                    case R.id.ic_menubook:
                        binding.viewpager.setCurrentItem(2);
                        break;
                    case R.id.ic_user:
                        binding.viewpager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });

    }

    private void setUpViewPager(){
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewpager.setAdapter(viewPagerAdapter);

        categoryArrayList = new ArrayList<>();

        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        binding.bottomNavigation.getMenu().findItem(R.id.ic_home).setChecked(true);
                        break;
                    case 1:
                        binding.bottomNavigation.getMenu().findItem(R.id.ic_manga).setChecked(true);
                        break;
                    case 2:
                        binding.bottomNavigation.getMenu().findItem(R.id.ic_menubook).setChecked(true);
                        break;
                    case 3:
                        binding.bottomNavigation.getMenu().findItem(R.id.ic_user).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void checkUser() {
        //get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            //not logged in, go to main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }



}