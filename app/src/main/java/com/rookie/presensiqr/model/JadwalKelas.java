package com.rookie.presensiqr.model;

public class JadwalKelas {
    private String hari,kelas,pukul;

    public JadwalKelas(String hari, String kelas, String pukul) {
        this.hari = hari;
        this.kelas = kelas;
        this.pukul = pukul;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getPukul() {
        return pukul;
    }

    public void setPukul(String pukul) {
        this.pukul = pukul;
    }
}
