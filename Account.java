import java.io.Serializable;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Account implements Serializable {
    private static int accountCounter = 1000;
    private int accountNumber;
    private String fullName;
    private String address;
    private String birthday;
    private String gender;
    private String accountType;
    private BigDecimal balance; // Using BigDecimal for better precision with money
    private String hashedPin; // Store hashed PIN instead of plain PIN
    private String salt; // Store salt for PBKDF2

    // Constructor
    public Account(String fullName, String address, String birthday, String gender, String accountType, double balance, String pin) {
        this.accountNumber = accountCounter++;
        this.fullName = fullName;
        this.address = address;
        this.birthday = birthday;
        this.gender = gender;
        this.accountType = accountType;
        this.balance = BigDecimal.valueOf(balance);
        this.salt = generateSalt(); // Generate salt for PBKDF2
        this.hashedPin = hashPin(pin, this.salt); // Hash the PIN using PBKDF2 with the generated salt
    }

    // Method to generate a random salt
    private String generateSalt() {
        byte[] saltBytes = new byte[16]; // 16 bytes for salt
        new SecureRandom().nextBytes(saltBytes); // Generate a secure random salt
        return Base64.getEncoder().encodeToString(saltBytes); // Encode salt in Base64
    }

    // Method to hash the PIN using PBKDF2
    private String hashPin(String pin, String salt) {
        try {
            // PBKDF2 configuration
            int iterations = 10000; // Number of iterations
            int keyLength = 256; // Length of the hash in bits

            PBEKeySpec spec = new PBEKeySpec(pin.toCharArray(), Base64.getDecoder().decode(salt), iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashedPinBytes = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hashedPinBytes); // Return Base64 encoded hash
        } catch (Exception e) {
            throw new RuntimeException("Error hashing PIN", e);
        }
    }

    // Getter methods
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

    public String getHashedPin() {
        return hashedPin;
    }

    public String getSalt() {
        return salt;
    }

    // Method to verify if an entered PIN matches the stored hash
    public boolean verifyPin(String enteredPin) {
        String hashToCheck = hashPin(enteredPin, this.salt); // Hash the entered PIN with the stored salt
        return hashToCheck.equals(this.hashedPin); // Check if the entered PIN's hash matches the stored hash
    }

    // Deposit method
    public void deposit(double amount) {
        this.balance = this.balance.add(BigDecimal.valueOf(amount));
    }

    // Withdraw method
    public boolean withdraw(double amount) {
        BigDecimal withdrawalAmount = BigDecimal.valueOf(amount);
        if (balance.compareTo(withdrawalAmount) >= 0) {
            balance = balance.subtract(withdrawalAmount);
            return true;
        }
        return false;
    }
}
