package kg.kadyr;
import kg.kadyr.classes.Session;
import kg.kadyr.menu.UserMenu;
import kg.kadyr.utils.Sql;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;


public class Main {
    public static Session session;
    public static JSONObject config;


    public static void main(String[] args) throws SQLException, IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader("src/resources/config.json");
        config = (JSONObject) jsonParser.parse(reader);

        Connection connection = Sql.connect((String) config.get("databaseName"));
        session = new Session(connection);
        UserMenu.main();
    }
}
