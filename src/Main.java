import java.sql.SQLException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try (DBHandler dbHandler = new DBHandler(Config.username, Config.password)) {
            dbHandler.copyFile(Config.filepath, Config.table);
            TableObject tableObject = dbHandler.getTable(Config.table);

            int fieldsLength = tableObject.getHeaderNames().length;

            for (int i = 0; i < fieldsLength; i++) {
                System.out.printf("%s %s:%-10s",
                        tableObject.getHeaderNames()[i],
                        tableObject.getHeaderTypes()[i],
                        tableObject.getHeaderWidths()[i]);
            }

            System.out.println();
            for (String[] strings : tableObject.getRowData()) {
                Arrays.asList(strings).forEach(item -> System.out.printf("%-25s", item));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
