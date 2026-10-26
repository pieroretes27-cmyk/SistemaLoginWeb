<%@page import="com.mycompany.sistemaloginweb.Usuario"%>
<%@page import="java.util.List"%>
<%@page import="com.mycompany.sistemaloginweb.UsuarioRepositoryImpl"%>
<%@page import="com.mycompany.sistemaloginweb.IUsuarioRepository"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    IUsuarioRepository repo = new UsuarioRepositoryImpl();
    
    // Procesar acciones del CRUD (Guardar, Eliminar, Desbloquear)
    String accion = request.getParameter("accion");
    if ("guardar".equals(accion)) {
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        if (user != null && pass != null && !user.isEmpty()) {
            Usuario nuevo = new Usuario(0, user, pass, 0, "ACTIVO");
            repo.guardar(nuevo);
        }
    } else if ("eliminar".equals(accion)) {
        int id = Integer.parseInt(request.getParameter("id"));
        repo.eliminar(id);
    } else if ("desbloquear".equals(accion)) {
        int id = Integer.parseInt(request.getParameter("id"));
        repo.cambiarEstado(id, "ACTIVO");
    }

    List<Usuario> usuarios = repo.listarTodos();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Dashboard - CRUD de Usuarios</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">

    <!-- Barra de navegación -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="#"><i class="bi bi-speedometer2"></i> SistemaLoginWeb - Dashboard</a>
            <div class="d-flex">
                <span class="navbar-text me-3 text-white">Panel de Administración</span>
                <a href="login.jsp" class="btn btn-outline-danger btn-sm"><i class="bi bi-box-arrow-right"></i> Salir</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <!-- Encabezado -->
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h2><i class="bi bi-people-fill text-primary"></i> Gestión de Usuarios</h2>
            <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modalUsuario">
                <i class="bi bi-person-plus-fill"></i> Nuevo Usuario
            </button>
        </div>

        <!-- Tabla de Registros -->
        <div class="card shadow-sm">
            <div class="card-body p-0">
                <table class="table table-hover table-striped mb-0 align-middle">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Usuario</th>
                            <th>Contraseña</th>
                            <th>Intentos Fallidos</th>
                            <th>Estado</th>
                            <th class="text-center">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Usuario u : usuarios) { %>
                        <tr>
                            <td><%= u.getId() %></td>
                            <td><strong><%= u.getUsername() %></strong></td>
                            <td><code><%= u.getPassword() %></code></td>
                            <td>
                                <span class="badge <%= u.getIntentos() > 0 ? "bg-warning text-dark" : "bg-secondary" %>">
                                    <%= u.getIntentos() %> / 3
                                </span>
                            </td>
                            <td>
                                <% if ("ACTIVO".equals(u.getEstado())) { %>
                                    <span class="badge bg-success">ACTIVO</span>
                                <% } else { %>
                                    <span class="badge bg-danger">BLOQUEADO</span>
                                <% } %>
                            </td>
                            <td class="text-center">
                                <% if ("BLOQUEADO".equals(u.getEstado())) { %>
                                    <form action="dashboard.jsp" method="POST" class="d-inline">
                                        <input type="hidden" name="accion" value="desbloquear">
                                        <input type="hidden" name="id" value="<%= u.getId() %>">
                                        <button type="submit" class="btn btn-warning btn-sm" title="Desbloquear cuenta">
                                            <i class="bi bi-unlock-fill"></i> Desbloquear
                                        </button>
                                    </form>
                                <% } %>
                                <form action="dashboard.jsp" method="POST" class="d-inline" onsubmit="return confirm('¿Seguro de eliminar este usuario?');">
                                    <input type="hidden" name="accion" value="eliminar">
                                    <input type="hidden" name="id" value="<%= u.getId() %>">
                                    <button type="submit" class="btn btn-danger btn-sm" title="Eliminar usuario">
                                        <i class="bi bi-trash-fill"></i> Eliminar
                                    </button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Modal para Nuevo Usuario -->
    <div class="modal fade" id="modalUsuario" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <form action="dashboard.jsp" method="POST">
                    <input type="hidden" name="accion" value="guardar">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title"><i class="bi bi-person-plus"></i> Registrar Nuevo Usuario</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Nombre de Usuario</label>
                            <input type="text" name="username" class="form-control" required placeholder="Ej. JuanPerez">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Contraseña</label>
                            <input type="password" name="password" class="form-control" required placeholder="******">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary">Guardar Usuario</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>