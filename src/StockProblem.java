// ################################### STOCK PROBLEM #####################################
//package com.example.employee.demo;


/*
We are developing a stock trading data management software that tracks the prices of different stocks over time and provides useful statistics.

The program includes three classes: `Stock`, `PriceRecord`, and `StockCollection`.

Classes:
* The `Stock` class represents data about a specific stock.
* The `PriceRecord` class holds information about a single price record for a stock.
* The `StockCollection` class manages a collection of price records for a particular stock and provides methods to retrieve useful statistics about the stock's prices.

To begin with, we present you with two tasks:
1-1) Read through and understand the code below. Please take as much time as necessary, and feel free to run the code.
1-2) The test for StockCollection is not passing due to a bug in the code. Make the necessary changes to StockCollection to fix the bug.

/ / 2) We want to add a new function called "getBiggestChange" to the StockCollection
class. This function calculates and returns the largest change in stock price between any
two consecutive days in the price records of a stock along with the dates of the change in a
list. For example, let's consider the following price records of a stock: Price Records: Price:
110
Date:
112
90
105 2023-06-29 2023-07-01 2023-06-25 2023-07-06 Stock price changes (sorted based on
date): Date:
2023-06-25 -> 2023-06-29 -> 2023-07-01 -> 2023-07-06 Price:
Change:
90 ->
+20
110 ->
+2
112 -> -7 105 In this case, the biggest change in the stock price was +20, which occurred
between 2023-06-25 and 2023-06-29. In this case, the function should return [20, "2023-
06-25", "2023-06-29"] Two days are considered consecutive if there are no other days' data
in between them in the price records based on their dates. To assist you in testing this new
function, we have provided the testGetBiggestChange function. / / We are currently
updating our system to include information about stock transactions. As part of this
update, we have introduced two new classes:
1. The Transaction class represents data about buy/sell transaction of a stock. It
includes data about the stock, the type of transaction (buy/sell), the date of
transaction and the quantity of the stocks.
2. The Tradebook class represents a list of transactions performed by a user. You are
provided with the code for the above classes. To complete the updates, we need to
add the getTotal function to the Tradebook class: 3-1) The getTotal function
should be used to get the total price of all the stocks owned by the user at the latest
date. This functions helps the user to calculate the current value of their portfolio.
This function takes the stockCollections list as input which contains
StockCollections for all stocks. The StockCollections list is guaranteed to contain
all stocks which would be present in the Tradebook and the stock collection for
each stock is guaranteed to have the price record for all transaction dates. To assist
you in testing these new functions, we have provided the testTradebook function.
*/

import java.util.*;
import org.junit.*;

class Stock {
    /** Data about a particular stock. */
    String symbol; // String, the symbol of the stock
    String name; // String, the name of the stock

    Stock(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Stock stock = (Stock) other;
        return symbol.equals(stock.symbol) && name.equals(stock.name);
    }
    @Override
    public int hashCode() {
        return java.util.Objects.hash(symbol, name);
    }
}

class PriceRecord {
    /** Data and methods about a single price record of a stock. */
    Stock stock; // Stock object representing the stock
    int price; // int, the price of the stock
    String date; // String, the date of the price record is of the format "YYYY-MM-DD"

    PriceRecord(Stock stock, int price, String date) {
        this.stock = stock;
        this.price = price;
        this.date = date;
    }
}

class StockCollection {
    /**
     * Data for a collection of price records for a particular stock, and methods for
     * getting useful statistics about the stock's prices.
     */
    List<PriceRecord> priceRecords = new ArrayList<>(); // list of PriceRecord objects, the price records for this particular stock
    Stock stock; // Stock, the Stock this StockCollection is for

    StockCollection(Stock stock) {
        this.stock = stock;
    }

    int getNumPriceRecords() {
        /** Returns the number of PriceRecords in this StockCollection */
        return priceRecords.size();
    }


    void addPriceRecord(PriceRecord priceRecord) {
        /** Adds a PriceRecord to this StockCollection. */
        if (!priceRecord.stock.equals(this.stock)) {
            throw new IllegalArgumentException("PriceRecord's Stock is not the same as the StockCollection's");
        }
        priceRecords.add(priceRecord);
    }

