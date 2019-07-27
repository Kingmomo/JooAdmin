package com.darmajaya.jooadmin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.darmajaya.jooadmin.Model.Profil;
import com.darmajaya.jooadmin.utils.Validate;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ProfilActivity extends AppCompatActivity {

    private static final String TAG = "Uploadctivity";
    private ImageView foto_toko, foto_logo;
    private Button btnupload;
    private EditText nama_toko, nama_pemilik, no_hp, atm_bank, atm_nomor, atm_nama, alamat, deskripsi, koordinat;
    private static final String FB_STORAGE_PATH = "profiltoko/";
    private static final int GALLERY_REQUEST_CODE = 5;
    private int MAP = 3;
    private StorageReference storage;
    private Uri imgUri;
    private UploadTask uploadTask;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        foto_toko = (ImageView) findViewById(R.id.foto_toko);
        btnupload = (Button) findViewById(R.id.btnupload);
        nama_toko = findViewById(R.id.nama_toko);
        nama_pemilik = findViewById(R.id.nama_pemilik);
        no_hp = findViewById(R.id.no_hp);
        atm_bank = findViewById(R.id.atm_bank);
        atm_nomor = findViewById(R.id.atm_nomor);
        atm_nama = findViewById(R.id.atm_nama);
        alamat = findViewById(R.id.alamat);
        deskripsi = findViewById(R.id.deskripsi);
        koordinat = findViewById(R.id.koordinat);
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance().getReference();

        Validate.dismissKeyboard(koordinat, getApplicationContext());
        koordinat.clearFocus();
        koordinat.setFocusable(false);

        foto_toko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
        koordinat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, MapsActivity.class);
                koordinat.setEnabled(false);
                startActivityForResult(intent, MAP);
            }
        });

        DocumentReference docRef = db.collection("profil").document("dataprofil");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Profil profil = documentSnapshot.toObject(Profil.class);
                nama_toko.setText(profil.getNama_toko());
                nama_pemilik.setText(profil.getNama_pemilik());
                no_hp.setText(profil.getNo_hp());
                atm_bank.setText(profil.getAtm_bank());
                atm_nomor.setText(profil.getAtm_nomor());
                atm_nama.setText(profil.getAtm_nama());
                alamat.setText(profil.getAlamat());
                deskripsi.setText(profil.getDeskripsi());
                koordinat.setText(profil.getKoordinat());

                Glide.with(ProfilActivity.this)
                        .load(profil.getFoto_toko())
                        .apply(
                                new RequestOptions()
                                        .placeholder(R.color.blue_800)
                                        .fitCenter())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(foto_toko);
            }
        });




        clickupload();



    }


    public String getImageExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                foto_toko.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == MAP) {

            if (resultCode == Activity.RESULT_OK) {
                double lat = (double) data.getDoubleExtra("location_lat", 0);
                double lng = (double) data.getDoubleExtra("location_lng", 0);
                System.out.println("mantap soul " + lat + " " + lng);
                koordinat.setText(String.valueOf(lat) + ", " + String.valueOf(lng));
            }


        }
    }


    public void clickupload() {
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgUri != null) {

                    final ProgressDialog dialog = new ProgressDialog(ProfilActivity.this);
                    dialog.setTitle("Uploading image");
                    dialog.show();

                    final StorageReference ref = storage.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));
                    uploadTask = ref.putFile(imgUri);
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Lanjutkan dengan Task untuk mendapat Url Gambar
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                dialog.dismiss();

                                Profil profil = new Profil();
                                profil.setFoto_toko(downloadUri.toString());
                                profil.setNama_toko(nama_toko.getText().toString());
                                profil.setNama_pemilik(nama_pemilik.getText().toString());
                                profil.setNo_hp(no_hp.getText().toString());
                                profil.setAtm_bank(atm_bank.getText().toString());
                                profil.setAtm_nomor(atm_nomor.getText().toString());
                                profil.setAtm_nama(atm_nama.getText().toString());
                                profil.setAlamat(alamat.getText().toString());
                                profil.setDeskripsi(deskripsi.getText().toString());
                                profil.setKoordinat(koordinat.getText().toString());


                                DocumentReference updatekonfimasi = db.collection("profil").document("dataprofil");
                                updatekonfimasi.set(profil);


                                Toast.makeText(ProfilActivity.this, "Upload Sukses", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                dialog.dismiss();
                                Toast.makeText(ProfilActivity.this, "Upload Gagal", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                    uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            dialog.setMessage("Uploaded " + progress + "%");
                        }
                    }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Upload is paused");
                        }
                    });
                } else {
                    Toast.makeText(ProfilActivity.this, "Pilih Gambar Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}
