package com.darmajaya.jooadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.HashMap;
import java.util.Map;

public class Logo extends AppCompatActivity {

    private static final String TAG = "Uploadctivity";
    private ImageView logo;
    private Button updatebtn;
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
        setContentView(R.layout.activity_logo);

        logo = (ImageView) findViewById(R.id.logo);
        updatebtn = findViewById(R.id.updatebtn);
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance().getReference();
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
        clickupload();

        if(getIntent().getStringExtra("logo") != null){
            Glide.with(Logo.this)
                    .load(getIntent().getStringExtra("foto"))
                    .apply(
                            new RequestOptions()
                                    .placeholder(R.color.pink_200)
                                    .fitCenter())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(logo);

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
                logo.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void clickupload() {
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgUri != null) {

                    final ProgressDialog dialog = new ProgressDialog(Logo.this);
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

                                Map<String, Object> toko = new HashMap<>();
                                toko.put("foto_logo", downloadUri.toString());


                                DocumentReference updatekonfimasi = db.collection("profil").document("dataprofil");
                                updatekonfimasi.update(toko);


                                Toast.makeText(Logo.this, "Update Logo Sukses", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(Logo.this, "Update Logo Gagal", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(Logo.this, "Pilih Gambar Terlebih Dahulu", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
