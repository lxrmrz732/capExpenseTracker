package expenseTracker.expenseStorage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;

public class ArrayExpenseStorage implements IExpenseStorage {
    /* - - - fields - - - */
    private final List<ExpenseRecord> expenses;
    private final String filename;
    private static final String DEFAULT_FILE_NAME = "expenses.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    private static final SimpleDateFormat TREND_FORMAT = new SimpleDateFormat("yyyy-MM");

    /* - - - constructors - - - */
    /**
     * Default constructor; don't load data from the disk.
     */
    public ArrayExpenseStorage() {
        this.expenses = new ArrayList<ExpenseRecord>();
        this.filename = DEFAULT_FILE_NAME;
    }

    /**
     * Parametrized constructor; load data from the file provided.
     * 
     * @param file the path to the expense file to load from
     */
    public ArrayExpenseStorage(String file) {
        this.expenses = new ArrayList<ExpenseRecord>();

        if (file == null || file.length() < 1) {
            file = DEFAULT_FILE_NAME;
        }

        this.filename = file;
        this.loadExpensesFromFile();
    }

    /* - - - methods - - - */
    /**
     * Adds a new expense to the tracker.
     * 
     * @param expense The ExpenseRecord to add to the internal List.
     */
    public void enterExpense(ExpenseRecord expense) {
        this.expenses.add(expense);
        this.saveExpensesToFile();
        return;
    }

    /**
     * Calculates the total of all expenses.
     * 
     * @return The total expense amount.
     */
    public BigInteger getTotalExpense() {
        return this.expenses.stream()
                .map(ExpenseRecord::amount)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    /**
     * Calculates the total expenses for each "category" String.
     * 
     * @return A map where keys are categories and values are total amounts.
     */
    public Map<String, BigInteger> getTotalExpenseByCategory() {
        return this.expenses.stream()
                .collect(Collectors.groupingBy(
                        ExpenseRecord::category,
                        Collectors.mapping(
                                ExpenseRecord::amount,
                                Collectors.reducing(BigInteger.ZERO, BigInteger::add))));
    }

    /**
     * Calculates the total expenses for a given category.
     * 
     * @param category the "category" String to find
     * 
     * @return a BigInteger corresponding to the expense
     *         of the given category.
     */
    public BigInteger getTotalExpenseByCategory(String category) {
        return this.getTotalExpenseByCategory().get(category);
    }

    /**
     * Identifies the category with the highest total expense.
     * 
     * @return a String representing the "category" with the greatest
     *         "amount" field , or null if no expenses exist.
     */
    public String getMostExpensiveCategory() {
        return this.getTotalExpenseByCategory().entrySet().stream()
                .max(Map.Entry.comparingByValue()) // max
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Identifies the category with the lowest total expense.
     * 
     * @return a String representing the "category" with the least
     *         "amount" field , or null if no expenses exist.
     */
    public String getLeastExpensiveCategory() {
        return this.getTotalExpenseByCategory().entrySet().stream()
                .min(Map.Entry.comparingByValue()) // min
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Provides a simple monthly expense trend.
     * It groups expenses by month and year and calculates the total for each.
     * 
     * @return A map where the key is the month-year (e.g., "2023-01") and the value
     *         is the total amount.
     */
    public Map<String, BigInteger> getExpenseTrend() {
        return this.expenses.stream()
                .sorted((e1, e2) -> e1.date().compareTo(e2.date()))
                .collect(Collectors.groupingBy(
                        expense -> TREND_FORMAT.format(expense.date()),
                        Collectors.mapping(
                                ExpenseRecord::amount,
                                Collectors.reducing(BigInteger.ZERO, BigInteger::add))));
    }

    /**
     * Return a list of all expenses.
     * 
     * @return An unmodifiable list of expenses.
     */
    public List<ExpenseRecord> listAllExpenses() {
        return Collections.unmodifiableList(this.expenses);
    }

    /**
     * Save the current list of expenses to a scuffed CSV file.
     */
    private void saveExpensesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(this.filename))) {
            for (ExpenseRecord expense : this.expenses) {
                writer.println(
                        expense.category() + "," +
                                expense.amount() + "," +
                                expense.data() + "," +
                                DATE_FORMAT.format(expense.date()));
            }
        } catch (IOException e) {
            System.err.println("Error encountered: " + e.getMessage() + ". Aborting file write.");
        }
    }

    /**
     * Load expenses from an existing CSV file at startup.
     */
    private void loadExpensesFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    try {
                        String category = parts[0];
                        BigInteger amount = new BigInteger(parts[1]);
                        String data = parts[2];
                        Date date = DATE_FORMAT.parse(parts[3]);
                        expenses.add(new ExpenseRecord(category, amount, data, date));
                    } catch (ParseException | NumberFormatException e) {
                        System.out.println("Invalid expense record: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error encountered: " + e.getMessage() + ". Aborting file read.");
        }
    }
}
