//package com.example.employee.demo;

// #################################  Transaction Problem ###############################################

/*
We are developing a payment transaction monitoring system that tracks accounts and
their transactions.
The system can compute each account's current balance and basic statistics.

Definitions:
* An "account" has a unique accountId and an owner name.
* A "transaction" represents money moving in or out of an account.
  - CREDIT increases the account balance.
  - DEBIT decreases the account balance.
* "AccountManager" manages accounts and transactions and provides balance-related
methods.

To begin with, we present you with two tasks:
1-1) Read through and understand the code below. Please take as much time as
necessary, and feel free to run it.
1-2) The test for AccountManager is not passing due to a bug in the code.
     Make the necessary changes to AccountManager to fix the bug.
/*
We are extending our payment transaction monitoring system to support
basic analytics over transactions.

For this task, we want to calculate the average transaction amount per account.

2) Implement the function getAverageTransactionAmountByAccount in AccountManager
that returns
the average transaction amount for each account.
Requirements: - The result should associate each accountId with the average amount of its transactions. - Both CREDIT and DEBIT transactions should be considered. - Transaction amounts should be treated as absolute values when calculating averages. - Accounts with no transactions should not appear in the result. - Transactions always refer to valid accounts.
To assist you in testing this new function, we have provided the
testGetAverageTransactionAmountByAccount test.
3) Implement the function getPaymentFee for each acount ID
The method should return the total payment fee for each account.
For credit - 1$ per transaction
For debit - 2$ per transaction.
return total fee.
We are extending our payment transaction monitoring system to calculate
transaction fees based on business rules.
Each account is charged transaction fees as follows:



 - The first 3 transactions for an account are FREE. - From the 4th transaction onward:
  - CREDIT transactions cost a flat fee of $1.
  - DEBIT transactions cost a flat fee of $2.

Transactions must be processed in chronological order (by timestamp).
Fees are applied per account independently.

3.1) Implement getTransactionFees() in AccountManager. The function should
return a map associating each accountId with the total transaction
fees charged to that account.

To assist you in testing this new function, we have provided the
testGetTransactionFees function.
*/
//        Solution
//
//package Transcations;
//Doubts : what is the bug in the code?


//import jakarta.persistence.criteria.CriteriaBuilder;

import java.rmi.MarshalledObject;
import java.util.*;
import java.util.stream.Collectors;

enum TransactionType {
    CREDIT,
    DEBIT
}

class Account {
    int accountId;
    String ownerName;

    Account(int accountId, String ownerName) {
        this.accountId = accountId;
        this.ownerName = ownerName;
    }
}

class Transaction {
    int transactionId;
    int accountId;
    TransactionType type;
    double amount;     // always positive
    long timestampSec; // monotonic in tests

    Transaction(int transactionId, int accountId,
                TransactionType type, double amount, long timestampSec) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestampSec = timestampSec;
    }
}


class AccountManager {

    Map<Integer, Account> accounts = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();

    void addAccount(Account account) {
        accounts.put(account.accountId, account);

    }

    void addTransaction(Transaction tx) {
        transactions.add(tx);
    }


    double getBalance(int accountId) {
        double balance = 0.0;
        for (Transaction tx : transactions) {
            if (tx.accountId == accountId) {
                if (tx.type == TransactionType.CREDIT) {                        //bug
                    balance = balance + tx.amount;
                } else { //  debit correctly reduces balance
                    balance = balance - tx.amount;
                }
            }
        }
        return balance;
    }

//    quazi practice  area

//    quazi practice area closed




    // task 2 , method implementation
    public Map<Integer, Double> getAverageTransactionAmountByAccount() {
        return transactions.stream().collect(
                Collectors.groupingBy(
                        tx -> tx.accountId,
                        Collectors.averagingDouble(tx -> Math.abs(tx.amount))
                )
        );
    }


// task 3(A), method implementation
//Implement the function getPaymentFee for each account ID
//    The method should return the total payment fee for each account.
//    For credit - 1$ per transaction
//    For debit - 2$ per transaction.
//return total fee.

    //    aaproach -> Map<Integer(accId), Integer(fee)> will be return
//    from every trx, fee(acc to transaction type )-> use map(accId, fee)
    public Map<Integer, Integer> getPaymentFeesByAccount() {
        Map<Integer, Integer> result = new HashMap<>();
        for (Transaction t : transactions) {

            int fee = (t.type == TransactionType.CREDIT) ? 1 : 2;
//            result.merge(t.accountId, fee, Integer::sum);
            result.put(t.accountId, result.getOrDefault(t.accountId, 0) + fee );
        }
        System.out.println(result);
        return result;
    }

