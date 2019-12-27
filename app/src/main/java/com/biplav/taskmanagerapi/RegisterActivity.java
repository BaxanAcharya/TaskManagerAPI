package com.biplav.taskmanagerapi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.biplav.taskmanagerapi.Api.UserInterface;
import com.biplav.taskmanagerapi.Url.Url;
import com.biplav.taskmanagerapi.model.User;
import com.biplav.taskmanagerapi.serverReponse.ImageResponse;
import com.biplav.taskmanagerapi.strictmode.StrictModeClass;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    EditText etfirstName, etlastName, etusernameSign, etPasswordSign;
//            etConfirPW;
    ImageView imageView;
    Button btnSignup;
    String imagePath;
    String imageName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        imageView = findViewById(R.id.imageView);
        etfirstName = findViewById(R.id.etFirstName);
        etusernameSign = findViewById(R.id.etSignUserName);
        etPasswordSign = findViewById(R.id.etPasswordSign);
        etlastName = findViewById(R.id.etLastName);
//        etConfirPW = findViewById(R.id.etConfirmPW);
        btnSignup = findViewById(R.id.btnSignUp);

        checkPermission();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowseImage();

            }
        });
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0
            );
        }
    }

    private void BrowseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); //browse only image
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
        Uri uri = data.getData();
        imageView.setImageURI(uri);
        imagePath = getRealPathFromUri(uri);
        System.out.println(imagePath);

    }

    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null
        );
        Cursor cursor = loader.loadInBackground();
        int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(colIndex);
        cursor.close();
        return result;

    }

    private void saveImageOnly() {
        File file = new File(imagePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("imageFile",
                file.getName(), requestBody
        );

        UserInterface userInterface = Url.getInstance().create(UserInterface.class);

        Call<ImageResponse> responseCall = userInterface.uploadImage(body);
        StrictModeClass.StrctMode();

        try {
            Response<ImageResponse> imageResponseResponse = responseCall.execute();

            imageName = imageResponseResponse.body().getFilename();
            System.out.println(imageName);
            Toast.makeText(this, "Image uploaded " + imageName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void save() {
        String fname = etfirstName.getText().toString().trim();
        String lname = etlastName.getText().toString().trim();
        String username = etusernameSign.getText().toString().trim();
        String password = etPasswordSign.getText().toString().trim();
//        String cpassword = etConfirPW.getText().toString().trim();

//        if (password == cpassword) {
            saveImageOnly();
            User user = new User(fname, lname, username, password, imageName);
            UserInterface userApi = Url.getInstance().create(UserInterface.class);
            Call<Void> voidCall = userApi.registerUser(user);

            voidCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Log.d("error", response.message());
                    }
                    Toast.makeText(RegisterActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                    etfirstName.setText("");
                    etlastName.setText("");
                    etusernameSign.setText("");
                    etPasswordSign.setText("");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("error", t.getLocalizedMessage());
                }
            });

//        }
//        else {
//            Toast.makeText(this, "Password dont match", Toast.LENGTH_SHORT).show();
//        }

    }

}
