package com.pdr.veigris;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

public class TakeoutUploadActivity extends AppCompatActivity {
    private TripDao tripDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tripDao = new DbHelper(getApplicationContext()).GetTripDao();

        setContentView(R.layout.activity_takeout_upload);

        Button button = (Button) findViewById(R.id.button_upload_takeout);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/zip");

                fileSelectorActivityResultLauncher.launch(intent);
            }
        });
    }

    ActivityResultLauncher<Intent> fileSelectorActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri uri = data.getData();
                    try {
                        File cacheDir = getCacheDir();
                        ZipManager.Unzip(getContentResolver().openInputStream(uri), cacheDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "arne", Toast.LENGTH_SHORT).show();
                }
            });
}