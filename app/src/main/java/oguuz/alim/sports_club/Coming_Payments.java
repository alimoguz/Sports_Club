package oguuz.alim.sports_club;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import oguuz.alim.sports_club.Adapters.Coming_Adapter;
import oguuz.alim.sports_club.Adapters.StudentAdapter;
import oguuz.alim.sports_club.models.students;

public class Coming_Payments extends AppCompatActivity {
    private String mCurrentUserId;
    private DatabaseReference mRootRef;
    private final List<students> coming_model= new ArrayList<students>();
    private Coming_Adapter coming_adapter;
    private RecyclerView mComingList;
    private List<String> pushId= new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private Button mSend;
    private TextView no_found;
    private Handler handler;
    private AlertDialog alertDialog;
    private List<String> phone_toSendMessage=new ArrayList<>();
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming__payments);

        this.setTitle("COMING PAYMENTS");

        mSend=findViewById(R.id.coming_payments_send_message);
        no_found=findViewById(R.id.no_found);
        no_found.setVisibility(View.INVISIBLE);


        //Firebase Instances
        mCurrentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserId);

        //Check from firebase databse for students with less than 2 lessons
        check_for_two();

        //No record TextView Visibility
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(coming_adapter.mComingList.size()!=0){
                    no_found.setVisibility(View.INVISIBLE);
                }
                else{
                    no_found.setVisibility(View.VISIBLE);
                }
            }
        },5000);


        //Send Message Click
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(Coming_Payments.this);
                builder.setTitle("SURE?");
                builder.setMessage("MESSAGE WILL BE SENT TO PERSONS THAT HAS RED COLOR");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        add_phone_list();
                        handler= new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                send_payment_message();
                                for(int i=0;i<pushId.size();i++){
                                    mRootRef.child("Students").child(pushId.get(i)).child("send").setValue("1");
                                }
                                check_for_two();
                            }
                        },5000);



                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog=builder.create();
                alertDialog.show();

            }
        });



    }
    //check phone numbers that do not send messages
    private void add_phone_list() {
        for(int i=0; i<pushId.size();i++){
            mRootRef.child("Students").child(pushId.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int send= Integer.parseInt(dataSnapshot.child("send").getValue().toString());
                    if(send==0){
                        phone_toSendMessage.add(dataSnapshot.child("phone").getValue().toString());
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void send_payment_message() {
        for(int i=0; i<phone_toSendMessage.size();i++){

            String phone=phone_toSendMessage.get(i).toString();

            if(phone.startsWith("0"))
                phone="9"+phone;
            else
                phone="90"+phone;

            new Connection_for_payment(phone).execute();

        }
    }


    private void check_for_two() {
        pushId.clear();
        coming_model.clear();

        mRootRef.child("Students").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                students stdnt= dataSnapshot.getValue(students.class);
                int course_num= Integer.parseInt(stdnt.getNumber_course());
                if(course_num<=1){
                    pushId.add(dataSnapshot.getKey());
                    coming_model.add(stdnt);
                    coming_adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        coming_adapter= new Coming_Adapter (this,coming_model);
        mComingList=(RecyclerView) findViewById(R.id.coming_payments_list);
        mLinearLayout= new LinearLayoutManager(this);
        mComingList.setHasFixedSize(true);
        mComingList.setLayoutManager(mLinearLayout);
        mComingList.setAdapter(coming_adapter);
    }
}


class Connection_for_payment extends AsyncTask<Void, Void, Void> {

    String phone;

    public Connection_for_payment(String phone) {this.phone = phone;}

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL u = new URL("http://api.netgsm.com.tr/xmlbulkhttppost.asp");
            URLConnection uc = u.openConnection();
            HttpURLConnection connection = (HttpURLConnection) uc;
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            OutputStream out = connection.getOutputStream();
            OutputStreamWriter wout = new OutputStreamWriter(out, "UTF-8");

            wout.write("<?xml version='1.0' encoding='iso-8859-9'?>"+
                    " <mainbody>"+
                    " <header>"+
                    "<company>NETGSM</company>"+
                    "<usercode>5549206409</usercode>"+
                    " <password>TAR564</password>"+
                    " <startdate></startdate>"+
                    " <stopdate></stopdate>"+
                    " <type>1:n</type>"+
                    " <msgheader>iKiZLERSK D</msgheader>"+
                    " </header>"+
                    " <body>"+
                    " <msg><![CDATA[SAYIN VELIMIZ; KURSUMUZDA KAYITLI OLAN OGRENCIMIZIN ODEMESI GELMISTIR. BILGINIZE...]]></msg>"+
                    " <no>"+phone+"</no>"+
                    " </body>"+
                    " </mainbody>");
            wout.flush();
            out.close();
            InputStream in = connection.getInputStream();
            int c;
            while ((c = in.read()) != -1) System.out.write(c);
            System.out.println();
            in.close();
            out.close();
            connection.disconnect();

        }
        catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        }
        return null;
    }
}
