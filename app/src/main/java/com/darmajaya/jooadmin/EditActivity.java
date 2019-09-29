package com.darmajaya.jooadmin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.darmajaya.jooadmin.Model.AddProduct;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "Uploadctivity";
    private ImageView foto;
    private Button tambahproduk;
    private EditText nama_produk, harga, deskripsi, waktu;
    private static final String FB_STORAGE_PATH = "produktoko/";
    private static final int GALLERY_REQUEST_CODE = 6;
    private int MAP = 3;
    private StorageReference storage;
    private Uri imgUri;
    private UploadTask uploadTask;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        foto = (ImageView) findViewById(R.id.foto);
        tambahproduk = (Button) findViewById(R.id.tambahproduk);
        nama_produk = findViewById(R.id.nama_produk);
        harga = findViewById(R.id.harga);
        deskripsi = findViewById(R.id.deskripsi);
        waktu = findViewById(R.id.waktu);
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance().getReference();
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
        clickupload();

        if(getIntent().getStringExtra("uid") != null){
            Glide.with(EditActivity.this)
                    .load(getIntent().getStringExtra("foto"))
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.color.pink_200)
                                    .fitCenter())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(foto);
            uid = getIntent().getStringExtra("uid");
            nama_produk.setText(getIntent().getStringExtra("nama"));
            harga.setText(getIntent().getStringExtra("harga"));
            deskripsi.setText(getIntent().getStringExtra("deskripsi"));
            waktu.setText(getIntent().getStringExtra("waktu"));
        }


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
                foto.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void clickupload() {
        tambahproduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgUri != null) {

                    final ProgressDialog dialog = new ProgressDialog(EditActivity.this);
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

                                final ProgressDialog dialog = new ProgressDialog(EditActivity.this);
                                dialog.setTitle("Uploading image");
                                dialog.setMessage("Sedang Proses");
                                dialog.show();

                                DocumentReference updatekonfimasi = db.collection("produk").document(getIntent().getStringExtra("uid"));
                                updatekonfimasi.update("nama_produk", nama_produk.getText().toString());
                                updatekonfimasi.update("deskripsi", deskripsi.getText().toString());
                                updatekonfimasi.update("foto", downloadUri.toString());
                                updatekonfimasi.update("harga", harga.getText().toString());
                                updatekonfimasi.update("waktu", waktu.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();

                                    }
                                });


                                Toast.makeText(EditActivity.this, "Upload Produk Sukses", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(EditActivity.this, "Upload Produk Gagal", Toast.LENGTH_SHORT).show();

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
                    final ProgressDialog dialog = new ProgressDialog(EditActivity.this);
                    dialog.setTitle("Loading");
                    dialog.setMessage("Sedang Proses");
                    dialog.show();

                    DocumentReference updatekonfimasi = db.collection("produk").document(getIntent().getStringExtra("uid"));
                    updatekonfimasi.update("nama_produk", nama_produk.getText().toString());
                    updatekonfimasi.update("deskripsi", deskripsi.getText().toString());
                    updatekonfimasi.update("harga", harga.getText().toString());
                    updatekonfimasi.update("waktu", waktu.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            finish();

                        }
                    });
                }

            }
        });
    }
}
