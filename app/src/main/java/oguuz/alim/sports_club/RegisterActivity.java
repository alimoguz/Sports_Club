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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateButton;
    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);



        mDisplayName =(TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail=(TextInputLayout)findViewById(R.id.reg_email);
        mPassword=(TextInputLayout)findViewById(R.id.reg_password);
        mCreateButton=(Button) findViewById(R.id.create_account);
        mAuth=FirebaseAuth.getInstance();

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String display_name= mDisplayName.getEditText().getText().toString().trim();
                String email= mEmail.getEditText().getText().toString().trim();
                String password= mPassword.getEditText().getText().toString().trim();

                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) ||!TextUtils.isEmpty(password)){

                    mProgressDialog.setTitle("Registering User");
                    mProgressDialog.setMessage("Please wait while we create your account");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    register_user(display_name,email,password);
                }

                else{
                    Toast.makeText(RegisterActivity.this,"error" +
                            "",Toast.LENGTH_LONG).show();
                }



            }
        });
    }


    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser curr_user= FirebaseAuth.getInstance().getCurrentUser();
                    String uid=curr_user.getUid();

                    mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    String DeviceToken = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String,String> userMap= new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("device_token", DeviceToken);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mProgressDialog.dismiss();
                                Intent mainIntent= new Intent(RegisterActivity.this, Main_Screen.class );
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }

                            else{

                                //Toast.makeText(RegisterActivity.this,"Sorun var",Toast.LENGTH_LONG).show();

                            }
                        }
                    });



                    /**/


                }

                else{
                    FirebaseAuthException e = (FirebaseAuthException )task.getException();
                    mProgressDialog.hide();
                    Toast.makeText(RegisterActivity.this,e.getErrorCode(),Toast.LENGTH_LONG).show();

                }

            }
        });



    }
}
