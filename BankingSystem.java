import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

public class BankingSystem extends JFrame {

    private static ArrayList<Account> accounts = new ArrayList<>();
    private static final String ACCOUNTS_FILE = "accounts.dat";
    protected CardLayout cardLayout;
    protected JPanel cardPanel;

    public static void main(String[] args) {
        // The entry point of the program
        SwingUtilities.invokeLater(() -> {
            // Create an instance of BankingSystem to start the application
            new BankingSystem(); 
        });
    }

    public BankingSystem() {
        loadAccountsFromFile(); // Load accounts from file

        setTitle("Galang-Peralta Banking Corporation");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set up the CardLayout to switch between different panels
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create the main menu panel
        JPanel mainMenuPanel = createMainMenuPanel();
        cardPanel.add(mainMenuPanel, "Main Menu");

        // Create panels for each action
        JPanel createAccountPanel = new CreateAccountPanel(this);
        cardPanel.add(createAccountPanel, "Create Account");

        JPanel balanceInquiryPanel = new BalanceInquiryPanel(this);
        cardPanel.add(balanceInquiryPanel, "Balance Inquiry");

        JPanel depositPanel = new DepositPanel(this);
        cardPanel.add(depositPanel, "Deposit");

        JPanel withdrawPanel = new WithdrawPanel(this);
        cardPanel.add(withdrawPanel, "Withdraw");

        JPanel accountInfoPanel = new AccountInfoPanel(this);
        cardPanel.add(accountInfoPanel, "Account Information");

        JPanel closeAccountPanel = new CloseAccountPanel(this);
        cardPanel.add(closeAccountPanel, "Close Account");

        add(cardPanel, BorderLayout.CENTER);

        // Show the main menu
        cardLayout.show(cardPanel, "Main Menu");

        setVisible(true);
    }

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
                saveAccountsToFile();
                System.exit(0);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid selection!");
        }
    }

    private void saveAccountsToFile() {
        try {
            File file = new File(ACCOUNTS_FILE);
            file.getParentFile().mkdirs();  // Create directories if they don't exist
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ACCOUNTS_FILE))) {
                oos.writeObject(accounts);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving accounts: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAccountsFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ACCOUNTS_FILE))) {
            accounts = (ArrayList<Account>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            accounts = new ArrayList<>();
        }
    }

    public static void addAccount(Account account) {
        accounts.add(account);
    }

    public static Account findAccount(int accountNumber) {
        for (Account acc : accounts) {
            if (acc.getAccountNumber() == accountNumber) {
                return acc;
            }
        }
        return null;
    }

    public static void removeAccount(Account account) {
        accounts.remove(account);
    }


class CreateAccountPanel extends JPanel {
        private final BankingSystem parent;

        public CreateAccountPanel(BankingSystem parent) {
            this.parent = parent;
            setLayout(new GridLayout(8, 2));

            JTextField fNameField = new JTextField();
            JTextField addressField = new JTextField();
            JTextField bdayField = new JTextField();
            JTextField genderField = new JTextField();
            JComboBox<String> accTypeComboBox = new JComboBox<>(new String[]{"SA", "CA"});
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

                if (accountType.equals("SA") && initialDeposit < 5000) {
                    JOptionPane.showMessageDialog(this, "Minimum deposit for Savings Account is 5000.");
                    return;
                } else if (accountType.equals("CA") && initialDeposit < 10000) {
                    JOptionPane.showMessageDialog(this, "Minimum deposit for Current Account is 10000.");
                    return;
                }

                Account account = new Account(fName, address, birthday, gender, accountType, initialDeposit, pin);
                BankingSystem.addAccount(account);
                JOptionPane.showMessageDialog(this, "Account created successfully! Account Number: " + account.getAccountNumber());

                // Return to the main menu
                parent.cardLayout.show(parent.cardPanel, "Main Menu");
            });
        }
    }

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
                        JOptionPane.showMessageDialog(this, "Balance: " + account.getBalance());
                    } else {
                        JOptionPane.showMessageDialog(this, "Account not found!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid account number!");
                }

                // Return to the main menu
                parent.cardLayout.show(parent.cardPanel, "Main Menu");
            });
        }
    }

class DepositPanel extends JPanel {
        private final BankingSystem parent;

        public DepositPanel(BankingSystem parent) {
            this.parent = parent;
            setLayout(new FlowLayout());

            JTextField accountNumberField = new JTextField(15);
            JTextField amountField = new JTextField(10);
            JButton depositButton = new JButton("Deposit");

            add(new JLabel("Account Number:"));
            add(accountNumberField);
            add(new JLabel("Amount:"));
            add(amountField);
            add(depositButton);

            depositButton.addActionListener(e -> {
                try {
                    int accountNumber = Integer.parseInt(accountNumberField.getText());
                    Account account = BankingSystem.findAccount(accountNumber);
                    if (account != null) {
                        double amount = Double.parseDouble(amountField.getText());
                        account.deposit(amount);
                        JOptionPane.showMessageDialog(this, "Deposited: " + amount);
                    } else {
                        JOptionPane.showMessageDialog(this, "Account not found!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input!");
                }

                // Return to the main menu
                parent.cardLayout.show(parent.cardPanel, "Main Menu");
            });
        }
    }

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
                        double amount = Double.parseDouble(amountField.getText());
                        if (account.withdraw(amount)) {
                            JOptionPane.showMessageDialog(this, "Withdrew: " + amount);
                        } else {
                            JOptionPane.showMessageDialog(this, "Insufficient balance!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Account not found!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input!");
                }

                // Return to the main menu
                parent.cardLayout.show(parent.cardPanel, "Main Menu");
            });
        }
    }

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
                        String info = "Account Number: " + account.getAccountNumber() + "\n" +
                                    "Full Name: " + account.getFullName() + "\n" +
                                    "Address: " + account.getAddress() + "\n" +
                                    "Birthday: " + account.getBirthday() + "\n" +
                                    "Gender: " + account.getGender() + "\n" +
                                    "Account Type: " + account.getAccountType() + "\n" +
                                    "Balance: " + account.getBalance() + "\n";
                        JOptionPane.showMessageDialog(this, info);
                    } else {
                        JOptionPane.showMessageDialog(this, "Account not found!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid account number!");
                }

                // Return to the main menu
                parent.cardLayout.show(parent.cardPanel, "Main Menu");
            });
        }
    }

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
                        BankingSystem.removeAccount(account);
                        JOptionPane.showMessageDialog(this, "Account closed successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Account not found!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid account number!");
                }

                // Return to the main menu
                parent.cardLayout.show(parent.cardPanel, "Main Menu");
            });
        }
    }
}