    int getMaxPrice() {
        /** Return the maximum price recorded in this StockCollection. */
        return priceRecords.stream().mapToInt(record -> record.price).max().
                orElse(-1);
    }

    int getMinPrice() {
        /** Return the minimum price recorded in this StockCollection. */
        return priceRecords.stream().mapToInt(record -> record.price).min().
                orElse(-1);
    }


    double getAvgPrice() {
        /** Return the average price recorded in this StockCollection. */
//      if (priceRecords.isEmpty()) {
//            return -1.0;
//        }
        if(priceRecords.isEmpty()){
            return -1.0;
        }
        double total = priceRecords.stream().mapToInt(record -> record.price).sum();
        return (total / priceRecords.size());
//        return priceRecords.stream().mapToInt(r->r.price).average().orElse(-1);
    }
//quazi implementation area

//    quazi implementation area close

//    approach what we are using => sorting ->looping -> comparing
//    1. If size < 2 → return null
//            2. Sort by date
//3. Loop from i=1
//            4. change = current - previous
//5. track max absolute change
//6. return result

//    approach -> sort -> loop -> return


    Object[] getBiggestChange() {

        //sorting(date) -> compare(price) -> absolutevalue -> max
//        yyyy-mm-dd
        if (priceRecords.size() < 2) return null; // if we have less than 2 records we can't calculate any change

        // sort by date by ascending order so that we can compare consecutive days
        priceRecords.sort(Comparator.comparing(r -> r.date));

        // Variable to store the maximum change found so far
        // Can be positive (increase) or negative (decrease)
        int maxChange = 0;

        // These will store the dates between which max change occurred
        String from = "";
        String to = "";

// Loop through the list starting from index 1
        // Because we compare current with previous
        for (int i = 1; i < priceRecords.size(); i++) {

            int change = priceRecords.get(i).price - priceRecords.get(i - 1).price;
            // Compare absolute value of change
            // Because we want biggest change (increase OR decrease)
            if (Math.abs(change) > Math.abs(maxChange)) {
                // Update maxChange
                maxChange = change;
                // Store the date range where this change happened
                from = priceRecords.get(i - 1).date; //prev date
                to = priceRecords.get(i).date; //curr date
            }
        }
        // Return result as Object array
        return new Object[]{maxChange, from, to};
    }


}

class Transactions {
    Stock stock;
    String transactionType; // "buy" or "sell"
    String date;
    int quantity;


    Transactions(Stock stock, String transactionType, String date, int quantity) {
        this.stock = stock;
        this.transactionType = transactionType;
        this.date = date;
        this.quantity = quantity;
    }
}

class Tradebook {
    ArrayList<Transactions> transactions = new ArrayList<>();

    void addTransaction(Transactions transaction) {
        transactions.add(transaction);
    }
//   quazi implementation area

//    quazi implementation area closed


//    approach
//Aggregate transactions
//    Map stocks → quantity
//    Get latest price
//            Multiply
//    approach map1(stock, quantity)  <--> map2(stock, price ) --> loop (multiply and sum) --> return


    int getTotal(ArrayList<StockCollection> stockCollections) {

        // Step 1: calculate holdings (how many stocks user currently owns)
        Map<Stock, Integer> holdings = new HashMap<>();// holdings {google: 17, apple: 20, ms: 30} <===>  map pricerecord {google: 200(price)}

        // Loop through all transactions (buy/sell)
        for (Transactions t : transactions) {
            // Get current quantity of this stock (default = 0 if not present)
            int qty = holdings.getOrDefault(t.stock, 0);
            // If transaction is BUY → add quantity
            if (t.transactionType.equals("buy")) {
                qty += t.quantity;
            }
            // If transaction is SELL → subtract quantity
            else {
                qty -= t.quantity;
            }
//update the holdings map
            holdings.put(t.stock, qty);
        }

        // Step 2: get latest prices of each stock
        Map<Stock, Integer> latestPrices = new HashMap<>();

        for (StockCollection sc : stockCollections) {
            // Find the latest price record (max date)
            PriceRecord latest =
                    sc.priceRecords.stream().max(Comparator.comparing(r -> r.date)).orElse(null);

            // If found, store stock → latest price
            if (latest != null) {
                latestPrices.put(sc.stock, latest.price);
            }
        }

        // Step 3: calculate total portfolio value
        int total = 0;
        // Loop through each stock user owns
        for (Stock stock : holdings.keySet()) {
            // Multiply quantity × latest price
            total += holdings.get(stock) * latestPrices.get(stock);
        }
        return total;
    }
}

