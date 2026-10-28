import java.sql.*;

public class UpdateDb {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:test_server/plugins/BruhClans/database.db")) {
            String payload = null;
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT PAYLOAD FROM BRUHCLANS_V5")) {
                    if (rs.next()) {
                        payload = rs.getString("PAYLOAD");
                    }
                }
            }
            if (payload != null) {
                payload = payload.replace("level: 1", "level: 0");
                try (PreparedStatement ps = conn.prepareStatement("UPDATE BRUHCLANS_V5 SET PAYLOAD = ? WHERE ID = 1")) {
                    ps.setString(1, payload);
                    ps.executeUpdate();
                    System.out.println("Updated database.db clan level to 0!");
                }
            }
        }
    }
}
