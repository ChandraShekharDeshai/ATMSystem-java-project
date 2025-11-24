import java.util.*;

/**
 * ATMSystem.java
 * Single-file ATM simulation (console).
 *
 * Notes:
 * - Money is represented with double for simplicity. For production use BigDecimal is recommended.
 * - All data is in-memory; no database or files used.
 */
public class ATMSystem {

    /* -------------------- Account class -------------------- */
    static class Account {
        private final int accountNumber;
        private final String holderName;
        private int pin;
        private double balance;
        private final Deque<String> miniStatement; // store latest N transactions
        private static final int STATEMENT_LIMIT = 10;

        public Account(int accountNumber, String holderName, int pin, double initialBalance) {
            this.accountNumber = accountNumber;
            this.holderName = holderName;
            this.pin = pin;
            this.balance = initialBalance;
            this.miniStatement = new ArrayDeque<>();
            addTransaction(String.format("Account opened with balance ₹%.2f", initialBalance));
        }

        public int getAccountNumber() { return accountNumber; }
        public String getHolderName() { return holderName; }
        public double getBalance() { return balance; }

        public boolean verifyPin(int enteredPin) {
            return this.pin == enteredPin;
        }

        public void changePin(int newPin) {
            this.pin = newPin;
            addTransaction("PIN changed");
        }

        public boolean withdraw(double amount) {
            if (amount <= 0) return false;
            if (amount > balance) return false;
            balance -= amount;
            addTransaction(String.format("Withdrawn ₹%.2f — New bal ₹%.2f", amount, balance));
            return true;
        }

        public boolean deposit(double amount) {
            if (amount <= 0) return false;
            balance += amount;
            addTransaction(String.format("Deposited ₹%.2f — New bal ₹%.2f", amount, balance));
            return true;
        }

        public boolean transferTo(Account toAccount, double amount) {
            if (toAccount == null) return false;
            if (amount <= 0) return false;
            if (amount > balance) return false;
            balance -= amount;
            toAccount.balance += amount;
            addTransaction(String.format("Transferred ₹%.2f to %d — New bal ₹%.2f",
                    amount, toAccount.accountNumber, balance));
            toAccount.addTransaction(String.format("Received ₹%.2f from %d — New bal ₹%.2f",
                    amount, this.accountNumber, toAccount.balance));
            return true;
        }

        private void addTransaction(String text) {
            if (miniStatement.size() == STATEMENT_LIMIT) {
                miniStatement.removeFirst();
            }
            String timeStamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            miniStatement.addLast(timeStamp + " | " + text);
        }

        public List<String> getMiniStatement() {
            return new ArrayList<>(miniStatement);
        }
    }

    /* -------------------- ATM class -------------------- */
    static class ATM {
        private final Map<Integer, Account> accounts = new HashMap<>();
        private final Scanner sc;
        private Account currentAccount = null;

        public ATM(Scanner sc) {
            this.sc = sc;
            seedSampleAccounts();
        }

        private void seedSampleAccounts() {
            // Pre-loaded sample accounts: accountNumber, name, pin, balance
            accounts.put(1001, new Account(1001, "Amit Kumar", 1111, 5000.0));
            accounts.put(1002, new Account(1002, "Bhavana Singh", 2222, 12000.0));
            accounts.put(1003, new Account(1003, "Charan Patel", 3333, 750.50));
        }

