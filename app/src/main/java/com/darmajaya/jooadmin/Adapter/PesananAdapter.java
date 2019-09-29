package com.darmajaya.jooadmin.Adapter;

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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.darmajaya.jooadmin.MapsPesananActivity;
import com.darmajaya.jooadmin.Model.Transaksi;
import com.darmajaya.jooadmin.PesananDetailActivity;
import com.darmajaya.jooadmin.R;
import com.darmajaya.jooadmin.utils.MySharedPreference;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class PesananAdapter extends FirestoreRecyclerAdapter<Transaksi, PesananAdapter.MyViewHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * com.firebase.ui.firestore.FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private Context context;
    private MySharedPreference mySharedPreference;
    private FirebaseFirestore db;


    public PesananAdapter(@NonNull FirestoreRecyclerOptions<Transaksi> options, Context context) {
        super(options);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, final int i, @NonNull final Transaksi model) {
        db = FirebaseFirestore.getInstance();
        mySharedPreference = new MySharedPreference(context);
        holder.nama.setText(model.getNama());
        holder.total.setText(model.getTotal());
        holder.tanggal.setText(model.getTanggal());
        holder.status.setText(model.getStatus());
        holder.jarak.setText(waktu(model.getWaktu()));
        holder.idtransaksi.setText(getSnapshots().getSnapshot(i).getId());
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PesananDetailActivity.class);
                intent.putExtra("total", model.getTotal());
                intent.putExtra("listproduk", model.getListproduk());
                intent.putExtra("konfirmasi", model.getBukti_transfer());
                context.startActivity(intent);

            }
        });

        holder.konfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialogWithListview(getSnapshots().getSnapshot(i).getId());
            }
        });

        holder.peta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsPesananActivity.class);
                intent.putExtra("koordinat", model.getKoordinat());
                context.startActivity(intent);
            }
        });

    }

    private void ShowAlertDialogWithListview(final String uid) {
        List<String> list = new ArrayList<String>();
        list.add("Pesanan Di Proses");
        list.add("Selesai");
        //Create sequence of items  
        final CharSequence[] Animals = list.toArray(new String[list.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Pilih Konfirmasi");
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Animals[item].toString();
                db = FirebaseFirestore.getInstance();
                Map<String, Object> status = new HashMap<>();
                status.put("status", selectedText);
                DocumentReference updatekonfimasi = db.collection("transaksi").document(uid);
                updatekonfimasi.update(status).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.success(context, "Status Berhasil Di Update", Toasty.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override

                    public void onFailure(@NonNull Exception e) {
                        Toasty.warning(context, "Status Gagal Di Update", Toasty.LENGTH_SHORT).show();
                    }
                });


            }
        });
        //Create alert dialog object via builder  
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog  
        alertDialogObject.show();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new PesananAdapter.MyViewHolder(view);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nama, idtransaksi, total, tanggal, status, jarak;
        View view;
        ImageView detail;
        Button konfirmasi, peta;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            detail = view.findViewById(R.id.detail);
            nama = view.findViewById(R.id.nama);
            idtransaksi = view.findViewById(R.id.idtransaksi);
            total = view.findViewById(R.id.total);
            tanggal = view.findViewById(R.id.tanggal);
            status = view.findViewById(R.id.status);
            konfirmasi = view.findViewById(R.id.konfirmasi);
            jarak = view.findViewById(R.id.waktu);
            peta = view.findViewById(R.id.peta);
        }
    }

    public String waktu(int time){
        return time/24/60 + " Hari - " + time/60%24 + " Jam - " + time%60 + " Menit";
    }
}
