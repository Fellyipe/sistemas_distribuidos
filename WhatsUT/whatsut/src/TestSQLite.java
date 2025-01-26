import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class TestSQLite {
    public static void main(String[] args) {
        // URL do banco de dados SQLite
        String url = "jdbc:sqlite:teste.db";

        // Conectar ao banco de dados
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Conexão estabelecida com sucesso!");

                // Criar uma tabela de exemplo
                String sql = "CREATE TABLE IF NOT EXISTS usuarios (\n"
                        + " id integer PRIMARY KEY,\n"
                        + " nome text NOT NULL\n"
                        + ");";
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
                System.out.println("Tabela criada com sucesso!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
