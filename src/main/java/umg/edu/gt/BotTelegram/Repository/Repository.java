package umg.edu.gt.BotTelegram.Repository;
//autor @Kenneth//

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import umg.edu.gt.BotTelegram.Model.Client;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Repository {
    private static final Logger log = LoggerFactory.getLogger(Repository.class);
    private static final String TABLENAME = "clients";
    private static DataSource dataSource;

    @Autowired  // Inyección automática del DataSource gestionado por Spring Boot
    public Repository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    // metodo para agregar un nuevo cliente
    public static void add(String name, Long chat_id) throws SQLException {
        String sql = "INSERT INTO " + TABLENAME + " (name, chat_id) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);          // Vincula el nombre
            stmt.setLong(2, chat_id);         // Vincula el chat_id como Long
            stmt.executeUpdate();             // Ejecuta la consulta de inserción
        }
    }
    // metodo para obtener un cliente por ID
    public static Client getById(Long chat_id) throws SQLException {
        String sql = "SELECT * FROM " + TABLENAME + " WHERE chat_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setLong(1, chat_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Client(rs.getInt("client"), rs.getString("name"));
            }
        }
        log.warn("client not found with  Chat ID = {}",chat_id);
        return null;  // Si no se encuentra el cliente
    }
}
