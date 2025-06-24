package expenseTracker.expenseStorage;

import java.math.BigInteger;

/**
 * Interface for expense storage.
 * 
 * "The user should be able to enter an expense by providing category, amount, and data.
 * The application should provide:
 * 1. Total expense
 * 2. Total expense by category
 * 3. Expense trend
 * 4. Highest and lowest spend category
 * All data can be kept in memory or in a file. A database is not necessary."
 */
public interface IExpenseStorage {
	/* "The user should be able to enter an expense by providing category, amount, and data." */ 
	public void enterExpense(ExpenseRecord expense);
	
	/* app should provide "Total expense" */
	public BigInteger findTotalExpense();
	
	/* app should provide "Total expense by category" */
	public BigInteger findTotalExpenseByCategory(String category);
	
	/* app should provide "Expense trend" */
	public void findExpenseTrend();
	
	/* app should provide "Highest and lowest spend category" */
	public String findMostExpensiveCategory();
	public String findLeastExpensiveCategory();
}
