package oguuz.alim.sports_club;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import oguuz.alim.sports_club.Adapters.AttandenceAdapter;
import oguuz.alim.sports_club.Adapters.StudentAdapter;
import oguuz.alim.sports_club.models.attandence_model;
import oguuz.alim.sports_club.models.students;

public class Attandence extends AppCompatActivity {

    private DatabaseReference mRootRef;
    private String mCurrentUserId;
    private final List<attandence_model> AttandenceModelClass= new ArrayList<attandence_model>();
    private RecyclerView mStudentList;
    private LinearLayoutManager mLinearLayout;
    private AttandenceAdapter attandenceAdapter;
    private Button save;
    private static List<String> pushId= new ArrayList<>();
    private List<Integer > att= new ArrayList<>();
    private AlertDialog alertDialog;
    private String thisTime;
    private Button change_date;
    private TextView date;
    private String newDate;
    private boolean exist;
    private Handler handler;
    private String first_num;
    private String last_num;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attandence);

        this.setTitle("ATTENDANCE");

        //Firebase Instances
        mCurrentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootRef=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserId);

        //Activity Instances
        save=findViewById(R.id.Attandence_Save);
        change_date=findViewById(R.id.change);
        date=findViewById(R.id.attandence_date);

        date.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"-"+
                (Calendar.getInstance().get(Calendar.MONTH)+1)+"-"+
                Calendar.getInstance().get(Calendar.YEAR));

        newDate=date.getText().toString();

        //First Loading
        load_from_firebase();


        //Clicks of Activity Buttons
        change_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Attandence.this);
                final DatePicker picker = new DatePicker(Attandence.this);
                picker.setCalendarViewShown(false);

                builder.setTitle("DATE FOR ATTENDANCE");
                builder.setView(picker);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Set", null);
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        newDate=picker.getDayOfMonth()+"-"+(picker.getMonth()+1)+"-"+picker.getYear();
                        date.setText(newDate);
                        load_from_firebase();
                    }
                });
                builder.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(Attandence.this);
                builder.setTitle("SURE?");
                builder.setMessage("ATTANDENCE FOR "+newDate+" WILL BE RECORD");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        add_coming_days(pushId, att);


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
    // UPDATE LEFT LESSON FROM DATEBASE FOR EACH STUDENT
    private void change_left_count_lesson(final List<String> pushId, List<Integer> att){

        for( int i=0;i<pushId.size();i++){
            if(att.get(i)==1){
                final int finalI = i;
                mRootRef.child("Students").child(pushId.get(i)).child("number_course").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        first_num=dataSnapshot.getValue().toString();
                        int a= Integer.parseInt(first_num);
                        int b=a-1;
                        last_num= String.valueOf(b);
                        mRootRef.child("Students").child(pushId.get(finalI)).child("number_course").setValue(last_num);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }



    }

    // CREATE ATTENDANCE LIST ACCORDING TO DATE
    private void add_coming_days(final List<String> pushId, final List<Integer> att){

        check_attendance_from_firebase();// IF DATE EXIST,THERE MUST BE ONLY 1 ATTANDENCE FOR A DATE
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(exist==true){
                    Toast.makeText(Attandence.this,"ATTENDANCE ALREADY EXISTS FOR DATE: "+newDate , Toast.LENGTH_SHORT).show();
                }
                else{
                    change_left_count_lesson(pushId,att);
                    for(int i=0;i<Attandence.pushId.size();i++){
                        mRootRef.child("Coming_Days").child(newDate).child(pushId.get(i)).setValue(att.get(i));

                    }
                    Toast.makeText(Attandence.this,"TRANSACTION SUCCESSFUL", Toast.LENGTH_SHORT).show();
                }

            }
        },1000);




        //Toast.makeText(Attandence.this,exist+"", Toast.LENGTH_SHORT).show();

    }

    private void check_attendance_from_firebase() {

        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Coming_Days")){
                    if(dataSnapshot.child("Coming_Days").hasChild(newDate)){
                        exist=true;
                    }
                    else{
                        exist=false;
                    }

                }
                else{
                    exist=false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // LOAD DATA FROM DATABASE FOR ATTENDANCE ACTIVITY
    private void load_from_firebase() {

        att.clear();
        pushId.clear();
        AttandenceModelClass.clear();

        mRootRef.child("Students").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                pushId.add(dataSnapshot.getKey());
                att=attandenceAdapter.att;
                attandence_model at_model=  dataSnapshot.getValue(attandence_model.class);
                AttandenceModelClass.add(at_model);
                attandenceAdapter.notifyDataSetChanged();

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

        attandenceAdapter= new AttandenceAdapter(AttandenceModelClass);
        mStudentList=(RecyclerView) findViewById(R.id.attandence_list);
        mLinearLayout= new LinearLayoutManager(this);
        mStudentList.setHasFixedSize(true);
        mStudentList.setLayoutManager(mLinearLayout);
        mStudentList.setAdapter(attandenceAdapter);
    }
}
