public class Main {
    public static void main(String[] args) {
        new DBHandler(Config.username, Config.password).copyFile(Config.filepath, Config.table);
    }
}
