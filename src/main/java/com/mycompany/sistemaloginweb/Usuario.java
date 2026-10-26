package com.mycompany.sistemaloginweb;

public class Usuario {
    private int id;
    private String username;
    private String password;
    private int intentos;
    private String estado;

    public Usuario() {}

    public Usuario(int id, String username, String password, int intentos, String estado) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.intentos = intentos;
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getIntentos() { return intentos; }
    public void setIntentos(int intentos) { this.intentos = intentos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}