package com.reservas.app.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainWindow extends JFrame {
    public MainWindow() {
        setTitle("Sistema de Reservas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnRegistrarAdmin = new JButton("Registrar Administrador");
        JButton btnRegistrarCliente = new JButton("Registrar Cliente");
        JButton btnCrearNegocio = new JButton("Crear Negocio");
        JButton btnListarNegocios = new JButton("Listar Negocios");
        JButton btnRealizarReserva = new JButton("Realizar Reserva");
        JButton btnListarReservas = new JButton("Listar Reservas");
        JButton btnSalir = new JButton("Salir");


        btnRegistrarAdmin.addActionListener(e -> registrarAdmin());
        btnRegistrarCliente.addActionListener(e -> registrarCliente());
        btnCrearNegocio.addActionListener(e -> crearNegocio());
        btnListarNegocios.addActionListener(e -> listarNegocios());
        btnRealizarReserva.addActionListener(e -> realizarReserva());
        btnListarReservas.addActionListener(e -> listarReservas());
        btnSalir.addActionListener(e -> {
            DatabaseConnection.closeConnection();
            System.exit(0);
        });


        panel.add(btnRegistrarAdmin);
        panel.add(btnRegistrarCliente);
        panel.add(btnCrearNegocio);
        panel.add(btnListarNegocios);
        panel.add(btnRealizarReserva);
        panel.add(btnListarReservas);
        panel.add(btnSalir);

        add(panel);
        setVisible(true);
    }

    private void registrarAdmin() {
        JDialog dialog = new JDialog(this, "Registrar Administrador", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));
        dialog.setLocationRelativeTo(this);

        JLabel lblNombre = new JLabel("Nombre:");
        JTextField txtNombre = new JTextField();
        JLabel lblCorreo = new JLabel("Correo:");
        JTextField txtCorreo = new JTextField();
        JLabel lblContrasena = new JLabel("Contraseña:");
        JPasswordField txtContrasena = new JPasswordField();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String contrasena = new String(txtContrasena.getPassword());

            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios.");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO usuarios (nombre, correo, contrasena, tipo_usuario) VALUES (?, ?, ?, 'Administrador')";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nombre);
                stmt.setString(2, correo);
                stmt.setString(3, contrasena);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Administrador registrado exitosamente.");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error al registrar administrador: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.add(lblNombre);
        dialog.add(txtNombre);
        dialog.add(lblCorreo);
        dialog.add(txtCorreo);
        dialog.add(lblContrasena);
        dialog.add(txtContrasena);
        dialog.add(btnGuardar);
        dialog.add(btnCancelar);

        dialog.setVisible(true);
    }

    private void registrarCliente() {
        JDialog dialog = new JDialog(this, "Registrar Cliente", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(4, 2, 5, 5));
        dialog.setLocationRelativeTo(this);

        JLabel lblNombre = new JLabel("Nombre:");
        JTextField txtNombre = new JTextField();
        JLabel lblCorreo = new JLabel("Correo:");
        JTextField txtCorreo = new JTextField();
        JLabel lblContrasena = new JLabel("Contraseña:");
        JPasswordField txtContrasena = new JPasswordField();
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String contrasena = new String(txtContrasena.getPassword());

            if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios.");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO usuarios (nombre, correo, contrasena, tipo_usuario) VALUES (?, ?, ?, 'Cliente')";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nombre);
                stmt.setString(2, correo);
                stmt.setString(3, contrasena);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Cliente registrado exitosamente.");
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error al registrar cliente: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.add(lblNombre);
        dialog.add(txtNombre);
        dialog.add(lblCorreo);
        dialog.add(txtCorreo);
        dialog.add(lblContrasena);
        dialog.add(txtContrasena);
        dialog.add(btnGuardar);
        dialog.add(btnCancelar);

        dialog.setVisible(true);
    }

    private void crearNegocio() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si hay administradores
            String countAdminsSql = "SELECT COUNT(*) FROM usuarios WHERE tipo_usuario = 'Administrador'";
            PreparedStatement countStmt = conn.prepareStatement(countAdminsSql);
            ResultSet countRs = countStmt.executeQuery();
            countRs.next();
            if (countRs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "Primero debe registrar un Administrador.");
                return;
            }

            JDialog dialog = new JDialog(this, "Crear Negocio", true);
            dialog.setSize(400, 400);
            dialog.setLayout(new GridLayout(0, 2, 5, 5));
            dialog.setLocationRelativeTo(this);

            JLabel lblAdmin = new JLabel("Administrador:");
            JComboBox<String> cbAdmins = new JComboBox<>();
            JLabel lblNombre = new JLabel("Nombre del Negocio:");
            JTextField txtNombre = new JTextField();
            JLabel lblCiudad = new JLabel("Ciudad:");
            JTextField txtCiudad = new JTextField();
            JLabel lblCalle = new JLabel("Calle:");
            JTextField txtCalle = new JTextField();
            JLabel lblNumero = new JLabel("Número:");
            JTextField txtNumero = new JTextField();
            JLabel lblCategorias = new JLabel("Categorías:");
            DefaultListModel<String> catModel = new DefaultListModel<>();
            JList<String> lstCategorias = new JList<>(catModel);
            JScrollPane catScrollPane = new JScrollPane(lstCategorias);
            JButton btnAgregarCat = new JButton("Agregar Categoría");
            JButton btnGuardar = new JButton("Guardar");
            JButton btnCancelar = new JButton("Cancelar");

            // Cargar administradores
            String adminsSql = "SELECT id, nombre FROM usuarios WHERE tipo_usuario = 'Administrador'";
            PreparedStatement adminsStmt = conn.prepareStatement(adminsSql);
            ResultSet adminsRs = adminsStmt.executeQuery();
            while (adminsRs.next()) {
                cbAdmins.addItem(adminsRs.getInt("id") + ": " + adminsRs.getString("nombre"));
            }

            // Cargar categorías disponibles
            String catsSql = "SELECT id, nombre FROM categorias";
            PreparedStatement catsStmt = conn.prepareStatement(catsSql);
            ResultSet catsRs = catsStmt.executeQuery();
            DefaultListModel<String> availableCats = new DefaultListModel<>();
            while (catsRs.next()) {
                availableCats.addElement(catsRs.getInt("id") + ": " + catsRs.getString("nombre"));
            }

            btnAgregarCat.addActionListener(e -> {
                String selectedCat = (String) JOptionPane.showInputDialog(
                    dialog,
                    "Seleccione una categoría:",
                    "Agregar Categoría",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    availableCats.toArray(),
                    null
                );
                if (selectedCat != null && !catModel.contains(selectedCat)) {
                    catModel.addElement(selectedCat);
                }
            });

            btnGuardar.addActionListener(e -> {
                String adminSelection = (String) cbAdmins.getSelectedItem();
                int idAdmin = Integer.parseInt(adminSelection.split(":")[0].trim());
                String nombre = txtNombre.getText();
                String ciudad = txtCiudad.getText();
                String calle = txtCalle.getText();
                String numero = txtNumero.getText();

                if (nombre.isEmpty() || ciudad.isEmpty() || calle.isEmpty() || numero.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Todos los campos son obligatorios.");
                    return;
                }

                try (Connection innerConn = DatabaseConnection.getConnection()) {
                    // Insertar dirección
                    String insertDirSql = "INSERT INTO direcciones (ciudad, calle, numero) VALUES (?, ?, ?)";
                    PreparedStatement dirStmt = innerConn.prepareStatement(insertDirSql, PreparedStatement.RETURN_GENERATED_KEYS);
                    dirStmt.setString(1, ciudad);
                    dirStmt.setString(2, calle);
                    dirStmt.setString(3, numero);
                    dirStmt.executeUpdate();
                    ResultSet dirRs = dirStmt.getGeneratedKeys();
                    dirRs.next();
                    int idDireccion = dirRs.getInt(1);

                    // Insertar negocio
                    String insertNegocioSql = "INSERT INTO negocios (nombre, id_propietario, id_direccion) VALUES (?, ?, ?)";
                    PreparedStatement negocioStmt = innerConn.prepareStatement(insertNegocioSql, PreparedStatement.RETURN_GENERATED_KEYS);
                    negocioStmt.setString(1, nombre);
                    negocioStmt.setInt(2, idAdmin);
                    negocioStmt.setInt(3, idDireccion);
                    negocioStmt.executeUpdate();
                    ResultSet negocioRs = negocioStmt.getGeneratedKeys();
                    negocioRs.next();
                    int idNegocio = negocioRs.getInt(1);

                    // Insertar categorías seleccionadas
                    for (int i = 0; i < catModel.size(); i++) {
                        String cat = catModel.get(i);
                        int catId = Integer.parseInt(cat.split(":")[0].trim());
                        String insertCatNegSql = "INSERT INTO negocio_categorias (id_negocio, id_categoria) VALUES (?, ?)";
                        PreparedStatement insertCatNegStmt = innerConn.prepareStatement(insertCatNegSql);
                        insertCatNegStmt.setInt(1, idNegocio);
                        insertCatNegStmt.setInt(2, catId);
                        insertCatNegStmt.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(dialog, "Negocio creado exitosamente.");
                    dialog.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al crear negocio: " + ex.getMessage());
                }
            });

            btnCancelar.addActionListener(e -> dialog.dispose());

            dialog.add(lblAdmin);
            dialog.add(cbAdmins);
            dialog.add(lblNombre);
            dialog.add(txtNombre);
            dialog.add(lblCiudad);
            dialog.add(txtCiudad);
            dialog.add(lblCalle);
            dialog.add(txtCalle);
            dialog.add(lblNumero);
            dialog.add(txtNumero);
            dialog.add(lblCategorias);
            dialog.add(catScrollPane);
            dialog.add(btnAgregarCat);
            dialog.add(new JLabel());
            dialog.add(btnGuardar);
            dialog.add(btnCancelar);

            dialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    private void listarNegocios() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT n.id, n.nombre, u.nombre AS propietario, d.ciudad, d.calle, d.numero " +
                        "FROM negocios n " +
                        "JOIN usuarios u ON n.id_propietario = u.id " +
                        "JOIN direcciones d ON n.id_direccion = d.id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            if (!rs.isBeforeFirst()) {
                textArea.append("No hay negocios registrados.\n");
            } else {
                textArea.append("---- Lista de Negocios ----\n");
                while (rs.next()) {
                    textArea.append("Negocio: " + rs.getString("nombre") + "\n");
                    textArea.append("Propietario: " + rs.getString("propietario") + "\n");
                    textArea.append("Dirección: " + rs.getString("calle") + " #" + rs.getString("numero") + ", " + rs.getString("ciudad") + "\n");

                    String catSql = "SELECT c.nombre FROM categorias c " +
                                  "JOIN negocio_categorias nc ON c.id = nc.id_categoria " +
                                  "WHERE nc.id_negocio = ?";
                    PreparedStatement catStmt = conn.prepareStatement(catSql);
                    catStmt.setInt(1, rs.getInt("id"));
                    ResultSet catRs = catStmt.executeQuery();
                    textArea.append("Categorías:\n");
                    while (catRs.next()) {
                        textArea.append("- " + catRs.getString("nombre") + "\n");
                    }
                    textArea.append("-----------------------------\n");
                }
            }

            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Lista de Negocios", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al listar negocios: " + e.getMessage());
        }
    }

    private void realizarReserva() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si hay clientes
            String countClientesSql = "SELECT COUNT(*) FROM usuarios WHERE tipo_usuario = 'Cliente'";
            PreparedStatement countStmt = conn.prepareStatement(countClientesSql);
            ResultSet countRs = countStmt.executeQuery();
            countRs.next();
            if (countRs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "Primero debe registrar un Cliente.");
                return;
            }

            // Verificar si hay negocios
            String countNegociosSql = "SELECT COUNT(*) FROM negocios";
            PreparedStatement countNegociosStmt = conn.prepareStatement(countNegociosSql);
            ResultSet countNegociosRs = countNegociosStmt.executeQuery();
            countNegociosRs.next();
            if (countNegociosRs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "No hay negocios disponibles para reservar.");
                return;
            }

            JDialog dialog = new JDialog(this, "Realizar Reserva", true);
            dialog.setSize(300, 150);
            dialog.setLayout(new GridLayout(3, 2, 5, 5));
            dialog.setLocationRelativeTo(this);

            JLabel lblCliente = new JLabel("Cliente:");
            JComboBox<String> cbClientes = new JComboBox<>();
            JLabel lblNegocio = new JLabel("Negocio:");
            JComboBox<String> cbNegocios = new JComboBox<>();
            JButton btnGuardar = new JButton("Guardar");
            JButton btnCancelar = new JButton("Cancelar");

            // Cargar clientes
            String clientesSql = "SELECT id, nombre FROM usuarios WHERE tipo_usuario = 'Cliente'";
            PreparedStatement clientesStmt = conn.prepareStatement(clientesSql);
            ResultSet clientesRs = clientesStmt.executeQuery();
            while (clientesRs.next()) {
                cbClientes.addItem(clientesRs.getInt("id") + ": " + clientesRs.getString("nombre"));
            }

            // Cargar negocios
            String negociosSql = "SELECT id, nombre FROM negocios";
            PreparedStatement negociosStmt = conn.prepareStatement(negociosSql);
            ResultSet negociosRs = negociosStmt.executeQuery();
            while (negociosRs.next()) {
                cbNegocios.addItem(negociosRs.getInt("id") + ": " + negociosRs.getString("nombre"));
            }

            btnGuardar.addActionListener(e -> {
                String clienteSelection = (String) cbClientes.getSelectedItem();
                String negocioSelection = (String) cbNegocios.getSelectedItem();
                int idCliente = Integer.parseInt(clienteSelection.split(":")[0].trim());
                int idNegocio = Integer.parseInt(negocioSelection.split(":")[0].trim());

                try (Connection innerConn = DatabaseConnection.getConnection()) {
                    String insertReservaSql = "INSERT INTO reservas (id_cliente, id_negocio) VALUES (?, ?)";
                    PreparedStatement insertStmt = innerConn.prepareStatement(insertReservaSql);
                    insertStmt.setInt(1, idCliente);
                    insertStmt.setInt(2, idNegocio);
                    insertStmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Reserva realizada exitosamente.");
                    dialog.dispose();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al realizar reserva: " + ex.getMessage());
                }
            });

            btnCancelar.addActionListener(e -> dialog.dispose());

            dialog.add(lblCliente);
            dialog.add(cbClientes);
            dialog.add(lblNegocio);
            dialog.add(cbNegocios);
            dialog.add(btnGuardar);
            dialog.add(btnCancelar);

            dialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    private void listarReservas() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si hay clientes
            String countClientesSql = "SELECT COUNT(*) FROM usuarios WHERE tipo_usuario = 'Cliente'";
            PreparedStatement countStmt = conn.prepareStatement(countClientesSql);
            ResultSet countRs = countStmt.executeQuery();
            countRs.next();
            if (countRs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "No hay clientes registrados.");
                return;
            }

            JDialog dialog = new JDialog(this, "Listar Reservas", true);
            dialog.setSize(300, 150);
            dialog.setLayout(new GridLayout(2, 2, 5, 5));
            dialog.setLocationRelativeTo(this);

            JLabel lblCliente = new JLabel("Cliente:");
            JComboBox<String> cbClientes = new JComboBox<>();
            JButton btnMostrar = new JButton("Mostrar Reservas");
            JButton btnCancelar = new JButton("Cancelar");

            // Cargar clientes
            String clientesSql = "SELECT id, nombre FROM usuarios WHERE tipo_usuario = 'Cliente'";
            PreparedStatement clientesStmt = conn.prepareStatement(clientesSql);
            ResultSet clientesRs = clientesStmt.executeQuery();
            while (clientesRs.next()) {
                cbClientes.addItem(clientesRs.getInt("id") + ": " + clientesRs.getString("nombre"));
            }

            btnMostrar.addActionListener(e -> {
                String clienteSelection = (String) cbClientes.getSelectedItem();
                int idCliente = Integer.parseInt(clienteSelection.split(":")[0].trim());
                String nombreCliente = clienteSelection.split(":")[1].trim();

                try (Connection innerConn = DatabaseConnection.getConnection()) {
                    String reservasSql = "SELECT r.id, n.nombre AS nombre_negocio, r.fecha_reserva " +
                                       "FROM reservas r " +
                                       "JOIN negocios n ON r.id_negocio = n.id " +
                                       "WHERE r.id_cliente = ?";
                    PreparedStatement reservasStmt = innerConn.prepareStatement(reservasSql);
                    reservasStmt.setInt(1, idCliente);
                    ResultSet reservasRs = reservasStmt.executeQuery();

                    JTextArea textArea = new JTextArea();
                    textArea.setEditable(false);
                    if (!reservasRs.isBeforeFirst()) {
                        textArea.append("El cliente " + nombreCliente + " no tiene reservas.\n");
                    } else {
                        textArea.append("Reservas del cliente " + nombreCliente + ":\n");
                        while (reservasRs.next()) {
                            textArea.append("- Reserva en " + reservasRs.getString("nombre_negocio") + 
                                           " (ID: " + reservasRs.getInt("id") + ") el " + 
                                           reservasRs.getTimestamp("fecha_reserva") + "\n");
                        }
                    }
                    JOptionPane.showMessageDialog(dialog, new JScrollPane(textArea), 
                                                "Reservas", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al listar reservas: " + ex.getMessage());
                }
            });

            btnCancelar.addActionListener(e -> dialog.dispose());

            dialog.add(lblCliente);
            dialog.add(cbClientes);
            dialog.add(btnMostrar);
            dialog.add(btnCancelar);

            dialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}