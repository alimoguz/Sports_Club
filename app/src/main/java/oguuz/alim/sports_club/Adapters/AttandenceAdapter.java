package oguuz.alim.sports_club.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import oguuz.alim.sports_club.Attandence;
import oguuz.alim.sports_club.R;
import oguuz.alim.sports_club.models.attandence_model;
import oguuz.alim.sports_club.models.students;

/**
 * Created by Alim on 22.4.2018.
 */

public class AttandenceAdapter extends RecyclerView.Adapter<AttandenceAdapter.AttandenceViewHolder> {

    private List<attandence_model> mAttandenceList;
    private int click_button=0;
    public List<Integer> att= new ArrayList<>();

    public AttandenceAdapter(List<attandence_model> attandenceModelClass) {

        this.mAttandenceList=attandenceModelClass;
    }



    @Override
    public AttandenceAdapter.AttandenceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.attandence_single_layout, parent, false);
        return new AttandenceAdapter.AttandenceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AttandenceViewHolder holder, final int position) {

        attandence_model c= mAttandenceList.get(position);
        String name_surname= c.getName()+" "+c.getSurname();
        holder.name.setText(name_surname);

        att.add(0);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(click_button==0){
                    holder.name.setBackgroundResource(R.color.colorAttandence);
                    click_button=1;
                    att.remove(position);
                    att.add(position,1);

                }
                else {
                    holder.name.setBackgroundResource(R.color.colorAttandence2);
                    click_button=0;
                    att.remove(position);
                    att.add(position,0);

                }

                ;
            }

        });





    }

    @Override
    public int getItemCount() {
        return mAttandenceList.size();
    }


    public class AttandenceViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        View mView;

        public AttandenceViewHolder(View v) {

            super(v);
            mView=v;

            name= v.findViewById(R.id.attandence_single_name);

        }
    }


}
