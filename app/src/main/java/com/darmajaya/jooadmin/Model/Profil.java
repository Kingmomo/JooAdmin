package com.darmajaya.jooadmin.Model;

public class Profil {
    private String alamat, atm_bank, atm_nama, atm_nomor, deskripsi, foto_logo, foto_toko, koordinat, nama_pemilik, nama_toko, no_hp;

    public Profil(String alamat, String atm_bank, String atm_nama, String atm_nomor, String deskripsi, String foto_logo, String foto_toko, String koordinat, String nama_pemilik, String nama_toko, String no_hp) {
        this.alamat = alamat;
        this.atm_bank = atm_bank;
        this.atm_nama = atm_nama;
        this.atm_nomor = atm_nomor;
        this.deskripsi = deskripsi;
        this.foto_logo = foto_logo;
        this.foto_toko = foto_toko;
        this.koordinat = koordinat;
        this.nama_pemilik = nama_pemilik;
        this.nama_toko = nama_toko;
        this.no_hp = no_hp;
    }

    public Profil() {
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getAtm_bank() {
        return atm_bank;
    }

    public void setAtm_bank(String atm_bank) {
        this.atm_bank = atm_bank;
    }

    public String getAtm_nama() {
        return atm_nama;
    }

    public void setAtm_nama(String atm_nama) {
        this.atm_nama = atm_nama;
    }

    public String getAtm_nomor() {
        return atm_nomor;
    }

    public void setAtm_nomor(String atm_nomor) {
        this.atm_nomor = atm_nomor;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getFoto_logo() {
        return foto_logo;
    }

    public void setFoto_logo(String foto_logo) {
        this.foto_logo = foto_logo;
    }

    public String getFoto_toko() {
        return foto_toko;
    }

    public void setFoto_toko(String foto_toko) {
        this.foto_toko = foto_toko;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }

    public String getNama_pemilik() {
        return nama_pemilik;
    }

    public void setNama_pemilik(String nama_pemilik) {
        this.nama_pemilik = nama_pemilik;
    }

    public String getNama_toko() {
        return nama_toko;
    }

    public void setNama_toko(String nama_toko) {
        this.nama_toko = nama_toko;
    }

    public String getNo_hp() {
        return no_hp;
    }

    public void setNo_hp(String no_hp) {
        this.no_hp = no_hp;
    }
}
