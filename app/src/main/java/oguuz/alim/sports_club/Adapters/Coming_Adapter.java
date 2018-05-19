package oguuz.alim.sports_club.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import oguuz.alim.sports_club.Coming_Payments;
import oguuz.alim.sports_club.List_Students;
import oguuz.alim.sports_club.R;
import oguuz.alim.sports_club.models.students;

/**
 * Created by Alim on 28.4.2018.
 */

public class Coming_Adapter extends RecyclerView.Adapter<Coming_Adapter.ComingViewHolder> {

    public List<students> mComingList;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;
    private ArrayList<String> pushId= new ArrayList<>();
    private Context context;



    public Coming_Adapter(Context context, List<students> coming_model) {
        mComingList=coming_model;
        this.context=context;

    }

    @Override
    public Coming_Adapter.ComingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.coming_single_layout, parent, false);
        return new ComingViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Coming_Adapter.ComingViewHolder holder, final int position) {
        pushId.clear();


        mDatabaseRef= FirebaseDatabase.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();


        students c= mComingList.get(position);
        int mSend= Integer.parseInt(c.getSend());
        if (mSend == 0) {
            holder.name_surname.setText(c.getName()+" "+ c.getSurname());
            holder.number_course.setText(c.getNumber_course());
            holder.mLinear.setBackgroundResource(R.color.colorAttandence2);

        }
        else{
            holder.name_surname.setText(c.getName()+" "+ c.getSurname());
            holder.number_course.setText(c.getNumber_course());
            holder.mLinear.setBackgroundResource(R.color.colorAttandence);

        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                pushId.clear();
                mDatabaseRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Students").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        int n= Integer.parseInt(dataSnapshot.child("number_course").getValue().toString());
                        if(n<=1)
                            pushId.add(dataSnapshot.getKey());
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

                CharSequence options[]= new CharSequence[]{"MARK AS PAID"};
                AlertDialog.Builder builder= new AlertDialog.Builder(context);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i== 0 )
                        {
                            mDatabaseRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Students").child(pushId.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int num= Integer.parseInt(dataSnapshot.child("number_course").getValue().toString()) ;
                                    num=num+8;
                                    String mThisDate=(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"/"+
                                            (Calendar.getInstance().get(Calendar.MONTH)+1)+"/"+
                                            Calendar.getInstance().get(Calendar.YEAR));
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Students").child(pushId.get(position)).child("number_course").setValue(""+num);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Students").child(pushId.get(position)).child("send").setValue(""+0);
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Students").child(pushId.get(position)).child("last_payment").setValue(mThisDate);
                                    Intent intent = new Intent(context, Coming_Payments.class);
                                    context.startActivity(intent);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                        }

                    }
                });
                builder.show();

                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mComingList.size();
    }




    public class ComingViewHolder extends RecyclerView.ViewHolder {

        public TextView name_surname;
        public TextView number_course;
        public LinearLayout mLinear;

        public ComingViewHolder(View itemView) {
            super(itemView);

            name_surname= itemView.findViewById(R.id.name_surname_coming_single);
            number_course=itemView.findViewById(R.id.num_course_coming_single);
            mLinear=itemView.findViewById(R.id.single_coming);
        }
    }
}

