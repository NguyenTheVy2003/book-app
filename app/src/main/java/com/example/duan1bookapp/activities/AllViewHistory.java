package com.example.duan1bookapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.duan1bookapp.R;
import com.example.duan1bookapp.databinding.ActivityAllBooksBinding;
import com.example.duan1bookapp.databinding.ActivityAllViewHistoryBinding;

public class AllViewHistory extends AppCompatActivity {
    private ActivityAllViewHistoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllViewHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}