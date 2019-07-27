package com.darmajaya.jooadmin.Model;

public class AddProduct{
    String nama_produk, foto, harga, deskripsi;

    public AddProduct(String nama_produk, String foto, String harga, String deskripsi) {
        this.nama_produk = nama_produk;
        this.foto = foto;
        this.harga = harga;
        this.deskripsi = deskripsi;
    }

    public AddProduct() {
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public void setNama_produk(String nama_produk) {
        this.nama_produk = nama_produk;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}