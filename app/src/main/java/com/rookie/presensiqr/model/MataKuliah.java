package com.rookie.presensiqr.model;

public class MataKuliah {
    private String kodeMatkul,namaMatkul,kelasMatkul,jamMatkul,hariMatkul,kodeKelas;

    public MataKuliah(String kodeMatkul, String namaMatkul, String kelasMatkul, String jamMatkul, String hariMatkul, String kodeKelas) {
        this.kodeMatkul = kodeMatkul;
        this.namaMatkul = namaMatkul;
        this.kelasMatkul = kelasMatkul;
        this.jamMatkul = jamMatkul;
        this.hariMatkul = hariMatkul;
        this.kodeKelas = kodeKelas;
    }

    public String getKodeMatkul() {
        return kodeMatkul;
    }

    public void setKodeMatkul(String kodeMatkul) {
        this.kodeMatkul = kodeMatkul;
    }

    public String getNamaMatkul() {
        return namaMatkul;
    }

    public void setNamaMatkul(String namaMatkul) {
        this.namaMatkul = namaMatkul;
    }

    public String getKelasMatkul() {
        return kelasMatkul;
    }

    public void setKelasMatkul(String kelasMatkul) {
        this.kelasMatkul = kelasMatkul;
    }

    public String getJamMatkul() {
        return jamMatkul;
    }

    public void setJamMatkul(String jamMatkul) {
        this.jamMatkul = jamMatkul;
    }

    public String getHariMatkul() {
        return hariMatkul;
    }

    public void setHariMatkul(String hariMatkul) {
        this.hariMatkul = hariMatkul;
    }

    public String getKodeKelas() {
        return kodeKelas;
    }

    public void setKodeKelas(String kodeKelas) {
        this.kodeKelas = kodeKelas;
    }
}
