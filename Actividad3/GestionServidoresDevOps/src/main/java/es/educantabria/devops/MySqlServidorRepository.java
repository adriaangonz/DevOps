package es.educantabria.devops;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC sobre MySQL 8.0 para persistir la colección de servidores.
 */
public class MySqlServidorRepository implements ServidorRepository {

    private final String url;
    private final String user;
    private final String password;

    public MySqlServidorRepository() {
        String host = envOrDefault("DB_HOST", "db");
        String port = envOrDefault("DB_PORT", "3306");
        String dbName = envOrDefault("DB_NAME", "devops_servidores");
        this.user = envOrDefault("DB_USER", "devops");
        this.password = envOrDefault("DB_PASSWORD", "devopspassword");
        this.url = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, dbName
        );
        inicializarBaseDeDatos();
    }

    private static String envOrDefault(String key, String fallback) {
        String val = System.getenv(key);
        return (val != null && !val.isBlank()) ? val : fallback;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private void inicializarBaseDeDatos() {
        int intentos = 0;
        int maxIntentos = 10;
        while (true) {
            try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
                st.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS servidores (" +
                        "id INT PRIMARY KEY, " +
                        "nombre VARCHAR(100) NOT NULL, " +
                        "ip VARCHAR(45) NOT NULL, " +
                        "estado VARCHAR(30) NOT NULL)"
                );
                if (contarInternal(conn) == 0) {
                    precargarDatos(conn);
                }
                return;
            } catch (SQLException e) {
                intentos++;
                if (intentos >= maxIntentos) {
                    throw new RuntimeException("No se pudo conectar con MySQL tras " + maxIntentos + " intentos: " + e.getMessage(), e);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Espera de conexión MySQL interrumpida", ie);
                }
            }
        }
    }

    private int contarInternal(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM servidores")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void precargarDatos(Connection conn) throws SQLException {
        String sql = "INSERT INTO servidores (id, nombre, ip, estado) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            insertarFila(ps, 1, "srv-web-nginx", "192.168.10.10", "ACTIVO");
            insertarFila(ps, 2, "srv-bd-postgres", "192.168.10.20", "ACTIVO");
            insertarFila(ps, 3, "srv-ci-jenkins", "192.168.10.30", "MANTENIMIENTO");
        }
    }

    private void insertarFila(PreparedStatement ps, int id, String nombre, String ip, String estado) throws SQLException {
        ps.setInt(1, id);
        ps.setString(2, nombre);
        ps.setString(3, ip);
        ps.setString(4, estado);
        ps.executeUpdate();
    }

    @Override
    public List<Servidor> listarTodos() {
        List<Servidor> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, ip, estado FROM servidores ORDER BY id ASC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Servidor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("ip"),
                        rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar servidores en MySQL: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public Optional<Servidor> buscarPorId(int id) {
        String sql = "SELECT id, nombre, ip, estado FROM servidores WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Servidor(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("ip"),
                            rs.getString("estado")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar servidor en MySQL: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public boolean agregar(Servidor servidor) {
        if (servidor == null || servidor.getId() <= 0 || buscarPorId(servidor.getId()).isPresent()) {
            return false;
        }
        String sql = "INSERT INTO servidores (id, nombre, ip, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, servidor.getId());
            ps.setString(2, servidor.getNombre());
            ps.setString(3, servidor.getIp());
            ps.setString(4, servidor.getEstado());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean actualizar(int id, String nuevoNombre, String nuevoEstado) {
        Optional<Servidor> opt = buscarPorId(id);
        if (opt.isEmpty()) {
            return false;
        }
        Servidor actual = opt.get();
        String nombreFinal = (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) ? nuevoNombre : actual.getNombre();
        String estadoFinal = (nuevoEstado != null && !nuevoEstado.trim().isEmpty()) ? nuevoEstado : actual.getEstado();

        String sql = "UPDATE servidores SET nombre = ?, estado = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreFinal);
            ps.setString(2, estadoFinal);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar servidor en MySQL: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM servidores WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar servidor en MySQL: " + e.getMessage(), e);
        }
    }

    @Override
    public int contar() {
        try (Connection conn = getConnection()) {
            return contarInternal(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error al contar servidores en MySQL: " + e.getMessage(), e);
        }
    }
}
