package bank;


import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Bank {

    /*
        Strings de connection à la base postgres
     */
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL = "jdbc:postgresql://localhost:5439/postgres";
    private static final String DB_USER = "postgres";

    /*
        Strings de connection à la base mysql, à décommenter et compléter avec votre nom de bdd et de user
     */
    // private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    // private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    // private static final String DB_USER = "bank_user";

    private static final String DB_PASS = "1234";

    private static final String TABLE_NAME = "accounts";

    private Connection c;

    public Bank() {
        initDb();

        // TODO
    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            String query = "CREATE TABLE accounts(name VARCHAR(255), balance FLOAT, overdraft FLOAT, blocked BOOLEAN DEFAULT 'false');";
            try{
                Statement stmt = c.createStatement();
                stmt.executeUpdate(query);

            }catch (SQLException e){
                System.out.print("erreur sql : " + e);
            }
            // TODO Init DB

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void closeDb() {
        try {
            c.close();
        } catch (SQLException e) {
            System.out.println("Could not close the database : " + e);
        }
    }

    void dropAllTables() {
        try (Statement s = c.createStatement()) {
            s.executeUpdate(
                       "DROP SCHEMA public CASCADE;" +
                            "CREATE SCHEMA public;" +
                            "GRANT ALL ON SCHEMA public TO postgres;" +
                            "GRANT ALL ON SCHEMA public TO public;");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    public void createNewAccount(String name, int balance, int threshold) {
        if(threshold > 0)
            return;
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            String query = "INSERT INTO accounts (name, balance, overdraft, blocked) VALUES ('" + name + "'," + Integer.toString(balance) + ',' + Integer.toString(threshold) + ",'false')";
            System.out.print(query);
            try{
                Statement stmt = c.createStatement();
                stmt.executeUpdate(query);

            }catch (SQLException e){
                System.out.print("erreur sql : " + e);
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public String printAllAccounts() {
        ResultSet result;
        String accounts = "";

        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            String query = "SELECT name, balance, overdraft, blocked FROM accounts";
            System.out.print(query);
            try{
                Statement stmt = c.createStatement();
                result = stmt.executeQuery(query);
                while(result.next()) {
                    String name = result.getString(1);
                    int balance = result.getInt(2);
                    int threshold = result.getInt(3);
                    boolean blocked = result.getBoolean(4);
                    accounts += new Account(name, balance, threshold, blocked).toString() + "\n";
                }
            }catch (SQLException e){
                System.out.print("erreur sql : " + e);
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return accounts;
    }

    public void changeBalanceByName(String name, int balanceModifier) {

        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            String query = "UPDATE accounts SET balance = balance + " + balanceModifier + " WHERE name = '" + name + "' AND blocked = 'false' AND balance +" + balanceModifier + " >= overdraft";
            System.out.print(query);
            try{
                Statement stmt = c.createStatement();
                stmt.executeQuery(query);
            }catch (SQLException e){
                System.out.print("erreur sql : " + e);
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void blockAccount(String name) {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");

            String query = "UPDATE accounts SET blocked = 'true' WHERE name = '" + name + "'";
            System.out.print(query);
            try{
                Statement stmt = c.createStatement();
                stmt.executeQuery(query);
            }catch (SQLException e){
                System.out.print("erreur sql : " + e);
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    // For testing purpose
    String getTableDump() {
        String query = "select * from " + TABLE_NAME;
        String res = "";

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // Getting nb colmun from meta data
            int nbColumns = r.getMetaData().getColumnCount();

            // while there is a next row
            while (r.next()){
                String[] currentRow = new String[nbColumns];

                // For each column in the row
                for (int i = 1 ; i <= nbColumns ; i++) {
                    currentRow[i - 1] = r.getString(i);
                }
                res += Arrays.toString(currentRow);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }
}
