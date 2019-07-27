package com.darmajaya.jooadmin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darmajaya.jooadmin.Adapter.EditHapusAdapter;
import com.darmajaya.jooadmin.Model.Produk;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class EditHapusActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditHapusAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hapus);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Dan hapus produk");

        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mGrid = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mGrid);
        recyclerView.setHasFixedSize(true);
        starter();
    }

    private void starter() {
        Query query = FirebaseFirestore.getInstance().collection("produk").orderBy("nama_produk", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Produk> options = new FirestoreRecyclerOptions.Builder<Produk>()
                .setQuery(query, Produk.class)
                .build();

        adapter = new EditHapusAdapter(options, EditHapusActivity.this);
        recyclerView.setAdapter(adapter);
    }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }
}
