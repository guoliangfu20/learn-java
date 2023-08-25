import java.io.Console;
import java.sql.*;
import java.util.*;

public class ReadAccessDemo {
    public static void main(String[] args) {
        try {
            List<Map<String, Object>> result = resolverMdb("src/main/Db.mdb");
            System.out.println(result);
        } catch (Exception exception) {
            exception.printStackTrace();

        }
    }

    static Connection connection = null;
    static ResultSet tables = null;
    static PreparedStatement preparedStatement = null;
    static ResultSet rs = null;

    public static List<Map<String, Object>> resolverMdb(String mdbPath) throws Exception {
        List<Map<String, Object>> lstMdb = new ArrayList<>();
        Properties prop = new Properties();
        prop.put("charSet", "UTF-8");

        String mdbUrl = "jdbc:ucanaccess://" + mdbPath;

        // 引入驱动
        Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();

        try {
            connection = DriverManager.getConnection(mdbUrl);
            tables = connection.getMetaData().getTables(mdbPath, null, null, new String[]{"TABLE"});

            while (tables.next()) {
                Map<String, Object> tableMap = new HashMap<>(16);
                Set<String> columnList = new HashSet<>();
                List<Map<String, String>> dataList = new ArrayList<>();
                String tableName = tables.getString(3);
                preparedStatement = connection.prepareStatement("select * from " + tableName);
                rs = preparedStatement.executeQuery();
                ResultSetMetaData data = rs.getMetaData();

                while (rs.next()) {
                    Map<String, String> map = new HashMap<>();
                    for (int i = 1; i <= data.getColumnCount(); i++) {
                        //列名
                        String columnName = data.getColumnName(i);
                        map.put(columnName, rs.getString(i));
                        columnList.add(columnName);
                    }
                    dataList.add(map);
                }
                tableMap.put("name", tableName);
                tableMap.put("column", columnList);
                tableMap.put("data", dataList);
                lstMdb.add(tableMap);
            }
        } catch (Exception exception) {
            System.out.println(exception);
        } finally {
            closeALl();
        }
        return lstMdb;
    }

    public static void closeALl() {

        try {
            if (null != rs) {
                rs.close();
            }
            if (null != tables) {
                tables.close();
            }
            if (null != preparedStatement) {
                preparedStatement.close();
            }
            if (null != connection) {
                connection.close();
            }
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }
}
