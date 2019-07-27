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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.darmajaya.jooadmin.Model.AddProduct;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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

import es.dmoral.toasty.Toasty;

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "Uploadctivity";
    private ImageView foto;
    private Button tambahproduk;
    private EditText nama_produk, harga, deskripsi;
    private static final String FB_STORAGE_PATH = "produktoko/";
    private static final int GALLERY_REQUEST_CODE = 5;
    private int MAP = 3;
    private StorageReference storage;
    private Uri imgUri;
    private UploadTask uploadTask;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        foto = (ImageView) findViewById(R.id.foto);
        tambahproduk = (Button) findViewById(R.id.tambahproduk);
        nama_produk = findViewById(R.id.nama_produk);
        harga = findViewById(R.id.harga);
        deskripsi = findViewById(R.id.deskripsi);
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

                    final ProgressDialog dialog = new ProgressDialog(AddProductActivity.this);
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

                                AddProduct produk = new AddProduct();
                                produk.setFoto(downloadUri.toString());
                                produk.setNama_produk(nama_produk.getText().toString());
                                produk.setHarga(harga.getText().toString());
                                produk.setDeskripsi(deskripsi.getText().toString());


                                DocumentReference updatekonfimasi = db.collection("produk").document();
                                updatekonfimasi.set(produk);


                                Toasty.success(AddProductActivity.this, "Upload Produk Sukses", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(AddProductActivity.this, MainActivity.class));
                            } else {
                                dialog.dismiss();
                                Toasty.warning(AddProductActivity.this, "Upload Produk Gagal", Toast.LENGTH_SHORT).show();

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
                    Toasty.warning(AddProductActivity.this, "Pilih Gambar Terlebih Dahulu", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

}
