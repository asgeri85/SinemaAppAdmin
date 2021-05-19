package com.example.sinemaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sinemaapp.Classlar.RandomName;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FilmEkleActivity extends AppCompatActivity {
    private EditText editTextAd,editTextYil,editTextYonetmen,editTextİmdb,editTextTur,editTextAciklama;
    private Button button;
    private ImageView imageView;
    private Bitmap bitmap;
    private FirebaseStorage storage;
    private StorageReference reference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private  String  imageUrl="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_ekle);
        editTextAciklama=findViewById(R.id.editAciklama);
        editTextAd=findViewById(R.id.editAd);
        editTextİmdb=findViewById(R.id.editİmdb);
        editTextTur=findViewById(R.id.editTur);
        editTextYil=findViewById(R.id.editTarih);
        editTextYonetmen=findViewById(R.id.editYonetmen);
        button=findViewById(R.id.buttonYukle);
        imageView=findViewById(R.id.imageViewYuklenen);
        storage=FirebaseStorage.getInstance();
        reference=storage.getReference();
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galeriAc();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ad=editTextAd.getText().toString();
                String yil=editTextYil.getText().toString();
                String aciklama=editTextAciklama.getText().toString();
                String imdb=editTextİmdb.getText().toString();
                String tur=editTextTur.getText().toString();
                String yonetmen=editTextYonetmen.getText().toString();
                if (!ad.isEmpty() && !yil.isEmpty() && !aciklama.isEmpty() && !imdb.isEmpty() && !tur.isEmpty() && !yonetmen.isEmpty() && !imageUrl.equals("")){
                    bilgiEkle(ad,yil,yonetmen,aciklama,imageUrl,tur,imdb);
                }else{
                    Toast.makeText(getApplicationContext(),"Tüm Alanları doldurunuz ve Resim Ekleyiniz",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void galeriAc(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,777);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==777 && resultCode==RESULT_OK && data!=null){
            Uri uri=data.getData();
            try {
                bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                imageView.setImageBitmap(bitmap);
                StorageReference put=reference.child("Fotolar").child(RandomName.getRandomString()+".jpg");
                put.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl=uri.toString();
                               Toast.makeText(getApplicationContext(),"Resim Secildi",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void bilgiEkle(String ad,String yil,String yonemt,String aciklama,String foto,String tur,String imdb){
        Map map=new HashMap();
        map.put("ad",ad);
        map.put("yil",yil);
        map.put("yonetmen",yonemt);
        map.put("aciklama",aciklama);
        map.put("tur",tur);
        map.put("imdb",imdb);
        map.put("fotoUrl",foto);

        databaseReference.child("Filmler").child(ad).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    koltukEkle(editTextAd.getText().toString());
                    Toast.makeText(getApplicationContext(),"Film Eklendi",Toast.LENGTH_SHORT).show();
                    editTextAd.setText("");
                    editTextAciklama.setText("");
                    editTextİmdb.setText("");
                    editTextTur.setText("");
                    editTextYil.setText("");
                    editTextYonetmen.setText("");
                    imageView.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
                }
            }
        });
    }

    public void koltukEkle(String ad){
        DatabaseReference referenceKoltur=databaseReference.child("Koltuklar").child(ad);

        for (int i=1;i<=50;i++){
            Map map=new HashMap();
            map.put("biletAlan","");
            referenceKoltur.child(String.valueOf(i)).setValue(map);
        }
    }
}