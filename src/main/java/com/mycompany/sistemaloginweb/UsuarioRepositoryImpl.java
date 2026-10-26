package com.mycompany.sistemaloginweb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositoryImpl implements IUsuarioRepository {

    @Override
    public String validarCredenciales(String user, String pass) {
        Connection con = null;
        try {
            con = Conexion.getInstancia().getConnection();
            String sqlCheck = "SELECT intentos, estado, password FROM usuarios WHERE username = ?";
            PreparedStatement psCheck = con.prepareStatement(sqlCheck);
            psCheck.setString(1, user);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                int intentos = rs.getInt("intentos");
                String estado = rs.getString("estado");
                String passwordBD = rs.getString("password");

                if ("BLOQUEADO".equals(estado)) {
                    return "BLOQUEADO";
                }

                if (passwordBD.equals(pass)) {
                    String sqlReset = "UPDATE usuarios SET intentos = 0, estado = 'ACTIVO' WHERE username = ?";
                    PreparedStatement psReset = con.prepareStatement(sqlReset);
                    psReset.setString(1, user);
                    psReset.executeUpdate();
                    return "EXITO";
                } else {
                    intentos++;
                    if (intentos >= 3) {
                        String sqlBloquear = "UPDATE usuarios SET intentos = ?, estado = 'BLOQUEADO' WHERE username = ?";
                        PreparedStatement psBloq = con.prepareStatement(sqlBloquear);
                        psBloq.setInt(1, intentos);
                        psBloq.setString(2, user);
                        psBloq.executeUpdate();
                        return "BLOQUEADO_AHORA";
                    } else {
                        String sqlUpdate = "UPDATE usuarios SET intentos = ? WHERE username = ?";
                        PreparedStatement psUp = con.prepareStatement(sqlUpdate);
                        psUp.setInt(1, intentos);
                        psUp.setString(2, user);
                        psUp.executeUpdate();
                        return "PASS_INCORRECTA";
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en validarCredenciales: " + e.getMessage());
        }
        return "NO_EXISTE";
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        try {
            Connection con = Conexion.getInstancia().getConnection();
            String sql = "SELECT * FROM usuarios";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getInt("intentos"),
                    rs.getString("estado")
                );
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean guardar(Usuario u) {
        try {
            Connection con = Conexion.getInstancia().getConnection();
            String sql = "INSERT INTO usuarios (username, password, intentos, estado) VALUES (?, ?, 0, 'ACTIVO')";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        try {
            Connection con = Conexion.getInstancia().getConnection();
            String sql = "DELETE FROM usuarios WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean cambiarEstado(int id, String estado) {
        try {
            Connection con = Conexion.getInstancia().getConnection();
            String sql = "UPDATE usuarios SET estado = ?, intentos = 0 WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }
}