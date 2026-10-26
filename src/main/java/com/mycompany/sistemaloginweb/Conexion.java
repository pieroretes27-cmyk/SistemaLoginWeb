package com.mycompany.sistemaloginweb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Conexion instancia = null;
    private Connection con = null;

    private Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // URL con parámetros de compatibilidad para MySQL 8.4 en el puerto 3307
            String url = "jdbc:mysql://localhost:3307/bd_login?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
           String user = "root";
           String password = "1234";
            
            con = DriverManager.getConnection(url, user, password);
            System.out.println("¡CONEXIÓN EXITOSA A MYSQL EN EL PUERTO 3307!");
            
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("ERROR CRÍTICO: No se encontró el Driver de MySQL en el proyecto.", e);
        } catch (SQLException e) {
            // Esto lanzará un error visible en la web explicando exactamente qué falló (clave incorrecta, servidor apagado, etc.)
            throw new RuntimeException("ERROR DE CONEXIÓN A MYSQL: " + e.getMessage() 
                + " -> Revisa si el puerto 3307 es correcto y si necesitas poner una contraseña en Conexion.java.", e);
        }
    }

    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        } else {
            try {
                if (instancia.con == null || instancia.con.isClosed()) {
                    instancia = new Conexion();
                }
            } catch (SQLException e) {
                instancia = new Conexion();
            }
        }
        return instancia;
    }

    public Connection getConnection() {
        return con;
    }
}