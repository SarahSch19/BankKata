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

    private ResultSet query(String sql) {
        return query(sql, null);
    }

    private ResultSet query(String sql, ArrayList<Object> values ){
        ResultSet result = null;
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            try{
                PreparedStatement stmt = c.prepareStatement(sql);
                if(values != null)
                    for (int i = 0; i < values.size(); i++) {
                        switch (values.get(i).getClass().getSimpleName()) {
                            case "String":
                                stmt.setString(i + 1, values.get(i).toString());
                                break;
                            case "Integer":
                                stmt.setInt(i + 1, (int) values.get(i));
                                break;
                            default:
                                System.out.println("Error type");
                        }
                    }
                try{
                    result = stmt.executeQuery();
                } catch (SQLException e){ }

            }catch (SQLException e){
                System.out.print("erreur sql : " + e);
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return result;
    }

    private void initDb() {
        query("CREATE TABLE accounts(name VARCHAR(255), balance FLOAT, overdraft FLOAT, blocked BOOLEAN DEFAULT 'false')");
    }

    public void closeDb() {
        try {
            c.close();
        } catch (SQLException e) {
            System.out.println("Could not close the database : " + e);
        }
    }

    public void dropAllTables() {
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

        ArrayList<Object> values = new ArrayList<Object>();
        values.add(name);
        values.add(balance);
        values.add(threshold);

        query("INSERT INTO accounts (name, balance, overdraft, blocked) VALUES (?,?,?,'false')", values);

    }

    public String printAllAccounts() {
        ResultSet result;
        String accounts = "";

        result = query("SELECT name, balance, overdraft, blocked FROM accounts");
        try {
            while(result.next()) {
                String name = result.getString(1);
                int balance = result.getInt(2);
                int threshold = result.getInt(3);
                boolean blocked = result.getBoolean(4);
                accounts += new Account(name, balance, threshold, blocked).toString() + "\n";
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return accounts;
    }

    public void changeBalanceByName(String name, int balanceModifier) {
        ArrayList<Object> values = new ArrayList<Object>();
        values.add(balanceModifier);
        values.add(name);
        values.add(balanceModifier);

        query("UPDATE accounts SET balance = balance + ? WHERE name = ? AND blocked = 'false' AND balance + ? >= overdraft", values);

    }

    public void blockAccount(String name) {
        ArrayList<Object> values = new ArrayList<Object>();
        values.add(name);
        query("UPDATE accounts SET blocked = 'true' WHERE name = ?", values);
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
