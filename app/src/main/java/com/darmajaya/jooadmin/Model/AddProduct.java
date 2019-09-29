package com.darmajaya.jooadmin.Model;

public class AddProduct{
    String nama_produk, foto, harga, deskripsi, waktu;

    public AddProduct() {
    }

    public AddProduct(String nama_produk, String foto, String harga, String deskripsi, String waktu) {
        this.nama_produk = nama_produk;
        this.foto = foto;
        this.harga = harga;
        this.deskripsi = deskripsi;
        this.waktu = waktu;
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

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }
}