
package logistics.view;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import logistics.controller.DeliveryController;
import sales.model.Sale;

public class DeliveryView {

    private final Scanner scanner = new Scanner(System.in);
    private final DeliveryController controller = new DeliveryController();

    public void showMenu() {

        int option = 0;

        do {
            System.out.println("\n========== LOGISTICS MENU ==========");
            System.out.println("1. Prepare sale");
            System.out.println("2. View dispatch panel");
            System.out.println("3. Return");
            System.out.print("Select an option: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid option.");
                scanner.nextLine();
                continue;
            }

            option = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (option) {

                    case 1: {

                        System.out.print("Sale ID: ");

                        if (!scanner.hasNextInt()) {
                            System.out.println("Invalid sale ID.");
                            scanner.nextLine();
                            break;
                        }

                        int saleId = scanner.nextInt();
                        scanner.nextLine();

                        if (saleId <= 0) {
                            System.out.println("Sale ID must be positive.");
                            break;
                        }

                        int updated = controller.prepareSale(saleId);

                        if (updated > 0) {
                            System.out.println("Sale moved to preparation successfully.");
                        } else {
                            System.out.println("Sale not found or cannot be prepared.");
                        }

                        break;
                    }

                    case 2: {

                        List<Sale> sales = controller.findSalesInPreparation();

                        if (sales.isEmpty()) {
                            System.out.println("No sales in preparation.");
                        } else {

                            System.out.println("\n========== DISPATCH PANEL ==========");

                            for (Sale sale : sales) {
                                System.out.println("----------------------------");
                                System.out.println("Sale ID: " + sale.getId());
                                System.out.println("Sale number: " + sale.getSaleNumber());
                                System.out.println("Customer ID: " + sale.getCustomerId());
                                System.out.println("Sale date: " + sale.getSaleDate());
                                System.out.println("Total: " + sale.getTotalAmount());
                            }
                        }

                        break;
                    }

                    case 3:
                        System.out.println("Returning to main menu...");
                        break;

                    default:
                        System.out.println("Invalid option.");
                        break;
                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }

        } while (option != 3);
    }
}
