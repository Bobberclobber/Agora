package se.liu.ida.josfa969.tddd80.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import se.liu.ida.josfa969.tddd80.R;
import se.liu.ida.josfa969.tddd80.fragments.InitialFragment;
import se.liu.ida.josfa969.tddd80.help_classes.Constants;

public class InitialActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new InitialFragment()).commit();
        }
    }

    public void onRegisterClick(View view) {
        // Starts the register user activity
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void onLoginClick(View view) {
        // Creates an intent to switch activity
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void onPreviewClick(View view) {
    }
}
