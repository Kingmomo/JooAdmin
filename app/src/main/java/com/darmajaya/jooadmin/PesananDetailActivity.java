package com.darmajaya.jooadmin;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.darmajaya.jooadmin.Adapter.PesananDetailAdapter;
import com.darmajaya.jooadmin.Model.Produk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PesananDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PesananDetailAdapter adapter;
    private Gson gson;
    private List<Produk> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan_detail);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        final TextView deskripsi = findViewById(R.id.deskripsi);
        TextView total = findViewById(R.id.total);
        TextView lihatbukti = findViewById(R.id.lihatbukti);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Pesanan");

        String name = getIntent().getStringExtra("total");
        String listproduk = getIntent().getStringExtra("listproduk");
        final String konfirmasi = getIntent().getStringExtra("konfirmasi");
        total.setText(name);

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();

        Produk[] addCartProducts = gson.fromJson(listproduk, Produk[].class);
        productList = convertObjectArrayToListObject(addCartProducts);
        LinearLayoutManager mGrid = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mGrid);
        recyclerView.setHasFixedSize(true);
        adapter = new PesananDetailAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        if (getIntent().getStringExtra("konfirmasi") == null) {
            lihatbukti.setText("Belum di Transfer Pembeli");
            lihatbukti.setBackgroundColor(getResources().getColor(R.color.red_800));
        }else{
            lihatbukti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogImageFull(konfirmasi);
                }
            });
        }


    }

    private List<Produk> convertObjectArrayToListObject(Produk[] allProducts) {
        List<Produk> mProduct = new ArrayList<Produk>();
        if (allProducts != null) {
            Collections.addAll(mProduct, allProducts);
        }
        return mProduct;
    }

    private void showDialogImageFull(String url) {
        final Dialog dialog = new Dialog(PesananDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_transfer);
        ImageView foto = dialog.findViewById(R.id.transfer);


        Glide.with(getApplicationContext()).load(url).into(foto);

        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
}
