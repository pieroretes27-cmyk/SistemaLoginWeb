package com.mycompany.sistemaloginweb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public String validarLogin(String user, String pass) {
        String resultado = "ERROR";
        Connection con = null;

        try {
            con = Conexion.getInstancia().getConnection();

            // 1. Consultar el estado, intentos y contraseña del usuario
            String sqlCheck = "SELECT intentos, estado, password FROM usuarios WHERE username = ?";
            PreparedStatement psCheck = con.prepareStatement(sqlCheck);
            psCheck.setString(1, user);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                int intentos = rs.getInt("intentos");
                String estado = rs.getString("estado");
                String passwordBD = rs.getString("password");

                // 2. Verificar si la cuenta ya está bloqueada
                if ("BLOQUEADO".equals(estado)) {
                    return "BLOQUEADO";
                }

                // 3. Validar si la contraseña es correcta
                if (passwordBD.equals(pass)) {
                    // Contraseña correcta: Reiniciar intentos a 0 y asegurar estado ACTIVO
                    String sqlReset = "UPDATE usuarios SET intentos = 0, estado = 'ACTIVO' WHERE username = ?";
                    PreparedStatement psReset = con.prepareStatement(sqlReset);
                    psReset.setString(1, user);
                    psReset.executeUpdate();
                    return "EXITO";
                } else {
                    // 4. Contraseña incorrecta: Incrementar contador de intentos
                    intentos++;
                    if (intentos >= 3) {
                        // Bloquear cuenta al llegar a 3 intentos fallidos
                        String sqlBloquear = "UPDATE usuarios SET intentos = ?, estado = 'BLOQUEADO' WHERE username = ?";
                        PreparedStatement psBloq = con.prepareStatement(sqlBloquear);
                        psBloq.setInt(1, intentos);
                        psBloq.setString(2, user);
                        psBloq.executeUpdate();
                        return "BLOQUEADO_AHORA";
                    } else {
                        // Solo actualizar el número de intentos fallidos
                        String sqlUpdate = "UPDATE usuarios SET intentos = ? WHERE username = ?";
                        PreparedStatement psUp = con.prepareStatement(sqlUpdate);
                        psUp.setInt(1, intentos);
                        psUp.setString(2, user);
                        psUp.executeUpdate();
                        return "PASS_INCORRECTA";
                    }
                }
            } else {
                resultado = "NO_EXISTE";
            }

        } catch (SQLException e) {
            System.out.println("Error en UsuarioDAO: " + e.getMessage());
        }
        return resultado;
    }
}