public class Config {
    public static final String username = "root";
    public static final String password = "notSoC0y";

    public static final String hostname = "localhost";
    public static final String database = "test";
    public static final String table = "testTable";

    public static final String filepath = "C:\\Users\\Jonas\\Desktop\\tekstfil.txt";
    public static final String filelineSplit = "/";

    private Config() {
        throw new AssertionError("This class cannot be instantiated");
    }
}
