package com.darmajaya.jooadmin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.darmajaya.jooadmin.utils.MySharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private MySharedPreference mySharedPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        CardView pesanan = findViewById(R.id.pesanan);
        CardView profil = findViewById(R.id.profil);
        CardView tambahproduk = findViewById(R.id.tambahproduk);
        CardView edithapus = findViewById(R.id.edithapus);
        final ImageView logo = findViewById(R.id.logo);
        final TextView namatoko = findViewById(R.id.namatoko);
        Button sign_out = findViewById(R.id.sign_out);


        pesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PesananActivity.class));

            }
        });

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfilActivity.class));
            }
        });

        tambahproduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddProductActivity.class));

            }
        });

        edithapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditHapusActivity.class));

            }
        });
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });



        mySharedPreference = new MySharedPreference(this);

        final DocumentReference docToko = db.collection("profil").document("dataprofil");
        docToko.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mySharedPreference.setLokasi(document.getString("koordinat"));

                        Glide.with(MainActivity.this)
                                .load(document.get("foto_logo"))
                                .apply(
                                        new RequestOptions()
                                                .placeholder(R.color.blue_800)
                                                .fitCenter())
                                .into(logo);

                        namatoko.setText(document.get("nama_toko").toString());

                        logo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, Logo.class);
                                if (document.get("foto_logo") != null) {
                                    intent.putExtra("logo", document.get("foto_logo").toString());
                                }
                                startActivity(intent);
                            }
                        });
                    } else {
                        mySharedPreference.setLokasi("-5.422359, 105.258188");
                    }
                } else {
                    mySharedPreference.setLokasi("-5.422359, 105.258188");
                }
            }
        });

    }

    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Anda ingin Keluar")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        dialog();
    }

}
