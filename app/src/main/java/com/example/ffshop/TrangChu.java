package com.example.ffshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class TrangChu extends AppCompatActivity {
    ImageView imageView;
    TextView name, email;
    Button logOutbtn;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_chu);
        imageView = findViewById(R.id.imageview);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        logOutbtn = findViewById(R.id.logout);
        boolean ffMode = getIntent().getBooleanExtra("ffMode", false);
        System.out.println(ffMode);
        if(ffMode){
            handleFacebookSignIn();
        }else{
            handleGoogleSignIn();
        }
    }

    private void handleFacebookSignIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            System.out.println(object);
                            String fullName = object.getString("name");
                            String emailUser = object.getString("email");
                            String url = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            name.setText(fullName);
                            email.setText(emailUser);
                            Picasso.get().load(url).into(imageView);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        // Application code

                    }
                });
        // tạo một yêu cầu lấy dữ liệu từ Graph API của Facebook.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,picture.type(large),birthday, email");
        request.setParameters(parameters); //  setParameters() được sử dụng để thiết lập các thông số yêu cầu trong yêu cầu GraphRequest
        request.executeAsync(); // để gửi yêu cầu lên máy chủ Facebook và lấy về kết quả thông tin người dùng. Kết quả được trả về thông qua đối số GraphResponse trong phương thức onCompleted() của GraphJSONObjectCallback.
        logOutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                startActivity(new Intent(TrangChu.this,MainActivity.class));
                finish();
            }
        });
    }

    private void handleGoogleSignIn() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct != null) {
            // Nếu được xác thực lấy các dữ liệu permission được cung cấp ở GoogleSignInAccout yêu cầu email và accout.
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            name.setText(personName);
            email.setText(personEmail);
        }
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        logOutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        startActivity(new Intent(TrangChu.this,MainActivity.class));
                    }
                });
                startActivity(new Intent(TrangChu.this,MainActivity.class));
                finish();
            }
        });
    }
}