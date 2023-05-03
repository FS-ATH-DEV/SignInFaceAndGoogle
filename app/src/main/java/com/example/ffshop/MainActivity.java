package com.example.ffshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ImageView fbBtn;
    ImageView ggBtn;
    CallbackManager callbackManager;

    // cung cấp các tùy chọn để xác định phạm vi truy cập cũng như các thông tin tk Gg mà được yêu cầu từ người dùng
    GoogleSignInOptions gso;
    // thực hiện quá trình đăng nhập và xác thực gg trên ứng dụng Android, trả về thông tin tk của người dùng bao gồm
    // các quyền truy cập đã được xác định trong GoogleSignInOptions
    GoogleSignInClient gsc;
    boolean ffMode = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dangnhap);
        ggBtn = findViewById(R.id.ggBtn);
        fbBtn = findViewById(R.id.fbBtn);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        ggBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                ffMode = false;
            }
        });
        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Facebook Graph API là một API RESTful được cung cấp bởi Facebook,
                // cho phép các nhà phát triển truy cập và tương tác với các tài nguyên trên Facebook
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile","email"));
                ffMode = true;
            }
        });
        if(ffMode){
            callbackManager = CallbackManager.Factory.create(); //tạo callbackManager để xử lý phản hồi đăng nhập bằng cách gọi CallbackManager.Factory.create
            // một callback để xử lý phản hồi khi đăng nhập
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // Tạo Intent để chuyển sang SecondActivity
                            Intent intent = new Intent(MainActivity.this, TrangChu.class);
                            // Đính kèm dữ liệu vào Intent
                            intent.putExtra("ffMode", ffMode);
                            // Chuyển sang SecondActivity
                            startActivity(intent);

                            System.out.println(loginResult);
                            finish();
                        }
                        @Override
                        public void onCancel() {
                            // App code
                        }
                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                        }
                    });
        }
    }
    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(ffMode){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }else{
            if(requestCode == 1000){
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                try {
                    task.getResult(ApiException.class);
                    navigateToSecondActivity();
                } catch (ApiException e) {
                    Toast.makeText(getApplicationContext(), "Wrong something", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    void navigateToSecondActivity(){
        finish();
        Intent intent = new Intent(MainActivity.this, TrangChu.class);
        intent.putExtra("ffMode", ffMode);
        startActivity(intent);
    }
}