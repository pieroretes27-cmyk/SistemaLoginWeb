package com.mycompany.sistemaloginweb;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si intentan entrar por URL directamente, los redirige al login
        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recibir los parámetros enviados por POST desde el formulario (login.jsp)
        String usuarioIngresado = request.getParameter("usuario");
        String passwordIngresado = request.getParameter("password");

        // 2. Instanciar el Repositorio para validar contra la base de datos de forma desacoplada
        IUsuarioRepository repo = new UsuarioRepositoryImpl();
        String resultado = repo.validarCredenciales(usuarioIngresado, passwordIngresado);

        // 3. Evaluar el resultado de la validación y decidir el siguiente paso
        if ("EXITO".equals(resultado)) {
            // Credenciales correctas: Creamos sesión y redirigimos al Dashboard
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuarioIngresado);
            response.sendRedirect("dashboard.jsp");
            
        } else if ("BLOQUEADO".equals(resultado) || "BLOQUEADO_AHORA".equals(resultado)) {
            // Cuenta bloqueada por exceso de intentos fallidos
            response.sendRedirect("login.jsp?error=bloqueado");
            
        } else {
            // Contraseña incorrecta o usuario no existente (cuenta intentos)
            response.sendRedirect("login.jsp?error=incorrecto");
        }
    }
}