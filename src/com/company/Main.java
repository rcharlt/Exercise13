package com.company;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/* Rylee Charlton, 04/16/2019, exercise 13:
In the checking account example, we addressed the race condition though synchronization,
first by declaring the withdraw() method as synchronized and second by using a synchronizer.
An alternative method is to use a synchronize statement like this:

//some code
synchronize(object) {
  // code that uses object but only allows one thread to access it at a time
}
This creates an intrinsic lock on object. Rewrite the checking account example to use an intrinsic lock on
the account object created Main.main().
 */


class CheckingAccount {
    // create a fair mutex
    private Semaphore permits = new Semaphore(1, true);
    private int balance;

    public CheckingAccount(int initialBalance)
    {
        balance = initialBalance;
    }

    public int withdraw(int amount) {
        synchronized(this) {
            try {
                permits.acquire();
            } catch (InterruptedException e) {
                // exception prevented acquiring a permit
                return balance;
            }
        }

        if (amount <= balance)
        {
            try {
                Thread.sleep((int) (Math.random() * 200));
            }
            catch (InterruptedException ie) {
            }

            balance -= amount;
        }

        permits.release();
        return balance;
    }
}


class AccountHolder implements Runnable {
    private String name;
    private CheckingAccount account;

    AccountHolder(String name, CheckingAccount account) {
        this.name = name;
        this.account = account;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(name + " tries to withdraw $10, balance: " +
                    account.withdraw(10));
        }

    }
}

public class Main {
    public static void main(String[] args) {
        CheckingAccount account = new CheckingAccount(100);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(new AccountHolder("Wife", account));
        executor.submit(new AccountHolder("Husband", account));

    }
}