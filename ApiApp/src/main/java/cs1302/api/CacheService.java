package cs1302.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Optional;

/**
 * Simple SQLite-backed cache for API responses and user data.
 */
public class CacheService {

    private final String jdbcUrl;

    public CacheService(String dbPath) {
        this.jdbcUrl = "jdbc:sqlite:" + dbPath;
        initialize();
    }

    private void initialize() {
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS dict_cache (" +
                    "word TEXT PRIMARY KEY, " +
                    "definition TEXT NOT NULL, " +
                    "updated_at INTEGER NOT NULL" +
                ")");
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS book_cache (" +
                    "query TEXT PRIMARY KEY, " +
                    "results TEXT NOT NULL, " +
                    "updated_at INTEGER NOT NULL" +
                ")");
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS favorites (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "type TEXT NOT NULL, " +
                    "value TEXT NOT NULL, " +
                    "created_at INTEGER NOT NULL" +
                ")");
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "type TEXT NOT NULL, " +
                    "value TEXT NOT NULL, " +
                    "created_at INTEGER NOT NULL" +
                ")");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize SQLite cache", e);
        }
    }

    public Optional<String> getCachedDefinition(String word) {
        final String sql = "SELECT definition FROM dict_cache WHERE word = ?";
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, word.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            // ignore cache errors
        }
        return Optional.empty();
    }

    public void putCachedDefinition(String word, String definition) {
        final String sql = "INSERT INTO dict_cache(word, definition, updated_at) VALUES(?,?,?) " +
            "ON CONFLICT(word) DO UPDATE SET definition=excluded.definition, updated_at=excluded.updated_at";
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, word.toLowerCase());
            ps.setString(2, definition);
            ps.setLong(3, Instant.now().getEpochSecond());
            ps.executeUpdate();
        } catch (SQLException e) {
            // ignore cache errors
        }
    }

    public Optional<String> getCachedBooks(String query) {
        final String sql = "SELECT results FROM book_cache WHERE query = ?";
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, query.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            // ignore cache errors
        }
        return Optional.empty();
    }

    public void putCachedBooks(String query, String json) {
        final String sql = "INSERT INTO book_cache(query, results, updated_at) VALUES(?,?,?) " +
            "ON CONFLICT(query) DO UPDATE SET results=excluded.results, updated_at=excluded.updated_at";
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, query.toLowerCase());
            ps.setString(2, json);
            ps.setLong(3, Instant.now().getEpochSecond());
            ps.executeUpdate();
        } catch (SQLException e) {
            // ignore cache errors
        }
    }

    public void addFavorite(String type, String value) {
        final String sql = "INSERT INTO favorites(type, value, created_at) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, value);
            ps.setLong(3, Instant.now().getEpochSecond());
            ps.executeUpdate();
        } catch (SQLException e) {
            // ignore
        }
    }

    public void addHistory(String type, String value) {
        final String sql = "INSERT INTO history(type, value, created_at) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, value);
            ps.setLong(3, Instant.now().getEpochSecond());
            ps.executeUpdate();
        } catch (SQLException e) {
            // ignore
        }
    }
}


