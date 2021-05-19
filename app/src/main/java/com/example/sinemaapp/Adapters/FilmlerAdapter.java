package com.example.sinemaapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sinemaapp.Classlar.Film;
import com.example.sinemaapp.FilmlerActivity;
import com.example.sinemaapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FilmlerAdapter extends RecyclerView.Adapter<FilmlerAdapter.FilmAdapterTutucu>{
    private List<Film>filmList;
    private Activity mContext;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    public FilmlerAdapter(List<Film> filmList,Activity mContext) {
        this.filmList = filmList;
        this.mContext = mContext;
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
    }

    @NonNull
    @Override
    public FilmAdapterTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.filmler_adapter,parent,false);
        return new FilmAdapterTutucu(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmAdapterTutucu holder, int position) {
        Film f=filmList.get(position);
        holder.textView.setText(f.getAd());
        Picasso.get().load(f.getFotoUrl()).into(holder.imageView);

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view,"Film Listeden silinsin mi?", BaseTransientBottomBar.LENGTH_LONG).setAction("Evet", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      reference.child("Filmler").child(f.getAd()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()){
                                 Snackbar.make(view,"Silindi",BaseTransientBottomBar.LENGTH_SHORT).show();
                                 Intent intent=new Intent(mContext,FilmlerActivity.class);
                                 mContext.startActivity(intent);
                                mContext.finish();
                              }
                          }
                      });
                    }
                }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filmList.size();
    }

    public class FilmAdapterTutucu extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView textView;
        private ImageView imageView;
        private ImageButton imageButton;
        public FilmAdapterTutucu(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardFilm);
            textView=itemView.findViewById(R.id.textViewFilmAdAdpter);
            imageView=itemView.findViewById(R.id.imageViewFilmAdapter);
            imageButton=itemView.findViewById(R.id.imageButton);
        }
    }

}
