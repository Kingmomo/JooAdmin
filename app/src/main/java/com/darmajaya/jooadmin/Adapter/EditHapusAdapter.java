package com.darmajaya.jooadmin.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.darmajaya.jooadmin.EditActivity;
import com.darmajaya.jooadmin.EditHapusActivity;
import com.darmajaya.jooadmin.Model.Produk;
import com.darmajaya.jooadmin.R;
import com.darmajaya.jooadmin.utils.MySharedPreference;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import es.dmoral.toasty.Toasty;


public class EditHapusAdapter extends FirestoreRecyclerAdapter<Produk, EditHapusAdapter.MyViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * com.firebase.ui.firestore.FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private Context context;
    private MySharedPreference mySharedPreference;
    private FirebaseFirestore db;
    private StorageReference storage;


    public EditHapusAdapter(@NonNull FirestoreRecyclerOptions<Produk> options, Context context) {
        super(options);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, final int i, @NonNull final Produk model) {
        db = FirebaseFirestore.getInstance();
        mySharedPreference = new MySharedPreference(context);
        holder.nama.setText(model.getNama_produk());
        holder.harga.setText(model.getHarga());
        holder.deskripsi.setText(model.getDeskripsi());

        Glide.with(context)
                .load(model.getFoto())
                .apply(
                        new RequestOptions()
                                .placeholder(R.color.pink_200)
                                .fitCenter())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.foto);

        holder.hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog(getSnapshots().getSnapshot(i).getId(), model.getFoto());
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("uid", getSnapshots().getSnapshot(i).getId());
                intent.putExtra("foto", model.getFoto());
                intent.putExtra("nama", model.getNama_produk());
                intent.putExtra("harga", model.getHarga());
                intent.putExtra("deskripsi", model.getDeskripsi());
                context.startActivity(intent);
            }
        });

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_hapus, parent, false);
        return new EditHapusAdapter.MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nama, harga, deskripsi;
        View view;
        ImageView foto;
        Button edit, hapus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            foto = view.findViewById(R.id.foto);
            nama = view.findViewById(R.id.nama);
            harga = view.findViewById(R.id.harga);
            deskripsi = view.findViewById(R.id.deskripsi);
            edit = view.findViewById(R.id.edit);
            hapus = view.findViewById(R.id.hapus);

        }
    }

    private void dialog(final String uid, String urlfoto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        storage = FirebaseStorage.getInstance().getReferenceFromUrl(urlfoto);
        builder.setMessage("Anda Yakin Ingin Mengapus Produk?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        storage.delete();
                        db.collection("produk").document(uid).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toasty.success(context, "Produk Berhasil Di Hapus", Toasty.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.warning(context, "Produk Gagal Di Hapus", Toasty.LENGTH_SHORT).show();
                                    }
                                });
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
}
