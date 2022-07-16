package com.example.ejercicio2_3.Models;

public class Fotografia {
    private Integer id;
    private String description;
    private byte[] foto;

    public Fotografia() {
    }

    public Fotografia(Integer id, String description, byte[] foto) {
        this.id = id;
        this.description = description;
        this.foto = foto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
}
