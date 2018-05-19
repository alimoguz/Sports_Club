package oguuz.alim.sports_club;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;
    private Button mLogin_btn;
    private ProgressDialog mLoginProgressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mLoginEmail=(TextInputLayout) findViewById(R.id.email_layout);
        mLoginPassword=(TextInputLayout) findViewById(R.id.password_layout);
        mLogin_btn= (Button) findViewById(R.id.email_sign_in_button);
        mLoginProgressDialog = new ProgressDialog(this);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= mLoginEmail.getEditText().getText().toString();
                String password= mLoginPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password)){

                    mLoginProgressDialog.setTitle("Logging In");
                    mLoginProgressDialog.setMessage("Please wait while we check your accounts.");
                    mLoginProgressDialog.setCanceledOnTouchOutside(false);
                    mLoginProgressDialog.show();

                    loginUser(email,password);
                }

            }
        });

    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    mLoginProgressDialog.dismiss();

                    String current_user_id=mAuth.getCurrentUser().getUid();
                    String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                    mUserDatabase.child(current_user_id).child("device_token").setValue(DeviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent mainIntent= new Intent(LoginActivity.this, Main_Screen.class );
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }
                    });

                }

                else{
                    mLoginProgressDialog.hide();
                    Toast.makeText(LoginActivity.this,"Cannot Sign in. Please check the form and try again later.",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
