package com.example.duan1bookapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.duan1bookapp.fragment.Fragment_Home;
import com.example.duan1bookapp.fragment.Fragment_Manga;
import com.example.duan1bookapp.fragment.Fragment_ReadingBook;
import com.example.duan1bookapp.fragment.Fragment_User;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Fragment_Home();
            case 1:
                return new Fragment_Manga();
            case 2:
                return new Fragment_ReadingBook();
            case 3:
                return new Fragment_User();
            default:
                return new Fragment_Home();

        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
