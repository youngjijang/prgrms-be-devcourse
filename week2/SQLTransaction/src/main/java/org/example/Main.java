package org.example;

import java.sql.*;

public class Main {
    static final String DB_URL = "jdbc:mysql://grepp.cpjgktk35rty.ap-northeast-2.rds.amazonaws.com:3306/test";
    static final String USER = "guest";
    static final String PASS = "";   // 패스워드는 비디오 강의 참고해서 입력해주세요

    public static void printResultSet(ResultSet resultSet) throws SQLException {
        // Ensure we start with first row
        resultSet.beforeFirst();
        while (resultSet.next()) {
            // Display values
            System.out.print("NAME: " + resultSet.getString("name"));
            System.out.print(", Gender: " + resultSet.getString("gender"));
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        try {
            // Open a Connection
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            // Set auto commit as false;
            connection.setAutoCommit(false);

            // Execute a query to create statement with
            // required arguments for RS example.
            System.out.println("Creating statement...");
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);

            // INSERT a row into your table
            System.out.println("Inserting one row...");
            statement.executeUpdate("INSERT INTO table VALUES ('Ben', 'Male');");  // table을 상황에 맞게 수정해주세요.

            // Commit data here.
            System.out.println("Committing data here...");
            connection.commit();

            // Now list all the available records.
            String sql = "SELECT * FROM table;";      // table을 상황에 맞게 수정해주세요.
            ResultSet resultSet = statement.executeQuery(sql);
            printResultSet(resultSet);

            // Clean-up environment
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection!=null){
                    // If there is an error then rollback the changes.
                    System.out.println("Rolling back data here...");
                    connection.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }finally {
                // finally block used to close resources
                try {
                    if (statement!=null){
                        statement.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}