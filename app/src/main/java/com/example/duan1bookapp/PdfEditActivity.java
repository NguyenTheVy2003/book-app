package com.example.duan1bookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.duan1bookapp.databinding.ActivityPdfEditBinding;

public class PdfEditActivity extends AppCompatActivity {
  private ActivityPdfEditBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_edit);
    }
}