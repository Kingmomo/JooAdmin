package com.darmajaya.jooadmin;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darmajaya.jooadmin.Adapter.PesananAdapter;
import com.darmajaya.jooadmin.Model.Transaksi;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PesananActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private PesananAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Daftar Pesanan");

        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mGrid = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mGrid);
        recyclerView.setHasFixedSize(true);
        starter();

        TextView filter = findViewById(R.id.filtersort);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

    }

    private void starter() {
        Query query = FirebaseFirestore.getInstance().collection("transaksi").orderBy("jarak", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Transaksi> options = new FirestoreRecyclerOptions.Builder<Transaksi>()
                .setQuery(query, Transaksi.class)
                .build();

        adapter = new PesananAdapter(options, PesananActivity.this);
        recyclerView.setAdapter(adapter);
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.filter_bottom, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(PesananActivity.this);
        dialog.setContentView(view);
        LinearLayout konfirmasi = view.findViewById(R.id.tertinggi);
        LinearLayout belum = view.findViewById(R.id.terendah);
        LinearLayout selesai = view.findViewById(R.id.selesai);
        LinearLayout proses = view.findViewById(R.id.proses);


        konfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort("1");
                dialog.dismiss();
            }
        });
        belum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort("0");
                dialog.dismiss();
            }
        });
        selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort("2");
                dialog.dismiss();
            }
        });
        proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort("4");
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    private void sort(String data) {
        Query query;
        query = FirebaseFirestore.getInstance().collection("transaksi");
        if (data.equals("1")) {
            query = FirebaseFirestore.getInstance().collection("transaksi").whereEqualTo("status", "Konfirmasi Pembayaran Di Proses").orderBy("jarak", Query.Direction.ASCENDING);
        } else if (data.equals("0")) {
            query = FirebaseFirestore.getInstance().collection("transaksi").whereEqualTo("status", "Belum Di Konfirmasi").orderBy("jarak", Query.Direction.ASCENDING);
        } else if (data.equals("2")) {
            query = FirebaseFirestore.getInstance().collection("transaksi").whereEqualTo("status", "Selesai").orderBy("jarak", Query.Direction.ASCENDING);
        } else {
            query = FirebaseFirestore.getInstance().collection("transaksi").whereEqualTo("status", "Pesanan Di Proses").orderBy("jarak", Query.Direction.ASCENDING);

        }

        FirestoreRecyclerOptions<Transaksi> options = new FirestoreRecyclerOptions.Builder<Transaksi>()
                .setQuery(query, Transaksi.class)
                .build();

        adapter = new PesananAdapter(options, PesananActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

}
