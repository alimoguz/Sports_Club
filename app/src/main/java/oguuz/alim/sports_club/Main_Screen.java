package oguuz.alim.sports_club;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Scanner;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Main_Screen extends AppCompatActivity {

    private DatabaseReference myRef;
    private ImageButton mAdd;
    private ImageButton mList;
    private ImageButton mAttandence;
    private ImageButton mComingPayment;

    private String club_name;

    private  static int SIGN_IN_REQUEST_CODE=1;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== R.id.menu_sign_out){
            Single<Boolean> single = ReactiveNetwork.checkInternetConnectivity();

            single
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override public void accept(@NonNull Boolean isConnectedToTheInternet) throws Exception {
                            if(isConnectedToTheInternet){
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(Main_Screen.this, "You have been signed out.", Toast.LENGTH_LONG).show();
                                sendToStart();

                            }
                            else{


                            }
                        }
                    });


        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);

        this.setTitle("SPORTS CLUB");
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__screen);

        mAdd= findViewById(R.id.add_student);
        mList=findViewById(R.id.list_student);
        mAttandence=findViewById(R.id.attandence);
        mComingPayment=findViewById(R.id.coming_payments);

        Single<Boolean> single = ReactiveNetwork.checkInternetConnectivity();

        single
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override public void accept(@NonNull Boolean isConnectedToTheInternet) throws Exception {
                        if(!isConnectedToTheInternet){
                            Toast.makeText(Main_Screen.this, "PLEASE CHECK THE CONNECTION", Toast.LENGTH_SHORT).show();
                            mAdd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(Main_Screen.this, "PLEASE CHECK THE CONNECTION", Toast.LENGTH_SHORT).show();
                                }
                            });

                            mList.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Toast.makeText(Main_Screen.this, "PLEASE CHECK THE CONNECTION", Toast.LENGTH_SHORT).show();

                                }
                            });

                            mAttandence.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(Main_Screen.this, "PLEASE CHECK THE CONNECTION", Toast.LENGTH_SHORT).show();
                                }
                            });

                            mComingPayment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(Main_Screen.this, "PLEASE CHECK THE CONNECTION", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                    }
                });

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Single<Boolean> single = ReactiveNetwork.checkInternetConnectivity();

                single
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override public void accept(@NonNull Boolean isConnectedToTheInternet) throws Exception {
                                if(isConnectedToTheInternet){
                                    Intent add_intent= new Intent(Main_Screen.this, Add_Student.class);
                                    startActivity(add_intent);
                                }
                                else{
                                    Toast.makeText(Main_Screen.this, "PLEASE CHECK THE CONNECTION", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });

        mList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Single<Boolean> single = ReactiveNetwork.checkInternetConnectivity();

                single
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override public void accept(@NonNull Boolean isConnectedToTheInternet) throws Exception {
                                if(isConnectedToTheInternet){
                                    Intent list_intent= new Intent(Main_Screen.this, List_Students.class);
                                    startActivity(list_intent);
                                }
                                else{
                                    Toast.makeText(Main_Screen.this, "PLEASE CHECK THE CONNECTION", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });



            }
        });

        mAttandence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Single<Boolean> single = ReactiveNetwork.checkInternetConnectivity();

                single
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override public void accept(@NonNull Boolean isConnectedToTheInternet) throws Exception {
                                if(isConnectedToTheInternet){
                                    Intent attandence_intent= new Intent(Main_Screen.this, Attandence.class);
                                    startActivity(attandence_intent);
                                }
                                else{
                                    Toast.makeText(Main_Screen.this, "PLEASE CHECK THE CONNECTION", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

        mComingPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Single<Boolean> single = ReactiveNetwork.checkInternetConnectivity();

                single
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override public void accept(@NonNull Boolean isConnectedToTheInternet) throws Exception {
                                if(isConnectedToTheInternet){
                                    Intent coming_intent= new Intent(Main_Screen.this, Coming_Payments.class);
                                    startActivity(coming_intent);
                                }
                                else{
                                    Toast.makeText(Main_Screen.this, "PLEASE CHECK THE CONNECTION", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });








    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser currentuser= FirebaseAuth.getInstance().getCurrentUser();

        if(currentuser==null){
            sendToStart();
        }
    }

    private void sendToStart() {

        Intent start_intent= new Intent(Main_Screen.this,StartActivity.class);
        startActivity(start_intent);
        finish();

    }
}