        public void start() {
            System.out.println("====== Welcome to Shastra ATM Simulator ======");
            outer:
            while (true) {
                currentAccount = null;
                System.out.println("\nPlease choose:");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Enter choice: ");
                String choice = sc.nextLine().trim();

                switch (choice) {
                    case "1":
                        if (login()) {
                            sessionLoop();
                        }
                        break;
                    case "2":
                        System.out.println("Thank you for using Shastra ATM. Goodbye!");
                        break outer;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }
        }

        private boolean login() {
            System.out.print("Enter account number: ");
            String accStr = sc.nextLine().trim();
            int accNum;
            try {
                accNum = Integer.parseInt(accStr);
            } catch (NumberFormatException e) {
                System.out.println("Account number should be numeric.");
                return false;
            }
            Account acc = accounts.get(accNum);
            if (acc == null) {
                System.out.println("Account not found.");
                return false;
            }

            // PIN attempts
            int attempts = 0;
            while (attempts < 3) {
                System.out.print("Enter 4-digit PIN: ");
                String pinStr = sc.nextLine().trim();
                int pin;
                try {
                    pin = Integer.parseInt(pinStr);
                } catch (NumberFormatException e) {
                    System.out.println("PIN should be numeric.");
                    attempts++;
                    continue;
                }
                if (acc.verifyPin(pin)) {
                    currentAccount = acc;
                    System.out.println("Login successful. Welcome, " + acc.getHolderName() + "!");
                    return true;
                } else {
                    attempts++;
                    System.out.println("Incorrect PIN. Attempts left: " + (3 - attempts));
                }
            }
            System.out.println("Too many incorrect attempts. Returning to main menu.");
            return false;
        }

        private void sessionLoop() {
            while (currentAccount != null) {
                printMenu();
                System.out.print("Choose an option: ");
                String option = sc.nextLine().trim();
                switch (option) {
                    case "1":
                        System.out.printf("Current Balance: ₹%.2f%n", currentAccount.getBalance());
                        break;
                    case "2":
                        doDeposit();
                        break;
                    case "3":
                        doWithdraw();
                        break;
                    case "4":
                        doTransfer();
                        break;
                    case "5":
                        showMiniStatement();
                        break;
                    case "6":
                        changePin();
                        break;
                    case "7":
                        System.out.println("Logging out...");
                        currentAccount = null;
                        break;
                    case "8":
                        System.out.println("Exiting ATM. Goodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            }
        }

        private void printMenu() {
            System.out.println("\n------ ATM Menu ------");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Mini Statement");
            System.out.println("6. Change PIN");
            System.out.println("7. Logout");
            System.out.println("8. Exit");
        }

        private void doDeposit() {
            System.out.print("Enter amount to deposit: ₹");
            String amtStr = sc.nextLine().trim();
            double amount;
            try {
                amount = Double.parseDouble(amtStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount.");
                return;
            }
            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }
            if (currentAccount.deposit(amount)) {
                System.out.printf("Deposit successful. New balance: ₹%.2f%n", currentAccount.getBalance());
            } else {
                System.out.println("Deposit failed.");
            }
        }

        private void doWithdraw() {
            System.out.print("Enter amount to withdraw: ₹");
            String amtStr = sc.nextLine().trim();
            double amount;
            try {
                amount = Double.parseDouble(amtStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount.");
                return;
            }
            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }
            if (currentAccount.withdraw(amount)) {
                System.out.printf("Please collect cash. New balance: ₹%.2f%n", currentAccount.getBalance());
            } else {
                System.out.println("Withdrawal failed. Check balance or enter a smaller amount.");
            }
        }

        private void doTransfer() {
            System.out.print("Enter recipient account number: ");
            String toAccStr = sc.nextLine().trim();
            int toAcc;
            try {
                toAcc = Integer.parseInt(toAccStr);
            } catch (NumberFormatException e) {
                System.out.println("Account number should be numeric.");
                return;
            }
            if (toAcc == currentAccount.getAccountNumber()) {
                System.out.println("Cannot transfer to the same account.");
                return;
            }
            Account recipient = accounts.get(toAcc);
            if (recipient == null) {
                System.out.println("Recipient account not found.");
                return;
            }
            System.out.print("Enter amount to transfer: ₹");
            String amtStr = sc.nextLine().trim();
            double amount;
            try {
                amount = Double.parseDouble(amtStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount.");
                return;
            }
            if (amount <= 0) {
                System.out.println("Amount must be positive.");
                return;
            }
            if (currentAccount.transferTo(recipient, amount)) {
                System.out.printf("Transfer successful. New balance: ₹%.2f%n", currentAccount.getBalance());
            } else {
                System.out.println("Transfer failed. Check balance or try again.");
            }
        }

        private void showMiniStatement() {
            System.out.println("\n--- Mini Statement (latest transactions) ---");
            List<String> stm = currentAccount.getMiniStatement();
            if (stm.isEmpty()) {
                System.out.println("No transactions yet.");
            } else {
                for (String s : stm) {
                    System.out.println(s);
                }
            }
        }

        private void changePin() {
            System.out.print("Enter current PIN: ");
            String curPinStr = sc.nextLine().trim();
            int curPin;
            try {
                curPin = Integer.parseInt(curPinStr);
            } catch (NumberFormatException e) {
                System.out.println("PIN should be numeric.");
                return;
            }
            if (!currentAccount.verifyPin(curPin)) {
                System.out.println("Incorrect current PIN.");
                return;
            }
            System.out.print("Enter new 4-digit PIN: ");
            String newPinStr = sc.nextLine().trim();
            int newPin;
            try {
                newPin = Integer.parseInt(newPinStr);
            } catch (NumberFormatException e) {
                System.out.println("PIN should be numeric.");
                return;
            }
            if (newPin < 1000 || newPin > 9999) {
                System.out.println("PIN must be 4 digits.");
                return;
            }
            currentAccount.changePin(newPin);
            System.out.println("PIN changed successfully.");
        }
    }

    /* -------------------- main -------------------- */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ATM atm = new ATM(sc);
        atm.start();
        sc.close();
    }
}
