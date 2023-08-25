package com.space.utils;


import java.sql.*;
import java.util.*;

/**
 * 读取 mdb文件帮助类
 */
public class MdbHelper {

    static Connection connection = null;
    static ResultSet tables = null;
    static PreparedStatement preparedStatement = null;
    static ResultSet rs = null;

    /**
     * 读取 mdb文件
     *
     * @param mdbPath 文件有效路径
     * @return 格式： [{data=[数据], name=表名, column=[列名]}]
     * 如：[{data=[{Index=1, Data=999}, {Index=2, Data=97}}], name=SpaceData, column=[Index, Data]}]
     */
    public static List<Map<String, Object>> resolverMdb(String mdbPath) {
        List<Map<String, Object>> lstMdb = new ArrayList<>();
        Properties prop = new Properties();
        prop.put("charSet", "UTF-8");
        String mdbUrl = "jdbc:ucanaccess://" + mdbPath;

        try {
            // 引入驱动
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver").newInstance();

            // 创建连接
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
            close();
        }
        return lstMdb;
    }

    public static void close() {
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
