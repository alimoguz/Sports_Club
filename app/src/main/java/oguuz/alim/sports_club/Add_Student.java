package oguuz.alim.sports_club;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Add_Student extends AppCompatActivity {

    private EditText mName,mSurname,mMother,mFather, mPhone, mAddress;
    private TextView mThisDate, mBirthday;
    private ImageButton mChangeDate, mChangeBirthday;
    private Button mSave;
    private String newDate;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabase;
    private ImageView mImage;
    private StorageReference mImageStorage;
    private Uri filePath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private  String downloadUrl;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__student);

        this.setTitle("NEW RECORD");


        FirebaseUser curr_user= FirebaseAuth.getInstance().getCurrentUser();
        String uid=curr_user.getUid();

        mImageStorage= FirebaseStorage.getInstance().getReference();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabase.keepSynced(true);

        mImage= findViewById(R.id.photo);
        mThisDate= findViewById(R.id.thisDate);
        mBirthday=findViewById(R.id.birthday);
        mName=findViewById(R.id.name);
        mSurname=findViewById(R.id.surname);
        mMother=findViewById(R.id.mother_name);
        mFather=findViewById(R.id.father_name);
        mPhone=findViewById(R.id.phone);
        mAddress=findViewById(R.id.address);
        mChangeBirthday=findViewById(R.id.change_birthday);
        mSave=findViewById(R.id.save);
        mChangeDate=findViewById(R.id.change_date);

        mProgressDialog = new ProgressDialog(this);


       mThisDate.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"/"+
                        (Calendar.getInstance().get(Calendar.MONTH)+1)+"/"+
                        Calendar.getInstance().get(Calendar.YEAR));

       mImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent camera= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               if (camera.resolveActivity(getPackageManager()) != null) {
                   startActivityForResult(camera, REQUEST_IMAGE_CAPTURE);
               }


           }
       });

       mChangeBirthday.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               final AlertDialog.Builder builder = new AlertDialog.Builder(Add_Student.this);
               final DatePicker picker = new DatePicker(Add_Student.this);
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

        mChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(Add_Student.this);
                final DatePicker picker = new DatePicker(Add_Student.this);
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

                if(!name.equals("") && !surname.equals("") &&!mothername.equals("")
                        &&!fathername.equals("") &&!phone.equals("") &&!address.equals(""))
                {


                    mProgressDialog.setMessage("Please wait while we save student record");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    
                    saveStudent(name, surname, mothername, fathername, phone, address,Reg_date,Birt);

                }

                else
                {
                    Toast.makeText(Add_Student.this, "Please Check Form and Try Again Later!!", Toast.LENGTH_SHORT).show();

                }



            }
        });








    }
    private void upload_image(final String mPushId){
        if(filePath != null){
            StorageReference ref = mImageStorage.child("images/"+ mPushId);
            ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        final String downloadUrl= task.getResult().getDownloadUrl().toString();
                        mDatabase.child("Students").child(mPushId).child("image").setValue(downloadUrl);

                    }
                    else{
                        Toast.makeText(Add_Student.this, "Photo can not uploaded...", Toast.LENGTH_LONG).show();

                    }

                }
            });
        }
    }

    private void saveStudent(final String name, final String surname, String mothername, String fathername, final String phone, String address,
                             String reg_date, String birthday) {



        HashMap<String,String> userMap= new HashMap<>();
        userMap.put("name", name);
        userMap.put("surname",surname);
        userMap.put("mothername", mothername);
        userMap.put("fathername", fathername);
        userMap.put("phone", phone);
        userMap.put("address",address);
        userMap.put("reg_date",reg_date);
        userMap.put("birth_date", birthday);
        userMap.put("number_course", String.valueOf(8));
        userMap.put("send", String.valueOf(0));
        userMap.put("last_payment", reg_date);
        userMap.put("image", "default");

        final String mPushId = mDatabase.push().getKey();

        mDatabase.child("Students").child(mPushId).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    mProgressDialog.dismiss();
                    upload_image(mPushId);
                    mImage.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
                    mName.setText("");
                    mSurname.setText("");
                    mMother.setText("");
                    mFather.setText("");
                    mPhone.setText("");
                    mAddress.setText("");
                    mThisDate.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"/"+
                            (Calendar.getInstance().get(Calendar.MONTH)+1)+"/"+
                            Calendar.getInstance().get(Calendar.YEAR));
                    mBirthday.setText("5/4/1995");
                    Toast.makeText(Add_Student.this, "Registration Successful...", Toast.LENGTH_SHORT).show();
                    sendWelcomeMessage(name,surname,phone);

                }
                else{
                    mProgressDialog.dismiss();
                    Toast.makeText(Add_Student.this, "Registration Unsuccessful... ", Toast.LENGTH_SHORT ).show();
                }

            }
        });




    }

    private void sendWelcomeMessage(String name,String surname, String phone) {
        if(phone.startsWith("0"))
            phone="9"+phone;
        else
            phone="90"+phone;
        String name_surname=name.toUpperCase()+" "+surname.toUpperCase();
        new Connection(phone,name_surname).execute();
    }


}

class Connection extends AsyncTask<Void, Void, Void> {
    String phone;
    String name_surname;
    public Connection(String phone,String name_surname) {
        this.phone=phone;
        this.name_surname=name_surname;
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
                    " <msg><![CDATA[SAYIN "+name_surname+";\n" +
                    " KAYIT ISLEMINIZ BASARI ILE GERCEKLESMISTIR, ASYA SPOR KULUBU AILEMIZE HOSGELDINIZ. SIZ DEGERLI UYEMIZIN BIZLERE IHTIYACI HALINDE \n" +
                    "www.asyabuzsporlari.com INTERNET ADRESIMIZDEN BIZE ULASABILIRSINIZ. SAGLIKLI GUNLER DILERIZ.]]></msg>"+
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

class Utils {

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}




