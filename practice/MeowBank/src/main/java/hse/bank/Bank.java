package hse.bank;

import java.util.Date;
import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import hse.bank.facade.HSEBank;
import hse.bank.params.ReportFormat;

@SpringBootApplication
public class Bank {

	private static HSEBank hseBank;

	private static Scanner scanner;

	private static void parseCommand(String[] args) {
		if (args.length == 0) {
			System.out.println("Необходимо указать команду.");
			return;
		}

		String command = args[0];

		try {
			switch (command) {
				case "СоздатьАккаунт":
					if (args.length != 2) {
						System.out.println("Необходимо указать имя аккаунта.");
						return;
					}
					hseBank.createBankAccount(args[1]);
					break;

				case "СоздатьКатегорию":
					if (args.length != 3) {
						System.out.println("Необходимо указать имя категории и тип (true - доход, false - расход).");
						return;
					}
					hseBank.createCategory(args[1], Boolean.parseBoolean(args[2]));
					break;

				case "ДобавитьОперацию":
					if (args.length != 5) {
						System.out.println(
								"Необходимо указать ID аккаунта, сумму, описание и ID категории.");
						return;
					}
					int bankAccountId = Integer.parseInt(args[1]);
					double amount = Double.parseDouble(args[2]);
					Date date = new Date();
					String description = args[3];
					int categoryId = Integer.parseInt(args[4]);
					hseBank.addOperation(bankAccountId, amount, date, description, categoryId);
					break;

				case "ИзменитьОписаниеОпирации":
					if (args.length != 3) {
						System.out.println("Необходимо указать ID операции и новое описание.");
						return;
					}
					int operationId = Integer.parseInt(args[1]);
					String newDescription = args[2];
					hseBank.editOperationDescription(operationId, newDescription);
					break;

				case "ИзменитьКатегориюОперации":
					if (args.length != 3) {
						System.out.println("Необходимо указать ID операции и новый ID категории.");
						return;
					}
					int opId = Integer.parseInt(args[1]);
					int newCategoryId = Integer.parseInt(args[2]);
					hseBank.editOperationCategory(opId, newCategoryId);
					break;

				case "ИзменитьИмяАккаунта":
					if (args.length != 3) {
						System.out.println("Необходимо указать ID аккаунта и новое имя.");
						return;
					}
					int accountId = Integer.parseInt(args[1]);
					String newAccountName = args[2];
					hseBank.editAccountName(accountId, newAccountName);
					break;

				case "ИзменитьИмяКатегории":
					if (args.length != 3) {
						System.out.println("Необходимо указать ID категории и новое имя.");
						return;
					}
					int catId = Integer.parseInt(args[1]);
					String newCategoryName = args[2];
					hseBank.editCategoryName(catId, newCategoryName);
					break;

				case "ИзменитьТипКатегории":
					if (args.length != 3) {
						System.out
								.println("Необходимо указать ID категории и новый тип (true - доход, false - расход).");
						return;
					}
					int categoryIdToEdit = Integer.parseInt(args[1]);
					Boolean newType = Boolean.parseBoolean(args[2]);
					hseBank.editCategoryType(categoryIdToEdit, newType);
					break;

				case "УдалитьОперацию":
					if (args.length != 2) {
						System.out.println("Необходимо указать ID операции.");
						return;
					}
					int operationIdToDelete = Integer.parseInt(args[1]);
					hseBank.deleteOperation(operationIdToDelete);
					break;

				case "УдалитьАккаунт":
					if (args.length != 2) {
						System.out.println("Необходимо указать ID аккаунта.");
						return;
					}
					int accountIdToDelete = Integer.parseInt(args[1]);
					hseBank.deleteAccount(accountIdToDelete);
					break;

				case "УдалитьКатегорию":
					if (args.length != 2) {
						System.out.println("Необходимо указать ID категории.");
						return;
					}
					int categoryIdToDelete = Integer.parseInt(args[1]);
					hseBank.deleteCategory(categoryIdToDelete);
					break;

				case "ВывестиАккаунты":
					hseBank.printAccounts();
					break;

				case "ВывестиКатегории":
					hseBank.printCategories();
					break;

				case "ВывестиОперации":
					hseBank.printOperations();
					break;
				case "ВывестиОбщийБаланс":
					hseBank.countAllMoney();
					break;

				default:
					System.out.println("Неизвестная команда: " + command);
					break;
			}
		} catch (NumberFormatException e) {
			System.out.println("Ошибка формата числа: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Произошла ошибка: " + e.getMessage());
		}
	}

	public static void main(String[] args) {

		var context = SpringApplication.run(Bank.class, args);

		hseBank = context.getBean(HSEBank.class);

		scanner = new Scanner(System.in);

		while (true) {
			System.out.println("Введите формат из которого будут загружены данные (CSV/JSON/YAML): ");
			String format = scanner.nextLine().trim();
			if (format.equalsIgnoreCase("csv") ||
					format.equalsIgnoreCase("json") ||
					format.equalsIgnoreCase("yaml")) {
				hseBank.init(ReportFormat.valueOf(format.toUpperCase()));
				break;
			} else {
				System.out.println("Выберите доступный формат.");
			}
		}

		while (true) {
			System.out.print("Введите команду: ");
			String command = scanner.nextLine().trim();
			if (command.equalsIgnoreCase("Выход")) {
				break;
			}
			String[] commandParts = command.split("\\s+");
			parseCommand(commandParts);
		}

		scanner.close();
	}
}