import java.io.*;
import java.math.BigDecimal;

public class Account implements Serializable {
    private static int accountCounter = 1000;
    private int accountNumber;
    private String fullName;
    private String address;
    private String birthday;
    private String gender;
    private String accountType;
    private BigDecimal balance; // Changed to BigDecimal for better precision with money
    private String pin;

    public Account(String fullName, String address, String birthday, String gender, String accountType, double balance, String pin) {
        this.accountNumber = accountCounter++;
        this.fullName = fullName;
        this.address = address;
        this.birthday = birthday;
        this.gender = gender;
        this.accountType = accountType;
        this.balance = BigDecimal.valueOf(balance); // Store balance as BigDecimal
        this.pin = pin;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getPin() {
        return pin;
    }

    public void deposit(double amount) {
        this.balance = this.balance.add(BigDecimal.valueOf(amount));
    }

    public boolean withdraw(double amount) {
        BigDecimal withdrawalAmount = BigDecimal.valueOf(amount);
        if (balance.compareTo(withdrawalAmount) >= 0) {
            balance = balance.subtract(withdrawalAmount);
            return true;
        }
        return false;
    }
}

