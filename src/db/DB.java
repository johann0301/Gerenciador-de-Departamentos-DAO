package db;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DB {

    private static Connection conn = null;

    public static Connection getConnection(){
        if(conn == null) {
            try {
                Properties props = loadProperties();
                String url = props.getProperty("dburl");
                conn = DriverManager.getConnection(url, props);
            }
            catch(SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
        return conn;
    }

    public static void closeConnection(){
        if( conn != null) {
            try{
                conn.close();
            } catch(SQLException e) {
                throw new DbException(e.getMessage());
            }
        }

    }


    private static Properties loadProperties() {
        try (var input = DB.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new DbException("Arquivo db.properties não encontrado");
            }
            Properties props = new Properties();
            props.load(input);
            return props;
        }
        catch(IOException e) {
            throw new DbException(e.getMessage());
        }

    }

    public static void closeStatment(Statement stat){
        if (stat!= null){
            try {
                stat.close();
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            }
        }
    }

    public static void closeResultSet(ResultSet rs){
        if (rs!= null){
            try {
                rs.close();
            } catch (SQLException e) {
                throw new DbException(e.getMessage());
            } 
        }
    }

}