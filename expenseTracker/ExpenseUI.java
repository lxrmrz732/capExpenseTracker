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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import expenseTracker.expenseStorage.*;

public class ExpenseUI {
	private static final IExpenseStorage expenseTracker = new ArrayExpenseStorage();
	private static final Scanner scanner = new Scanner(System.in);
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

	public static void main(String[] args) {
		run(); // TODO: refactor
	}

	/**
	 * The main application loop. Displays a menu and processes user input.
	 */
	public static void run() {
		while (true) {
			printMenu();
			int choice = getUserChoice(); // TODO: print BigInteger as decimal with 2 places

			switch (choice) {
				case 1:
					addExpense();
					break;
				case 2:
					printExpenses();
					break;
				case 3:
					viewTotalExpense();
					break;
				case 4:
					viewExpenseByCategory();
					break;
				case 5:
					viewHighestAndLowestSpend(); // TODO: rename
					break;
				case 6:
					viewExpenseTrend();
					break;
				case 7:
					System.out.println("Exiting application. Goodbye!");
					return;
				default:
					System.out.println("Invalid choice. Please try again.");
			}
			System.out.println(); // For spacing
		}
	}

	/**
	 * Prints the main menu options to the console.
	 */
	private static void printMenu() {
		System.out.println("--- Expense Tracker Menu ---");
		System.out.println("1. Add a new expense");
		System.out.println("2. View all expenses");
		System.out.println("3. View total expense");
		System.out.println("4. View total expense by category");
		System.out.println("5. View highest and lowest spend categories");
		System.out.println("6. View expense trend (by month)");
		System.out.println("7. Exit");
		System.out.print("Enter your choice: ");
	}

	/**
	 * Reads and validates the user's menu choice.
	 * 
	 * @return The user's integer choice.
	 */
	private static int getUserChoice() {
		while (!scanner.hasNextInt()) {
			System.out.print("Invalid input. Please enter a number: ");
			scanner.next(); // Consume the invalid input
		}
		return scanner.nextInt();
	}

	/**
	 * Handles the logic for adding a new expense based on user input.
	 */
	private static void addExpense() {
		scanner.nextLine(); // Consume newline left-over

		System.out.print("Enter expense category: ");
		String category = scanner.nextLine();

		BigInteger amount = null;
		while (amount == null) {
			System.out.print("Enter expense amount: ");
			try {
				amount = new BigInteger(scanner.next());
				if (amount.compareTo(BigInteger.ZERO) < 0) {
					System.out.println("Amount cannot be negative.");
					amount = null; // Reset for re-looping
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid amount. Please enter a valid number.");
			}
		}

		scanner.nextLine(); // Consume newline

		Date date = null;
		while (date == null) {
			System.out.print("Enter date (MM/dd/yyyy): ");
			String dateString = scanner.nextLine();
			try {
				date = DATE_FORMAT.parse(dateString);
			} catch (ParseException e) {
				System.out.println("Invalid date format. Please use MM/dd/yyyy.");
			}
		}

		expenseTracker.enterExpense(new ExpenseRecord(category, amount, "", date)); // TODO: add data
		System.out.println("Expense added successfully!");
	}

	/**
	 * Prints a list of all recorded expenses.
	 */
	private static void printExpenses() {
		System.out.println("\n--- All Expenses ---");
		List<ExpenseRecord> expenses = expenseTracker.listAllExpenses();
		if (expenses.isEmpty()) {
			System.out.println("No expenses recorded yet.");
		} else {
			expenses.forEach(System.out::println);
		}
	}

	/**
	 * Displays the single total expense amount.
	 */
	private static void viewTotalExpense() {
		System.out.println("\n--- Total Expense ---");
		System.out.printf("Total expense: $%.2f%n", expenseTracker.getTotalExpense());
	}

	/**
	 * Displays the total expense for each category.
	 */
	private static void viewExpenseByCategory() {
		System.out.println("\n--- Expense by Category ---");
		Map<String, BigInteger> categoryTotals = expenseTracker.getTotalExpenseByCategory();
		if (categoryTotals.isEmpty()) {
			System.out.println("No expenses to categorize.");
		} else {
			categoryTotals
					.forEach((category, total) -> System.out.printf("Category: %-15s Total: $%.2f%n", category, total));
		}
	}

	/**
	 * Displays the highest and lowest spending categories.
	 */
	private static void viewHighestAndLowestSpend() {
		System.out.println("\n--- Highest and Lowest Spend ---");
		System.out.println("Highest spend category: " + expenseTracker.getMostExpensiveCategory());
		System.out.println("Lowest spend category: " + expenseTracker.getLeastExpensiveCategory());
	}

	/**
	 * Displays the monthly spending trend.
	 */
	private static void viewExpenseTrend() {
		System.out.println("\n--- Monthly Expense Trend ---");
		Map<String, BigInteger> trendData = expenseTracker.getExpenseTrend();
		if (trendData.isEmpty()) {
			System.out.println("Not enough data to show a trend.");
		} else {
			trendData.forEach((month, total) -> System.out.printf("Month: %s, Total Spend: $%.2f%n", month, total));
		}
	}
}
