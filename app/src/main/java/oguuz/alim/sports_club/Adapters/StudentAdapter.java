package oguuz.alim.sports_club.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import oguuz.alim.sports_club.Add_Student;
import oguuz.alim.sports_club.Attandence;
import oguuz.alim.sports_club.List_Students;
import oguuz.alim.sports_club.R;
import oguuz.alim.sports_club.models.students;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Alim on 22.4.2018.
 */

 public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

     public List<students> mStudentList;
     private FirebaseAuth mAuth ;
     public DatabaseReference mDatabaseRef;
     private ArrayList<String> pushId= new ArrayList<>();
    private Context context;
    private String newDate;
    ProgressDialog mProgressDialog;
    private String last_payment;
    private Dialog dialog;
    private String image;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static CircleImageView mPhoto;
    public static String push;
    private AlertDialog alertDialog;


    public StudentAdapter(Context context,List<students> studentModelClass) {
         this.context=context;
         this.mStudentList=studentModelClass;
    }




    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.student_single_layout, parent, false);
        return new StudentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final StudentViewHolder holder, final int position) {
        mDatabaseRef=FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        final students c= mStudentList.get(position);
        if(!c.getImage().equals("default")){
            Picasso.with(context).load(c.getImage()).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(holder.image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(c.getImage()).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(holder.image);

                }
            });
        }
        holder.name.setText(c.getName());
        holder.surname.setText(c.getSurname());
        holder.birthday.setText(c.getBirth_date());
        holder.mother.setText(c.getMothername());
        holder.father.setText(c.getFathername());
        holder.phone.setText(c.getPhone());
        holder.address.setText(c.getAddress());
        holder.reg_date.setText(c.getReg_date());
        holder.number_course.setText(c.getNumber_course());


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mDatabaseRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Students").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
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


                CharSequence options[]= new CharSequence[]{"UPDATE","DELETE"};

                AlertDialog.Builder builder= new AlertDialog.Builder(context);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i== 0 )
                        {
                            dialog= new Dialog(context);
                            dialog.setContentView(R.layout.custom_dialog);
                            dialog.show();
                            //CUSTOM DIALOG VARIABLES
                            ImageButton change_date= dialog.findViewById(R.id.change_date);
                            final TextView mThisDate= dialog.findViewById(R.id.thisDate);
                            final TextView mBirthday=dialog.findViewById(R.id.birthday);
                            final EditText mName=dialog.findViewById(R.id.name);
                            final EditText mSurname=dialog.findViewById(R.id.surname);
                            final EditText mMother=dialog.findViewById(R.id.mother_name);
                            final EditText mFather=dialog.findViewById(R.id.father_name);
                            final EditText mPhone=dialog.findViewById(R.id.phone);
                            final EditText mAddress=dialog.findViewById(R.id.address);
                            mPhoto= dialog.findViewById(R.id.photo);
                            ImageButton mChangeBirthday=dialog.findViewById(R.id.change_birthday);
                            Button  mSave=dialog.findViewById(R.id.save);
                            //END OF CUSTOM DIALOG VARIABLES
                            mDatabaseRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Students").child(pushId.get(position)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild("last_payment")){
                                        last_payment=dataSnapshot.child("last_payment").getValue().toString();
                                        image= dataSnapshot.child("image").getValue().toString();

                                    }
                                    else
                                        last_payment=c.getReg_date();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            //DIALOG INITIALIZE WITH FIREBASE VARIABLES
                            mThisDate.setText(c.getReg_date());
                            mBirthday.setText(c.getBirth_date());
                            mName.setText(c.getName());
                            mSurname.setText(c.getSurname());
                            mMother.setText(c.getMothername());
                            mFather.setText(c.getFathername());
                            mPhone.setText(c.getPhone());
                            mAddress.setText(c.getAddress());
                            if(!c.getImage().equals("default")){
                                Picasso.with(context).load(c.getImage()).networkPolicy(NetworkPolicy.OFFLINE)
                                        .placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(mPhoto, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(context).load(c.getImage()).placeholder(R.drawable.com_facebook_profile_picture_blank_portrait).into(mPhoto);

                                    }
                                });
                            }
                            //END OF DIALOG INITIALIZE WITH FIREBASE VARIABLES

                            mSave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String name=mName.getText().toString().trim();
                                    String surname=mSurname.getText().toString().trim();
                                    String mothername=mMother.getText().toString().trim();
                                    String fathername=mFather.getText().toString().trim();
                                    String phone=mPhone.getText().toString().trim();
                                    String address=mAddress.getText().toString().trim();
                                    String Reg_date=mThisDate.getText().toString().trim();
                                    String Birt= mBirthday.getText().toString().trim();

                                    if(!name.equals("") && !surname.equals("") &&!mothername.equals("") &&!fathername.equals("") &&!phone.equals("") &&!address.equals(""))
                                    {
                                        mProgressDialog= new ProgressDialog(context);
                                        mProgressDialog.setMessage("Please wait while we update student record");
                                        mProgressDialog.setCanceledOnTouchOutside(false);
                                        mProgressDialog.show();
                                        updateStudent(pushId.get(position),name, surname, mothername, fathername, phone, address,Reg_date,Birt);
                                    }
                                }
                            });


                            change_date.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    final DatePicker picker = new DatePicker(context);
                                    picker.setCalendarViewShown(false);

                                    builder.setTitle("DATE OF REGISTRATION");
                                    builder.setView(picker);
                                    builder.setNegativeButton("Cancel", null);
                                    builder.setPositiveButton("Set", null);
                                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            newDate=picker.getDayOfMonth()+"/"+(picker.getMonth()+1)+"/"+picker.getYear();
                                            mThisDate.setText(newDate);
                                        }
                                    });
                                    builder.show();
                                }
                            });

                            mChangeBirthday.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    final DatePicker picker = new DatePicker(context);
                                    picker.setCalendarViewShown(false);

                                    builder.setTitle("DATE OF BIRTH");
                                    builder.setView(picker);
                                    builder.setNegativeButton("Cancel", null);
                                    builder.setPositiveButton("Set", null);
                                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            newDate=picker.getDayOfMonth()+"/"+(picker.getMonth()+1)+"/"+picker.getYear();
                                            mBirthday.setText(newDate);
                                        }
                                    });
                                    builder.show();

                                }
                            });

                        }

                        else
                        {
                            delete_student(pushId.get(position));

                        }

                    }
                });

                builder.show();

                return false;
            }
        });







    }

    private void delete_student(final String pushId) {
        AlertDialog.Builder builder= new AlertDialog.Builder(context);
        builder.setTitle("SURE?");
        builder.setMessage("RECORD WILL DELETE");
        builder.setIcon(R.drawable.delete);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabaseRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Students").child(pushId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                                    Toast.makeText(context, " DELETE SUCCESSFULL...", Toast.LENGTH_SHORT).show();
                                    Intent intent= new Intent(context, List_Students.class);
                                    context.startActivity(intent);
                        }
                        else{
                            Toast.makeText(context, " DELETE UNSUCCESSFULL...", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
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

    private void updateStudent(String pushId, final String name, final String surname, String mothername, String fathername, final String phone, String address, String reg_date, String birt)
    {
        HashMap<String,String> userMap= new HashMap<>();
        userMap.put("name", name);
        userMap.put("surname",surname);
        userMap.put("mothername", mothername);
        userMap.put("fathername", fathername);
        userMap.put("phone", phone);
        userMap.put("address",address);
        userMap.put("reg_date",reg_date);
        userMap.put("birth_date", birt);
        userMap.put("number_course", String.valueOf(8));
        userMap.put("send", String.valueOf(0));
        userMap.put("last_payment", last_payment);
        userMap.put("image", image);

        mDatabaseRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Students").child(pushId).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mProgressDialog.dismiss();
                    Intent intent = new Intent(context, List_Students.class);
                    context.startActivity(intent);
                    sendUpdateMessage(phone);

                }
                else{
                    mProgressDialog.dismiss();
                    Toast.makeText(context, "Update Unsuccessful... ", Toast.LENGTH_SHORT ).show();
                }

            }
        });
    }

    private void sendUpdateMessage(String phone) {
        if(phone.startsWith("0"))
            phone="9"+phone;
        else
            phone="90"+phone;
        new Connection2(phone).execute();
    }

    private boolean click(boolean b) {
        return b;
    }




    @Override
    public int getItemCount() {
        return mStudentList.size();
    }


    public class StudentViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView surname;
        public TextView birthday;
        public TextView mother;
        public TextView father;
        public TextView phone;
        public TextView address;
        public TextView reg_date;
        public TextView number_course;
        public ImageView image;


        public StudentViewHolder(View itemView) {
            super(itemView);

             image= itemView.findViewById(R.id.photo);
            name= itemView.findViewById(R.id.single_name);
            surname= itemView.findViewById(R.id.single_surname);
            birthday= itemView.findViewById(R.id.single_birth);
            mother= itemView.findViewById(R.id.single_mother);
            father= itemView.findViewById(R.id.single_father);
            phone= itemView.findViewById(R.id.single_phone);
            address= itemView.findViewById(R.id.single_address);
            reg_date= itemView.findViewById(R.id.single_registration);
            number_course=itemView.findViewById(R.id.single_left);

        }
    }


    public class Connection2 extends AsyncTask<Void, Void, Void> {
        String phone;
        String name_surname;
        public Connection2(String phone) {
            this.phone=phone;
        }


        protected Void doInBackground(Void... Arg0){
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
                        " <msg><![CDATA[SAYIN UYEMIZ BILGILERINIZ GUNCELLENMISTIR.]]></msg>"+
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
}


