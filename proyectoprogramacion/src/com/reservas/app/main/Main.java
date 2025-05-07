package com.reservas.app.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion = 0;

        inicializarCategorias();

        while (true) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Registrar Usuario Administrador");
            System.out.println("2. Registrar Usuario Cliente");
            System.out.println("3. Crear Negocio (Administrador)");
            System.out.println("4. Listar Negocios");
            System.out.println("5. Realizar Reserva (Cliente)");
            System.out.println("6. Listar Reservas de un Cliente");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");
            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Entrada inválida, intente nuevamente.");
                continue;
            }

            switch (opcion) {
                case 1:
                    registrarAdmin(sc);
                    break;
                case 2:
                    registrarCliente(sc);
                    break;
                case 3:
                    crearNegocio(sc);
                    break;
                case 4:
                    listarNegocios();
                    break;
                case 5:
                    realizarReserva(sc);
                    break;
                case 6:
                    listarReservasCliente(sc);
                    break;
                case 7:
                    System.out.println("Saliendo del sistema...");
                    DatabaseConnection.closeConnection();
                    sc.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opción no válida, intente nuevamente.");
            }
        }
    }

    private static void inicializarCategorias() {
        String[] categorias = {"Restaurantes", "Hoteles", "Tiendas","Peluqueria","Discoteca","Barberia","Bisuteria","Cacharreria"};
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (String nombre : categorias) {
                String checkSql = "SELECT COUNT(*) FROM categorias WHERE nombre = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, nombre);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    String insertSql = "INSERT INTO categorias (nombre) VALUES (?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                    insertStmt.setString(1, nombre);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al inicializar categorías: " + e.getMessage());
        }
    }

    private static void registrarAdmin(Scanner sc) {
        System.out.print("Ingrese nombre del Admin: ");
        String nombre = sc.nextLine();
        System.out.print("Ingrese correo: ");
        String correo = sc.nextLine();
        System.out.print("Ingrese contraseña: ");
        String contrasena = sc.nextLine();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            System.out.println("Todos los campos son obligatorios.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO usuarios (nombre, correo, contrasena, tipo_usuario) VALUES (?, ?, ?, 'Administrador')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            stmt.setString(2, correo);
            stmt.setString(3, contrasena);
            stmt.executeUpdate();
            System.out.println("Administrador registrado exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al registrar administrador: " + e.getMessage());
        }
    }

    private static void registrarCliente(Scanner sc) {
        System.out.print("Ingrese nombre del Cliente: ");
        String nombre = sc.nextLine();
        System.out.print("Ingrese correo: ");
        String correo = sc.nextLine();
        System.out.print("Ingrese contraseña: ");
        String contrasena = sc.nextLine();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            System.out.println("Todos los campos son obligatorios.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO usuarios (nombre, correo, contrasena, tipo_usuario) VALUES (?, ?, ?, 'Cliente')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            stmt.setString(2, correo);
            stmt.setString(3, contrasena);
            stmt.executeUpdate();
            System.out.println("Cliente registrado exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al registrar cliente: " + e.getMessage());
        }
    }

    private static void crearNegocio(Scanner sc) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si hay administradores
            String countAdminsSql = "SELECT COUNT(*) FROM usuarios WHERE tipo_usuario = 'Administrador'";
            PreparedStatement countStmt = conn.prepareStatement(countAdminsSql);
            ResultSet rs = countStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Primero debe registrar un Administrador.");
                return;
            }

            // Mostrar administradores disponibles
            System.out.println("Administradores disponibles:");
            String adminsSql = "SELECT id, nombre FROM usuarios WHERE tipo_usuario = 'Administrador'";
            PreparedStatement adminsStmt = conn.prepareStatement(adminsSql);
            rs = adminsStmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("nombre"));
            }

            System.out.print("Seleccione el administrador por ID: ");
            int idAdmin;
            try {
                idAdmin = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada no numérica. Intente nuevamente.");
                return;
            }

            // Verificar si el administrador existe
            String checkAdminSql = "SELECT COUNT(*) FROM usuarios WHERE id = ? AND tipo_usuario = 'Administrador'";
            PreparedStatement checkAdminStmt = conn.prepareStatement(checkAdminSql);
            checkAdminStmt.setInt(1, idAdmin);
            rs = checkAdminStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Administrador no válido.");
                return;
            }

            System.out.print("Ingrese nombre del negocio: ");
            String nombreNegocio = sc.nextLine();

            System.out.print("Ingrese la ciudad: ");
            String ciudad = sc.nextLine();
            System.out.print("Ingrese la calle: ");
            String calle = sc.nextLine();
            System.out.print("Ingrese el número: ");
            String numero = sc.nextLine();

            // Insertar dirección
            String insertDirSql = "INSERT INTO direcciones (ciudad, calle, numero) VALUES (?, ?, ?)";
            PreparedStatement dirStmt = conn.prepareStatement(insertDirSql, PreparedStatement.RETURN_GENERATED_KEYS);
            dirStmt.setString(1, ciudad);
            dirStmt.setString(2, calle);
            dirStmt.setString(3, numero);
            dirStmt.executeUpdate();
            rs = dirStmt.getGeneratedKeys();
            rs.next();
            int idDireccion = rs.getInt(1);

            // Insertar negocio
            String insertNegocioSql = "INSERT INTO negocios (nombre, id_propietario, id_direccion) VALUES (?, ?, ?)";
            PreparedStatement negocioStmt = conn.prepareStatement(insertNegocioSql, PreparedStatement.RETURN_GENERATED_KEYS);
            negocioStmt.setString(1, nombreNegocio);
            negocioStmt.setInt(2, idAdmin);
            negocioStmt.setInt(3, idDireccion);
            negocioStmt.executeUpdate();
            rs = negocioStmt.getGeneratedKeys();
            rs.next();
            int idNegocio = rs.getInt(1);

            // Asignar categorías al negocio
            System.out.println("Categorías disponibles:");
            String catsSql = "SELECT id, nombre FROM categorias";
            PreparedStatement catsStmt = conn.prepareStatement(catsSql);
            rs = catsStmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("nombre"));
            }

            System.out.print("Seleccione el ID de la categoría a asignar (0 para terminar): ");
            while (true) {
                int catId;
                try {
                    catId = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Entrada no numérica. Intente nuevamente.");
                    break;
                }
                if (catId == 0) break;

                // Verificar si la categoría existe
                String checkCatSql = "SELECT COUNT(*) FROM categorias WHERE id = ?";
                PreparedStatement checkCatStmt = conn.prepareStatement(checkCatSql);
                checkCatStmt.setInt(1, catId);
                rs = checkCatStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    String insertCatNegSql = "INSERT INTO negocio_categorias (id_negocio, id_categoria) VALUES (?, ?)";
                    PreparedStatement insertCatNegStmt = conn.prepareStatement(insertCatNegSql);
                    insertCatNegStmt.setInt(1, idNegocio);
                    insertCatNegStmt.setInt(2, catId);
                    insertCatNegStmt.executeUpdate();
                    System.out.println("Categoría asignada.");
                } else {
                    System.out.println("Categoría no válida.");
                }
                System.out.print("Seleccione otra categoría (0 para terminar): ");
            }

            System.out.println("Negocio creado exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al crear negocio: " + e.getMessage());
        }
    }

    private static void listarNegocios() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT n.id, n.nombre, u.nombre AS propietario, d.ciudad, d.calle, d.numero " +
                        "FROM negocios n " +
                        "JOIN usuarios u ON n.id_propietario = u.id " +
                        "JOIN direcciones d ON n.id_direccion = d.id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No hay negocios registrados.");
                return;
            }

            System.out.println("---- Lista de Negocios ----'s ----");
            while (rs.next()) {
                System.out.println("Negocio: " + rs.getString("nombre"));
                System.out.println("Propietario: " + rs.getString("propietario"));
                System.out.println("Dirección: " + rs.getString("calle") + " #" + rs.getString("numero") + ", " + rs.getString("ciudad"));

                // Mostrar categorías del negocio
                String catSql = "SELECT c.nombre FROM categorias c " +
                              "JOIN negocio_categorias nc ON c.id = nc.id_categoria " +
                              "WHERE nc.id_negocio = ?";
                PreparedStatement catStmt = conn.prepareStatement(catSql);
                catStmt.setInt(1, rs.getInt("id"));
                ResultSet catRs = catStmt.executeQuery();
                System.out.println("Categorías:");
                while (catRs.next()) {
                    System.out.println("- " + catRs.getString("nombre"));
                }
                System.out.println("-----------------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error al listar negocios: " + e.getMessage());
        }
    }

    private static void realizarReserva(Scanner sc) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si hay clientes
            String countClientesSql = "SELECT COUNT(*) FROM usuarios WHERE tipo_usuario = 'Cliente'";
            PreparedStatement countStmt = conn.prepareStatement(countClientesSql);
            ResultSet rs = countStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Primero debe registrar un Cliente.");
                return;
            }

            // Verificar si hay negocios
            String countNegociosSql = "SELECT COUNT(*) FROM negocios";
            countStmt = conn.prepareStatement(countNegociosSql);
            rs = countStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("No hay negocios disponibles para reservar.");
                return;
            }

            // Seleccionar cliente
            System.out.println("Clientes disponibles:");
            String clientesSql = "SELECT id, nombre FROM usuarios WHERE tipo_usuario = 'Cliente'";
            PreparedStatement clientesStmt = conn.prepareStatement(clientesSql);
            rs = clientesStmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("nombre"));
            }
            System.out.print("Seleccione el cliente por ID: ");
            int idCliente;
            try {
                idCliente = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada no numérica. Intente nuevamente.");
                return;
            }

            // Verificar si el cliente existe
            String checkClienteSql = "SELECT COUNT(*) FROM usuarios WHERE id = ? AND tipo_usuario = 'Cliente'";
            PreparedStatement checkClienteStmt = conn.prepareStatement(checkClienteSql);
            checkClienteStmt.setInt(1, idCliente);
            rs = checkClienteStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Cliente no válido.");
                return;
            }

            // Seleccionar negocio
            System.out.println("Negocios disponibles:");
            String negociosSql = "SELECT id, nombre FROM negocios";
            PreparedStatement negociosStmt = conn.prepareStatement(negociosSql);
            rs = negociosStmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("nombre"));
            }
            System.out.print("Seleccione el negocio por ID: ");
            int idNegocio;
            try {
                idNegocio = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada no numérica. Intente nuevamente.");
                return;
            }

            // Verificar si el negocio existe
            String checkNegocioSql = "SELECT COUNT(*) FROM negocios WHERE id = ?";
            PreparedStatement checkNegocioStmt = conn.prepareStatement(checkNegocioSql);
            checkNegocioStmt.setInt(1, idNegocio);
            rs = checkNegocioStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Negocio no válido.");
                return;
            }

            // Crear la reserva
            String insertReservaSql = "INSERT INTO reservas (id_cliente, id_negocio) VALUES (?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertReservaSql);
            insertStmt.setInt(1, idCliente);
            insertStmt.setInt(2, idNegocio);
            insertStmt.executeUpdate();
            System.out.println("Reserva realizada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al realizar reserva: " + e.getMessage());
        }
    }

    private static void listarReservasCliente(Scanner sc) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si hay clientes
            String countClientesSql = "SELECT COUNT(*) FROM usuarios WHERE tipo_usuario = 'Cliente'";
            PreparedStatement countStmt = conn.prepareStatement(countClientesSql);
            ResultSet rs = countStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("No hay clientes registrados.");
                return;
            }

            System.out.println("Clientes disponibles:");
            String clientesSql = "SELECT id, nombre FROM usuarios WHERE tipo_usuario = 'Cliente'";
            PreparedStatement clientesStmt = conn.prepareStatement(clientesSql);
            rs = clientesStmt.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("nombre"));
            }
            System.out.print("Seleccione el cliente por ID: ");
            int idCliente;
            try {
                idCliente = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada no numérica. Intente nuevamente.");
                return;
            }

            // Verificar si el cliente existe
            String checkClienteSql = "SELECT nombre FROM usuarios WHERE id = ? AND tipo_usuario = 'Cliente'";
            PreparedStatement checkClienteStmt = conn.prepareStatement(checkClienteSql);
            checkClienteStmt.setInt(1, idCliente);
            rs = checkClienteStmt.executeQuery();
            if (!rs.next()) {
                System.out.println("Cliente no válido.");
                return;
            }
            String nombreCliente = rs.getString("nombre");

            // Listar reservas
            String reservasSql = "SELECT r.id, n.nombre AS nombre_negocio, r.fecha_reserva " +
                                "FROM reservas r " +
                                "JOIN negocios n ON r.id_negocio = n.id " +
                                "WHERE r.id_cliente = ?";
            PreparedStatement reservasStmt = conn.prepareStatement(reservasSql);
            reservasStmt.setInt(1, idCliente);
            rs = reservasStmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("El cliente " + nombreCliente + " no tiene reservas.");
            } else {
                System.out.println("Reservas del cliente " + nombreCliente + ":");
                while (rs.next()) {
                    System.out.println("- Reserva en " + rs.getString("nombre_negocio") + " (ID: " + rs.getInt("id") + ") el " + rs.getTimestamp("fecha_reserva"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar reservas: " + e.getMessage());
        }
    }
}