public class StockProblem {

    public static void main(String[] args) {
        testPriceRecord();
        testStockCollection();
        testGetBiggestChange();
        testTradebook();
        System.out.println("✅ All tests passed!");
    }

    public static void testPriceRecord() {
        // Test basic PriceRecord functionality
        System.out.println("Running testPriceRecord");
        Stock testStock = new Stock("AAPL", "Apple Inc.");
        PriceRecord testPriceRecord = new PriceRecord(testStock, 100, "2023-07-01");

        Assert.assertEquals(testPriceRecord.stock, testStock);
        Assert.assertEquals(testPriceRecord.price, 100);
        Assert.assertEquals(testPriceRecord.date, "2023-07-01");
    }

    private static StockCollection makeStockCollection(Stock stock, Object[][] priceData) {
        StockCollection stockCollection = new StockCollection(stock);
        for (Object[] priceRecordData : priceData) {
            PriceRecord priceRecord = new PriceRecord(stock, (int) priceRecordData[0], (String) priceRecordData[1]);
            stockCollection.addPriceRecord(priceRecord);
        }
        return stockCollection;
    }

    public static void testStockCollection() {
        System.out.println("Running testStockCollection");
        // Test basic StockCollection functionality

        Stock testStock = new Stock("AAPL", "Apple Inc.");
        StockCollection stockCollection = new StockCollection(testStock);
        Assert.assertEquals(0, stockCollection.getNumPriceRecords());
        Assert.assertEquals(-1, stockCollection.getMaxPrice());
        Assert.assertEquals(-1, stockCollection.getMinPrice());
        Assert.assertEquals(-1.0, stockCollection.getAvgPrice(), 0.001);

        /*
         * Price Records: Price: 110 112 90 105 Date: 2023-06-29 2023-07-01 2023-06-28
         * 2023-07-06
         */
        Object[][] priceData = { { 110, "2023-06-29" }, { 112, "2023-07-01" }, { 90, "2023-06-28" },
                { 105, "2023-07-06" } };
        testStock = new Stock("AAPL", "Apple Inc.");
        stockCollection = makeStockCollection(testStock, priceData);

        Assert.assertEquals(priceData.length, stockCollection.getNumPriceRecords());
        Assert.assertEquals(112, stockCollection.getMaxPrice());
        Assert.assertEquals(90, stockCollection.getMinPrice());
        Assert.assertEquals(104.25, stockCollection.getAvgPrice(), 0.1);
    }

    static void testGetBiggestChange() {
        System.out.println("Running testGetBiggestChange");

        Stock stock = new Stock("AAPL", "Apple Inc.");
        StockCollection sc = new StockCollection(stock);

        assert sc.getBiggestChange() == null;

        Object[][] data = {
                {110, "2023-06-29"},
                {112, "2023-07-01"},
                {90, "2023-06-25"},
                {105, "2023-07-06"}
        };

        sc = makeStockCollection(stock, data);

        assert Arrays.equals(
                new Object[]{20, "2023-06-25", "2023-06-29"},
                sc.getBiggestChange()
        );
    }
    static void testTradebook() {
        System.out.println("Running testTradebook");

        Tradebook tb = new Tradebook();
        Stock stock = new Stock("AAPL", "Apple Inc.");

        Object[][] data = {
                {110, "2023-06-29"},
                {112, "2023-07-01"},
                {90, "2023-06-25"},
                {105, "2023-07-06"}
        };

        StockCollection sc = makeStockCollection(stock, data);
        ArrayList<StockCollection> list = new ArrayList<>();
        list.add(sc);

        tb.addTransaction(new Transactions(stock, "buy", "2023-06-25", 10));
        assert tb.getTotal(list) == 1050;

        tb.addTransaction(new Transactions(stock, "buy", "2023-06-29", 5));
        tb.addTransaction(new Transactions(stock, "sell", "2023-07-01", 3));

        assert tb.getTotal(list) == 1260;
    }
}