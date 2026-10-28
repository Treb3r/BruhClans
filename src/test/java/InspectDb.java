import java.sql.*;

public class InspectDb {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:test_server/plugins/BruhClans/database.db")) {
            try (Statement st = conn.createStatement()) {
                try (ResultSet rs = st.executeQuery("SELECT PAYLOAD FROM BRUHCLANS_V5")) {
                    while (rs.next()) {
                        System.out.println(rs.getString("PAYLOAD"));
                    }
                }
            }
        }
    }
}
