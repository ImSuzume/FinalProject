import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;


public class BankingSystem extends JFrame {

    private static ArrayList<Account> accounts = new ArrayList<>();
    private static final String ACCOUNTS_FILE = "accounts.dat";
    protected CardLayout cardLayout;
    protected JPanel cardPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankingSystem());
    }

    public BankingSystem() {
        loadOrSaveAccounts(false); // Load accounts from file

        setTitle("Galang-Peralta Banking Corporation");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Adding all panels using a helper method
        addPanelToLayout(createMainMenuPanel(), "Main Menu");
        addPanelToLayout(new CreateAccountPanel(this), "Create Account");
        addPanelToLayout(new BalanceInquiryPanel(this), "Balance Inquiry");
        addPanelToLayout(new DepositPanel(this), "Deposit");
        addPanelToLayout(new WithdrawPanel(this), "Withdraw");
        addPanelToLayout(new AccountInfoPanel(this), "Account Information");
        addPanelToLayout(new CloseAccountPanel(this), "Close Account");

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "Main Menu");

        setVisible(true);
    }

    // Method to add panels to the CardLayout
    private void addPanelToLayout(JPanel panel, String name) {
        cardPanel.add(panel, name);
    }

    private boolean verifyPin(Account account, String pin) {
        try {
            // Extract the salt (which is stored as a String in the account) and convert it to a byte array
            String saltString = account.getSalt(); // Get the salt from account
            if (saltString == null) {
                throw new IllegalArgumentException("Salt is null for this account.");
            }

            // Convert the salt String to byte array using Base64 decoding (assuming the salt was Base64 encoded)
            byte[] salt = Base64.getDecoder().decode(saltString);

            // Retrieve the stored hash (not the raw pin)
            String storedHash = account.getHashedPin();

            // Hash the entered pin with the stored salt
            String hashedPin = hashPin(pin, salt);

            // Return whether the hashes match
            return storedHash.equals(hashedPin);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    

    private String hashPin(String pin, byte[] salt) throws Exception {
        int iterations = 10000; // Number of iterations for PBKDF2
        int keyLength = 256; // Length of the hash in bits
    
        PBEKeySpec spec = new PBEKeySpec(pin.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash); // Return the Base64 encoded hash as a String
    }
    

    // Consolidated file loading and saving method
    private void loadOrSaveAccounts(boolean save) {
        try {
            if (save) {
                // Saving accounts
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ACCOUNTS_FILE))) {
                    oos.writeObject(accounts); // Save accounts
                }
            } else {
                // Loading accounts
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ACCOUNTS_FILE))) {
                    accounts = (ArrayList<Account>) ois.readObject(); // Load accounts
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!save) {
                accounts = new ArrayList<>();
                JOptionPane.showMessageDialog(this, "Error loading accounts: " + e.getMessage());
            }
            if (save) {
                JOptionPane.showMessageDialog(this, "Error saving accounts: " + e.getMessage());
            }
        }    
    }

    // Main menu panel setup
    private JPanel createMainMenuPanel() {
        JPanel mainMenuPanel = new JPanel(new GridLayout(4, 2));
        String[] buttons = {"Create Account", "Balance Inquiry", "Deposit", "Withdraw", "Account Information", "Close Account", "Exit"};

        for (String btnText : buttons) {
            JButton button = new JButton(btnText);
            mainMenuPanel.add(button);
            button.addActionListener(e -> handleMenuSelection(btnText));
        }
        return mainMenuPanel;
    }

    // Handle menu selection and switch panels
    private void handleMenuSelection(String selection) {
        switch (selection) {
            case "Create Account":
                cardLayout.show(cardPanel, "Create Account");
                break;
            case "Balance Inquiry":
                cardLayout.show(cardPanel, "Balance Inquiry");
                break;
            case "Deposit":
                cardLayout.show(cardPanel, "Deposit");
                break;
            case "Withdraw":
                cardLayout.show(cardPanel, "Withdraw");
                break;
            case "Account Information":
                cardLayout.show(cardPanel, "Account Information");
                break;
            case "Close Account":
                cardLayout.show(cardPanel, "Close Account");
                break;
            case "Exit":
                loadOrSaveAccounts(true); // Save accounts to file
                System.exit(0);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid selection!");
        }
    }

    // Add a new account
    public static void addAccount(Account account) {
        accounts.add(account);
    }

    // Find an account by account number
    public static Account findAccount(int accountNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber() == accountNumber) {
                return acc;
            }
        }
        return null;
    }

    // Remove an account
    public static void removeAccount(Account account) {
        accounts.remove(account);
    }

    // Create Account Panel
    class CreateAccountPanel extends JPanel {
        private final BankingSystem parent;

        public CreateAccountPanel(BankingSystem parent) {
            this.parent = parent;
            setLayout(new GridLayout(8, 2));

            JTextField fNameField = new JTextField();
            JTextField addressField = new JTextField();
            JTextField bdayField = new JTextField();
            JTextField genderField = new JTextField();
            JComboBox<String> accTypeComboBox = new JComboBox<>(new String[]{"Savings", "Current"});
            JTextField initialDepositField = new JTextField();
            JTextField pinField = new JTextField();

            add(new JLabel("Full Name:"));
            add(fNameField);
            add(new JLabel("Address:"));
            add(addressField);
            add(new JLabel("Birthday (DD/MM/YYYY):"));
            add(bdayField);
            add(new JLabel("Gender:"));
            add(genderField);
            add(new JLabel("Account Type:"));
            add(accTypeComboBox);
            add(new JLabel("Initial Deposit:"));
            add(initialDepositField);
            add(new JLabel("Pin (6 digits):"));
            add(pinField);

            JButton createButton = new JButton("Create Account");
            add(new JLabel());
            add(createButton);

            createButton.addActionListener(e -> {
                String fName = fNameField.getText();
                String address = addressField.getText();
                String birthday = bdayField.getText();
                String gender = genderField.getText();
                String accountType = (String) accTypeComboBox.getSelectedItem();
                double initialDeposit = Double.parseDouble(initialDepositField.getText());
                String pin = pinField.getText();

                if (pin.length() != 6 || !pin.matches("\\d{6}")) {
                    JOptionPane.showMessageDialog(this, "PIN must be exactly 6 digits!");
                    return;
                }

                if (accountType.equals("Savings") && initialDeposit < 5000) {
                    JOptionPane.showMessageDialog(this, "Minimum deposit for Savings Account is 5000.");
                    return;
                } else if (accountType.equals("Current") && initialDeposit < 10000) {
                    JOptionPane.showMessageDialog(this, "Minimum deposit for Current Account is 10000.");
                    return;
                }

                Account account = new Account(fName, address, birthday, gender, accountType, initialDeposit, pin);
                BankingSystem.addAccount(account);
                JOptionPane.showMessageDialog(this, "Account created successfully! Account Number: " + account.getAccountNumber());

                parent.cardLayout.show(parent.cardPanel, "Main Menu");
            });
        }
    }

    // Balance Inquiry Panel
    class BalanceInquiryPanel extends JPanel {
        private final BankingSystem parent;

        public BalanceInquiryPanel(BankingSystem parent) {
            this.parent = parent;
            setLayout(new FlowLayout());

            JTextField accountNumberField = new JTextField(15);
            JButton checkBalanceButton = new JButton("Check Balance");

            add(new JLabel("Account Number:"));
            add(accountNumberField);
            add(checkBalanceButton);

            checkBalanceButton.addActionListener(e -> {
                try {
                    int accountNumber = Integer.parseInt(accountNumberField.getText());
                    Account account = BankingSystem.findAccount(accountNumber);
                    if (account != null) {
                        String pin = JOptionPane.showInputDialog(this, "Enter PIN for account access:");

                        if (account.verifyPin(pin)) {
                            JOptionPane.showMessageDialog(this, "Balance: " + account.getBalance());
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid PIN!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Account not found!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid account number!");
                }

                parent.cardLayout.show(parent.cardPanel, "Main Menu");
            });
        }
    }

    // Deposit Panel
    class DepositPanel extends JPanel {
        private final BankingSystem parent;

        public DepositPanel(BankingSystem parent) {
            this.parent = parent;
            setLayout(new FlowLayout());

            JTextField accountNumberField = new JTextField(10);
            JTextField pinField = new JTextField(10);
            JTextField depositAmountField = new JTextField(10);
            JButton depositButton = new JButton("Deposit");

            add(new JLabel("Account Number:"));
            add(accountNumberField);
            add(new JLabel("PIN:"));
            add(pinField);
            add(new JLabel("Deposit Amount:"));
            add(depositAmountField);
            add(depositButton);

            depositButton.addActionListener(e -> {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                String pin = pinField.getText();
                double amount = Double.parseDouble(depositAmountField.getText());

                Account account = BankingSystem.findAccount(accountNumber);

                if (account != null && parent.verifyPin(account, pin)) {
                    account.deposit(amount);
                    JOptionPane.showMessageDialog(this, "Deposit successful! New balance: " + account.getBalance());
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid account number or PIN.");
                }
            });
        }
    }

    // Withdraw Panel
    class WithdrawPanel extends JPanel {
        private final BankingSystem parent;

        public WithdrawPanel(BankingSystem parent) {
            this.parent = parent;
            setLayout(new FlowLayout());

            JTextField accountNumberField = new JTextField(15);
            JTextField amountField = new JTextField(10);
            JButton withdrawButton = new JButton("Withdraw");

            add(new JLabel("Account Number:"));
            add(accountNumberField);
            add(new JLabel("Amount:"));
            add(amountField);
            add(withdrawButton);

            withdrawButton.addActionListener(e -> {
                try {
                    int accountNumber = Integer.parseInt(accountNumberField.getText());
                    Account account = BankingSystem.findAccount(accountNumber);
                    if (account != null) {
                        String pin = JOptionPane.showInputDialog(this, "Enter PIN for account access:");

                        if (verifyPin(account, pin)) {
                            double amount = Double.parseDouble(amountField.getText());
                            if (account.getBalance().compareTo(BigDecimal.valueOf(amount)) >= 0) {
                                account.withdraw(amount);
                                JOptionPane.showMessageDialog(this, "Withdrawn: " + amount);
                            } else {
                                JOptionPane.showMessageDialog(this, "Insufficient funds!");
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid PIN!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Account not found!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input!");
                }

                parent.cardLayout.show(parent.cardPanel, "Main Menu");
            });
        }
    }

    // Account Information Panel
    class AccountInfoPanel extends JPanel {
        private final BankingSystem parent;

        public AccountInfoPanel(BankingSystem parent) {
            this.parent = parent;
            setLayout(new FlowLayout());

            JTextField accountNumberField = new JTextField(15);
            JButton showInfoButton = new JButton("Show Account Info");

            add(new JLabel("Account Number:"));
            add(accountNumberField);
            add(showInfoButton);

            showInfoButton.addActionListener(e -> {
                try {
                    int accountNumber = Integer.parseInt(accountNumberField.getText());
                    Account account = BankingSystem.findAccount(accountNumber);
                    if (account != null) {
                        String pin = JOptionPane.showInputDialog(this, "Enter PIN for account access:");

                        if (verifyPin(account, pin)) {
                            String info = "Account Number: " + account.getAccountNumber() + "\n"
                                    + "Full Name: " + account.getFullName() + "\n"
                                    + "Address: " + account.getAddress() + "\n"
                                    + "Birthday: " + account.getBirthday() + "\n"
                                    + "Gender: " + account.getGender() + "\n"
                                    + "Account Type: " + account.getAccountType() + "\n"
                                    + "Balance: " + account.getBalance();
                            JOptionPane.showMessageDialog(this, info);
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid PIN!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Account not found!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid account number!");
                }

                parent.cardLayout.show(parent.cardPanel, "Main Menu");
            });
        }
    }

    // Close Account Panel
    class CloseAccountPanel extends JPanel {
        private final BankingSystem parent;

        public CloseAccountPanel(BankingSystem parent) {
            this.parent = parent;
            setLayout(new FlowLayout());

            JTextField accountNumberField = new JTextField(15);
            JButton closeAccountButton = new JButton("Close Account");

            add(new JLabel("Account Number:"));
            add(accountNumberField);
            add(closeAccountButton);

            closeAccountButton.addActionListener(e -> {
                try {
                    int accountNumber = Integer.parseInt(accountNumberField.getText());
                    Account account = BankingSystem.findAccount(accountNumber);
                    if (account != null) {
                        String pin = JOptionPane.showInputDialog(this, "Enter PIN for account access:");

                        if (verifyPin(account, pin)) {
                            BankingSystem.removeAccount(account);
                            JOptionPane.showMessageDialog(this, "Account closed successfully!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid PIN!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Account not found!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid account number!");
                }

                parent.cardLayout.show(parent.cardPanel, "Main Menu");
            });
        }
    } 
}
