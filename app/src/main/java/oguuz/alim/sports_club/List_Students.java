package oguuz.alim.sports_club;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import oguuz.alim.sports_club.Adapters.StudentAdapter;
import oguuz.alim.sports_club.models.students;

public class List_Students extends AppCompatActivity {

    private DatabaseReference mRootRef;
    private String mCurrentUserId;
    private final List<students> StudentModelClass= new ArrayList<students>();
    private RecyclerView mStudentList;
    private LinearLayoutManager mLinearLayout;
    private StudentAdapter studentAdapter;
    private TextView no_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__students);

        this.setTitle("ALL STUDENTS");

        no_record=findViewById(R.id.no_record);
        no_record.setVisibility(View.INVISIBLE);


        final ProgressDialog mProgres= new ProgressDialog(this);
        mProgres.setMessage("Loading...");
        mProgres.show();

        mCurrentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootRef= FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserId);
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Students")){
                    list_student();
                    mProgres.dismiss();


                }
                else{
                    no_record.setVisibility(View.VISIBLE);
                    mProgres.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    public void list_student() {

        mRootRef.child("Students").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                students stdnt= dataSnapshot.getValue(students.class);
                StudentModelClass.add(stdnt);
                studentAdapter.notifyDataSetChanged();

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

        studentAdapter= new StudentAdapter (this, StudentModelClass);
        mStudentList=(RecyclerView) findViewById(R.id.student_list);
        mLinearLayout= new LinearLayoutManager(this);
        mStudentList.setHasFixedSize(true);
        mStudentList.setLayoutManager(mLinearLayout);
        mStudentList.setAdapter(studentAdapter);


    }


}
