import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestionRecetas {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;
        do {
            System.out.println("Gestor de recetas");
            System.out.println("1.Mostrar todas las recetas almacenadas");
            System.out.println("2.Buscar una receta mediante su identificador");
            System.out.println("3.Añadir nueva receta");
            System.out.println("4.Modificar el tiempo de preparación y el número de raciones de una receta");
            System.out.println("5.Eliminar una receta del catálogo");
            System.out.println("6.Buscar receta por nombre");
            System.out.println("7.Salir");
            System.out.print("Elige una opción: ");
            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
                switch (opcion) {
                    case 1:
                        mostrarRecetas();
                        break;
                    case 2:
                        System.out.print("Introduce el ID de la receta: ");
                        int idBuscar = Integer.parseInt(scanner.nextLine().trim());
                        buscarRecetaPorId(idBuscar);
                        break;
                    case 3:
                        System.out.print("Nombre de la receta: ");
                        String nombre = scanner.nextLine().trim();
                        System.out.print("Categoría: ");
                        String categoria = scanner.nextLine().trim();
                        System.out.print("Tiempo de preparación: ");
                        int tiempo = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Dificultad: ");
                        String dificultad = scanner.nextLine().trim();
                        System.out.print("Número de raciones: ");
                        int raciones = Integer.parseInt(scanner.nextLine().trim());
                        anadirReceta(new Receta(nombre, categoria, tiempo, dificultad, raciones));
                        break;
                    case 4:
                        System.out.print("Introduce el ID de la receta a modificar: ");
                        int idMod = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Nuevo tiempo de preparación: ");
                        int nuevoTiempo = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Nuevo número de raciones: ");
                        int nuevasRaciones = Integer.parseInt(scanner.nextLine().trim());
                        modificarReceta(idMod, nuevoTiempo, nuevasRaciones);
                        break;
                    case 5:
                        System.out.print("Introduce el ID de la receta a eliminar: ");
                        int idEliminar = Integer.parseInt(scanner.nextLine().trim());
                        eliminarReceta(idEliminar);
                        break;
                    case 6:
                        System.out.print("Introduce el nombre de la receta a buscar: ");
                        String nombreBuscar = scanner.nextLine().trim();
                        buscarRecetaPorNombre(nombreBuscar);
                        break;
                    case 7:
                        System.out.println("Saliendo de la aplicación");
                        break;
                    default:
                        System.out.println("Opción no válida");
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número válido");
            } catch (Exception e) {
                System.out.println("Error durante la ejecución: " + e.getMessage());
            }
        } while (opcion != 7);
        scanner.close();
    }
    private static List<Receta> obtenerListaRecetas() {
        List<Receta> recetas = new ArrayList<>();
        String sql = "SELECT id, nombre, categoria, tiempo_preparacion, dificultad, raciones " +
                "FROM recetas " +
                "ORDER BY id";
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                recetas.add(new Receta(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("categoria"),
                        rs.getInt("tiempo_preparacion"),
                        rs.getString("dificultad"),
                        rs.getInt("raciones")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar las recetas: " + e.getMessage());
        }
        return recetas;
    }
    private static void mostrarRecetas() {
        List<Receta> recetas = obtenerListaRecetas();
        if (recetas.isEmpty()) {
            System.out.println("No hay recetas registradas");
        } else {
            recetas.forEach(System.out::println);
        }
    }
    private static void buscarRecetaPorId(int id) {
        String sql = "SELECT id, nombre, categoria, tiempo_preparacion, dificultad, raciones " +
                "FROM recetas " +
                "WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Receta r = new Receta(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("categoria"),
                            rs.getInt("tiempo_preparacion"),
                            rs.getString("dificultad"),
                            rs.getInt("raciones")
                    );
                    System.out.println("Receta encontrada: " + r);
                } else {
                    System.out.println("No se encontró ninguna receta con el ID " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar la receta: " + e.getMessage());
        }
    }
    private static void anadirReceta(Receta nuevaReceta) {
        String sql = "INSERT INTO recetas (nombre, categoria, tiempo_preparacion, dificultad, raciones) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevaReceta.getNombre());
            pstmt.setString(2, nuevaReceta.getCategoria());
            pstmt.setInt(3, nuevaReceta.getTiempoPreparacion());
            pstmt.setString(4, nuevaReceta.getDificultad());
            pstmt.setInt(5, nuevaReceta.getRaciones());
            pstmt.executeUpdate();
            System.out.println("Receta añadida correctamente");
        } catch (SQLException e) {
            System.out.println("Error al añadir la receta: " + e.getMessage());
        }
    }
    private static void modificarReceta(int id, int nuevoTiempo, int nuevasRaciones) {
        String sql = "UPDATE recetas SET tiempo_preparacion = ?, raciones = ? " +
                "WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nuevoTiempo);
            pstmt.setInt(2, nuevasRaciones);
            pstmt.setInt(3, id);
            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                System.out.println("Tiempo de preparación y raciones modificados correctamente");
            } else {
                System.out.println("Receta con ID " + id + " no encontrada");
            }
        } catch (SQLException e) {
            System.out.println("Error al modificar la receta: " + e.getMessage());
        }
    }
    private static void eliminarReceta(int id) {
        String sql = "DELETE FROM recetas " +
                "WHERE id = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                System.out.println("Receta eliminada correctamente");
            } else {
                System.out.println("No se encontró ninguna receta con el ID " + id);
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar la receta: " + e.getMessage());
        }
    }
    private static void buscarRecetaPorNombre(String nombre) {
        String sql = "{CALL buscarReceta(?)}";
        try (Connection conn = ConexionBD.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, nombre);
            try (ResultSet rs = cstmt.executeQuery()) {
                boolean encontrada = false;
                while (rs.next()) {
                    encontrada = true;
                    Receta r = new Receta(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("categoria"),
                            rs.getInt("tiempo_preparacion"),
                            rs.getString("dificultad"),
                            rs.getInt("raciones")
                    );
                    System.out.println("Receta encontrada: " + r);
                }
                if (!encontrada) {
                    System.out.println("No se encontró ninguna receta con el nombre " + nombre);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar la receta por nombre: " + e.getMessage());
        }
    }
}