    // task 3(B), method implementation
//     - The first 3 transactions for an account are FREE. - From the 4th transaction onward:
//            - CREDIT transactions cost a flat fee of $1.
//            - DEBIT transactions cost a flat fee of $2.
//
//    Transactions must be processed in chronological order (by timestamp).
//    Fees are applied per account independently.
//
//            3.1) Implement getTransactionFees() in AccountManager. The function should
//return a map associating each accountId with the total transaction
//    fees charged to that account.

    public Map<Integer, Double> getTransactionFees() {
        Map<Integer, List<Transaction>> perAccount = new HashMap<>();

        for (Transaction t : transactions) {
            perAccount.computeIfAbsent(t.accountId, k -> new ArrayList<>()).add(t);
        }

        Map<Integer, Double> fees = new HashMap<>();

        for (Map.Entry<Integer, List<Transaction>> e : perAccount.entrySet()) {
            List<Transaction> list = e.getValue();
            list.sort(Comparator.comparing(t -> t.timestampSec));

            double totalFee = 0.0;
            for (int i = 3; i < list.size(); i++) { // first 3 are free
                if (list.get(i).type == TransactionType.CREDIT) {
                    totalFee += 1.0;
                } else {
                    totalFee += 2.0;
                }
            }
            fees.put(e.getKey(), totalFee);
        }
        return fees;
    }



}

public class TransactionProblem {

    public static void main(String[] args) {
        testGetBalance();
        testGetAverageTransactionAmountByAccount();
        testGetPaymentFeesByAccount();
        testGetTransactionFees();
        System.out.println(" All tests passed.");
    }



    static void assertAlmost(double exp, double act, double eps) {
        if (Math.abs(exp - act) > eps) {
            throw new AssertionError("Expected " + exp + " but got " + act);
        }
    }

    static void assertEquals(Object exp, Object act) {
        if (!Objects.equals(exp, act)) {
            throw new AssertionError("Expected " + exp + " but got " + act);
        }
    }


    static void testGetBalance() {
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));

        mgr.addTransaction(new Transaction(1, 1, TransactionType.CREDIT, 100, 1));
        mgr.addTransaction(new Transaction(2, 1, TransactionType.DEBIT, 30, 2));
        mgr.addTransaction(new Transaction(3, 1, TransactionType.DEBIT, 20, 3));
        mgr.addTransaction(new Transaction(4, 1, TransactionType.CREDIT, 10, 4));

        assertAlmost(60.0, mgr.getBalance(1), 0.0001);




    }

    static void testGetAverageTransactionAmountByAccount() {
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));
        mgr.addAccount(new Account(2, "Bob"));

        mgr.addTransaction(new Transaction(1, 1, TransactionType.CREDIT, 100, 1));
        mgr.addTransaction(new Transaction(2, 1, TransactionType.DEBIT, 30, 2));
        mgr.addTransaction(new Transaction(3, 1, TransactionType.DEBIT, 20, 3));
        mgr.addTransaction(new Transaction(4, 1, TransactionType.CREDIT, 10, 4));

        mgr.addTransaction(new Transaction(5, 2, TransactionType.CREDIT, 80, 5));
        mgr.addTransaction(new Transaction(6, 2, TransactionType.DEBIT, 20, 6));

        Map<Integer, Double> avg = mgr.getAverageTransactionAmountByAccount();

        assertAlmost(40.0, avg.get(1), 0.0001);
        assertAlmost(50.0, avg.get(2), 0.0001);
    }

    static void testGetPaymentFeesByAccount() {
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));

        mgr.addTransaction(new Transaction(1, 1, TransactionType.CREDIT, 10, 1));
        mgr.addTransaction(new Transaction(2, 1, TransactionType.DEBIT, 10, 2));
        mgr.addTransaction(new Transaction(3, 1, TransactionType.DEBIT, 10, 3));

        Map<Integer, Integer> fees = mgr.getPaymentFeesByAccount();
        assertEquals(5, fees.get(1)); // 1 + 2 + 2
    }


    static void testGetTransactionFees() {
        AccountManager mgr = new AccountManager();
        mgr.addAccount(new Account(1, "Alice"));

        mgr.addTransaction(new Transaction(1, 1, TransactionType.CREDIT, 10, 1));
        mgr.addTransaction(new Transaction(2, 1, TransactionType.DEBIT, 10, 2));
        mgr.addTransaction(new Transaction(3, 1, TransactionType.CREDIT, 10, 3));
        mgr.addTransaction(new Transaction(4, 1, TransactionType.DEBIT, 10, 4)); // $2
        mgr.addTransaction(new Transaction(5, 1, TransactionType.CREDIT, 10, 5)); // $1

        Map<Integer, Double> fees = mgr.getTransactionFees();
        assertAlmost(3.0, fees.get(1), 0.0001);
    }
}


