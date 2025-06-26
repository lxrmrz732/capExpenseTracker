package expenseTracker.expenseStorage;

import java.math.BigInteger;
import java.util.Date;

/**
 * Store the data of each expense.
 * Each expense needs a category, an amount, some "data" (a typo?)
 * and a timestamp of when it happened.
 */
record ExpenseRecord(String category, BigInteger amount, String data, Date date) {};
