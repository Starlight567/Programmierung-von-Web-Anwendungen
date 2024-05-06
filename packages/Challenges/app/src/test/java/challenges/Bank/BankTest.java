package challenges.Bank;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BankTest {

    Bank bank;
    Account jane;
    Account john;
    Account allowNegativeAccount;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        jane = new Account("1", "Jane", false);
        john = new Account("2", "John", false);
        allowNegativeAccount = new Account("3", "AllowNegative", true);

        bank.openAccount(jane);
        bank.openAccount(john);
        bank.openAccount(allowNegativeAccount);
    }

    @Test
    void addAccounts() {
        assertEquals(bank.totalManagedMoney(), 0);

        jane.deposit(100);

        assertEquals(bank.totalManagedMoney(), 100);
    }

    @Test
    void addAccountWithDuplicateId() {
        Account acc1 = new Account("1", "Pretending to be Jane", false);
        assertThrows(IllegalArgumentException.class, () -> bank.openAccount(acc1));
    }

    @Test
    void closeAccountWithPositiveBalance() {
        assertDoesNotThrow(() -> bank.closeAccount(jane));

        john.deposit(100);

        assertDoesNotThrow(() -> bank.closeAccount(john));
    }

    @Test
    void closeAccountWithNegativeBalance() {
        jane.withdraw(100);
        assertThrows(InsufficientFundsException.class, () -> bank.closeAccount(jane));
    }

    @Test
    void closeAccountWithNegativeBalanceAllowed() {
        allowNegativeAccount.withdraw(100);
        assertThrows(InsufficientFundsException.class, () -> bank.closeAccount(allowNegativeAccount));
    }

    @Test
    void withdraw() {
        jane.deposit(100);
        jane.withdraw(50);

        assertEquals(jane.balance, 50);
    }

    @Test
    void withdrawFail() {
        jane.deposit(50);

        assertThrows(InsufficientFundsException.class, () -> jane.withdraw(100));
    }

    @Test
    void withdrawIntoNegative() {
        allowNegativeAccount.withdraw(100);
        assertEquals(allowNegativeAccount.balance, -100);
    }

    @Test
    void transferMoney() {
        jane.deposit(100);

        assertEquals(bank.totalManagedMoney(), 100);
        assertEquals(jane.balance, 100);
        assertEquals(john.balance, 0);

        bank.transfer(jane, john, 50);

        assertEquals(bank.totalManagedMoney(), 100);
        assertEquals(jane.balance, 50);
        assertEquals(john.balance, 50);
    }

    @Test
    void transferMoneyFail() {
        assertEquals(bank.totalManagedMoney(), 0);
        assertEquals(jane.balance, 0);
        assertEquals(john.balance, 0);

        assertThrows(InsufficientFundsException.class, () -> bank.transfer(jane, john, 50));
    }

    @Test
    void allowedToWithdraw() {
        jane.deposit(100);
        assertTrue(jane.allowedToWithdraw(100));
        assertFalse(jane.allowedToWithdraw(101));

        assertEquals(allowNegativeAccount.balance, 0);
        assertTrue(allowNegativeAccount.allowedToWithdraw(100));
    }
}
