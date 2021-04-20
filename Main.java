import bank.Bank;

import java.util.Scanner;

public class Main extends Application {

    private static Scanner s = new Scanner(System.in);

    // Nettoie l'écran des prints précédents
    private static void flushScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {

        // Init
        Bank b = new Bank();

        /// Declaration before loop
        boolean endOfSession = false;
        String userInput;

        // Loop
        while (!endOfSession) {

            // Menu display
            System.out.println("\n\nWhat operation do you want to do ?");
            System.out.println("0. See all accounts");
            System.out.println("1. Create a new account");
            System.out.println("2. Change balance on a given account");
            System.out.println("3. Block an account");
            System.out.println("q. Quit\n");

            // Getting primary input
            userInput = s.nextLine();
            String name;
            int balance;
            int threshold;
            // Processing user input
            switch (userInput) {
                case "q":
                    endOfSession = true;
                    b.dropAllTables();
                    b.closeDb();
                    break;
                case "0":
                    System.out.print(b.printAllAccounts());
                    break;
                case "1":
                    System.out.print("Enter the account name : ");
                    name = s.nextLine();

                    System.out.print("Enter the account balance : ");
                    balance = Integer.parseInt(s.nextLine());

                    System.out.print("Enter the maximum threshold account : ");
                    threshold = Integer.parseInt(s.nextLine());

                    b.createNewAccount(name, balance, threshold);
                    break;
                case "2":
                    System.out.print("Enter the account name : ");
                    name = s.nextLine();

                    System.out.print("Enter what you want to add to this account : ");
                    balance = Integer.parseInt(s.nextLine());

                    b.changeBalanceByName(name, balance);
                    break;

                case "3":
                    System.out.print("Enter the account name that will be blocked : ");
                    name = s.nextLine();

                    b.blockAccount(name);
                    break;
            }
        }

    }
}

