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
    SimpleDateFormat trendFormat = new SimpleDateFormat("yyyy-MM"); // TODO: make constant

    /* - - - constructors - - - */
    /**
     * Default constructor; don't load data from the disk.
     */
    public ArrayExpenseStorage() {
        this.expenses = new ArrayList<ExpenseRecord>();
        filename = DEFAULT_FILE_NAME;
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
        return expenses.stream()
                .map(ExpenseRecord::amount)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }

    /**
     * Calculates the total expenses for each "category" String.
     * 
     * @return A map where keys are categories and values are total amounts.
     */
    public Map<String, BigInteger> getTotalExpenseByCategory() {
        return expenses.stream()
                .collect(Collectors.groupingBy(
                        ExpenseRecord::category,
                        Collectors.mapping(ExpenseRecord::amount,
                                Collectors.reducing(BigInteger.ZERO, BigInteger::add))));
    }

    /**
     * Calculates the total expenses for a given category.
     * 
     * @param category the "category" String to find
     * @return a BigInteger corresponding
     */
    public BigInteger getTotalExpenseByCategory(String category) {
        return this.getTotalExpenseByCategory().get(category);
    }

    /**
     * Identifies the category with the highest total spending.
     * 
     * @return The category with the highest spending, or "N/A" if no expenses
     *         exist.
     */
    public String getMostExpensiveCategory() {
        return getTotalExpenseByCategory().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Identifies the category with the lowest total spending.
     * 
     * @return The category with the lowest spending, or "N/A" if no expenses exist.
     */
    public String getLeastExpensiveCategory() {
        return getTotalExpenseByCategory().entrySet().stream()
                .min(Map.Entry.comparingByValue())
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
        return expenses.stream()
                .sorted((e1, e2) -> e1.date().compareTo(e2.date()))
                .collect(Collectors.groupingBy(
                        expense -> trendFormat.format(expense.date()),
                        Collectors.mapping(
                                ExpenseRecord::amount,
                                Collectors.reducing(BigInteger.ZERO, BigInteger::add))));
    }

    /**
     * Returns a list of all expenses.
     * 
     * @return An unmodifiable list of expenses.
     */
    public List<ExpenseRecord> getExpenses() {
        return Collections.unmodifiableList(this.expenses);
    }

    /**
     * Saves the current list of expenses to a CSV file.
     * This ensures data persistence between application runs.
     */
    private void saveExpensesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (ExpenseRecord expense : this.expenses) {
                writer.println(
                        expense.category() + "," +
                                expense.amount() + "," +
                                expense.data() + "," +
                                DATE_FORMAT.format(expense.date()));
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses to file: " + e.getMessage());
        }
    }

    /**
     * Loads expenses from the CSV file at startup.
     * This populates the expense list with previously saved data.
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
                        System.out.println("Skipping invalid expense record: " + line);
                    }
                }
            }
        } catch (IOException e) {
            // File might not exist on first run, which is fine.
            System.out.println("No previous expense data found. Starting fresh.");
        }
    }
}
