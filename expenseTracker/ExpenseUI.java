/*
 * ExpenseUI.java
 * 
 * Copyright 2025 Alex Ramírez <Reisen@6470beast>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

package expenseTracker;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import expenseTracker.expenseStorage.*;

public class ExpenseUI {
	/* - - - fields - - - */
	private static final IExpenseStorage EXPENSE_TRACKER = new ArrayExpenseStorage(null);
	private static final Scanner SCANNER = new Scanner(System.in);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

	/* - - - methods - - - */
	public static void main(String[] args) {
		/* symbols */
		int choice = 0;

		while (true) {
			printMenu();
			switch (choice = getUserChoice()) {
				case 1:
					addExpense();
					break;
				case 2:
					printExpenses();
					break;
				case 3:
					printTotalExpense();
					break;
				case 4:
					printExpenseByCategory();
					break;
				case 5:
					printCategoryExtremes();
					break;
				case 6:
					printExpenseTrend();
					break;
				case 7:
					System.out.println("Exiting application.");
					return;
				default:
					System.err.printf("Invalid choice: %d\n", choice);
			}
			System.out.println();
		}
	}

	/**
	 * Prints the main menu options to the console.
	 */
	private static void printMenu() {
		System.out.print("""
				- - - Expense Tracker Menu - - -
				1. Add a new expense
				2. View all expenses
				3. View total expense
				4. View total expense by category
				5. View most and least expensive categories
				6. View expense trend
				7. Exit
				""");
		System.out.print("Enter your choice: ");
	}

	/**
	 * Reads the user's menu choice from stdin.
	 * 
	 * @return The user's integer choice.
	 */
	private static int getUserChoice() {
		while (!SCANNER.hasNextInt()) {
			System.err.print("Invalid input. Enter an integer: ");
			SCANNER.next(); // Consume the invalid input
		}
		return SCANNER.nextInt();
	}

	/**
	 * Add a new expense based on user input from stdin.
	 */
	private static void addExpense() {
		/* symbols */
		String category = null;
		BigInteger amount = null;
		String data = null;
		Date date = null;

		SCANNER.nextLine(); // Consume newline left-over

		/* Get category */
		System.out.print("Enter expense category: ");
		category = SCANNER.nextLine();

		/* Get amount */
		while (amount == null) {
			System.out.print("Enter expense amount: ");
			try {
				amount = floatStringToInt(SCANNER.next());
				if (amount.compareTo(BigInteger.ZERO) < 0) {
					System.err.println("Amount cannot be negative.");
					amount = null; // Reset for re-looping
				}
			} catch (NumberFormatException e) {
				System.err.printf("Invalid amount.\nError message: %s\n",
						e.getMessage());
			}
		}
		SCANNER.nextLine(); // Consume newline

		/* Get data */
		System.out.print("Enter expense data: ");
		data = SCANNER.nextLine();

		/* Get date */
		while (date == null) {
			System.out.print("Enter date (MM/dd/yyyy): ");
			String dateString = SCANNER.nextLine();
			try {
				date = DATE_FORMAT.parse(dateString);
			} catch (ParseException e) {
				System.err.printf("Invalid date.\nError message: %s\n",
						e.getMessage());
			}
		}

		/* Submit expense to backend */
		EXPENSE_TRACKER.enterExpense(new ExpenseRecord(category, amount, data, date));
		System.out.println("Expense recorded.");
		return;
	}

	/**
	 * Prints a list of all recorded expenses.
	 */
	private static void printExpenses() {
		System.out.println("\n- - - All Expenses - - -");
		List<ExpenseRecord> expenses = EXPENSE_TRACKER.listAllExpenses();
		if (expenses.isEmpty()) {
			System.out.println("No expenses recorded yet.");
		} else {
			expenses.forEach(System.out::println);
		}
		return;
	}

	/**
	 * Displays the single total expense amount.
	 */
	private static void printTotalExpense() {
		System.out.println("\n- - - Total Expense - - -");
		System.out.printf("Total expense: $%.2f%n", intToFloat(EXPENSE_TRACKER.getTotalExpense()));
	}

	/**
	 * Displays the total expense for each category.
	 */
	private static void printExpenseByCategory() {
		System.out.println("\n- - - Expense by Category - - -");
		Map<String, BigInteger> categoryTotals = EXPENSE_TRACKER.getTotalExpenseByCategory();
		if (categoryTotals.isEmpty()) {
			System.out.println("No expenses to categorize.");
		} else {
			categoryTotals
					.forEach((category, total) -> System.out.printf(
							"Category: %-15s, Total expense: $%.2f%n", category, intToFloat(total)));
		}
	}

	/**
	 * Displays the highest and lowest spending categories.
	 */
	private static void printCategoryExtremes() {
		System.out.println("\n- - - Category Extremes - - -");
		System.out.println("Most expensive category: " + EXPENSE_TRACKER.getMostExpensiveCategory());
		System.out.println("Least expensive category: " + EXPENSE_TRACKER.getLeastExpensiveCategory());
	}

	/**
	 * Displays the total expense per month.
	 */
	private static void printExpenseTrend() {
		System.out.println("\n- - - Monthly Expense Trend - - -");
		Map<String, BigInteger> trendData = EXPENSE_TRACKER.getExpenseTrend();
		if (trendData.isEmpty()) {
			System.out.println("No expenses to show a trend.");
		} else {
			trendData.forEach((month, total) -> System.out.printf(
					"Month: %s, Total expense: $%.2f%n", month, intToFloat(total)));
		}
	}

	/**
	 * Convert the backend integer "amount" representation to a float for display.
	 * The BigInteger "amount" in an ExpenseRecord represents whole cents, so
	 * this function returns the equivalent dollar amount (with precision
	 * consequences).
	 * 
	 * @param intVal BigInteger representing cents to convert to dollars
	 * 
	 * @return a double representing the corresponding dollar amount
	 */
	private static double intToFloat(BigInteger intVal) {
		return intVal.doubleValue() / 100.0;
	}

	/**
	 * Convert the frontend floating-point "amount" representation
	 * to a BigInteger for storage.
	 * The BigInteger "amount" in an ExpenseRecord represents whole cents, so
	 * this function converts the equivalent dollar amount (with precision
	 * consequences).
	 * 
	 * @param floatString string representing dollars to convert to cents
	 * 
	 * @return a BigInteger representing the corresponding cent amount
	 */
	private static BigInteger floatStringToInt(String floatString) {
		BigDecimal bd = new BigDecimal(floatString);
		return bd.multiply(new BigDecimal(100)).toBigInteger();
	}
}
