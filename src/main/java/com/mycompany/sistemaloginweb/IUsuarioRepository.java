package com.mycompany.sistemaloginweb;

import java.util.List;

public interface IUsuarioRepository {
    String validarCredenciales(String username, String password);
    List<Usuario> listarTodos();
    boolean guardar(Usuario usuario);
    boolean eliminar(int id);
    boolean cambiarEstado(int id, String estado);
}