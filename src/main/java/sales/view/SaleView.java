package sales.view;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import sales.controller.SaleController;
import sales.model.Sale;

public class SaleView {

    private final Scanner scanner = new Scanner(System.in);
    private final SaleController controller = new SaleController();

    public void showMenu() {

        int option = 0;

        do {
            System.out.println("\n========== SALES MENU ==========");
            System.out.println("1. Create and confirm sale");
            System.out.println("2. Find sale by ID");
            System.out.println("3. List all sales");
            System.out.println("4. Find available lots by product");
            System.out.println("5. Return");
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

                    case 1:
                        System.out.println("Sale confirmation is not available yet.");
                        break;

                    case 2: {
                        System.out.print("Sale ID: ");

                        if (!scanner.hasNextInt()) {
                            System.out.println("Invalid sale ID.");
                            scanner.nextLine();
                            break;
                        }

                        int id = scanner.nextInt();
                        scanner.nextLine();

                        if (id <= 0) {
                            System.out.println("Sale ID must be positive.");
                            break;
                        }

                        Sale sale = controller.findSaleById(id);

                        if (sale == null) {
                            System.out.println("Sale not found.");
                        } else {
                            printSale(sale);
                        }

                        break;
                    }

                    case 3: {
                        List<Sale> sales = controller.findAllSales();

                        if (sales.isEmpty()) {
                            System.out.println("No sales found.");
                        } else {
                            for (Sale sale : sales) {
                                printSale(sale);
                            }
                        }

                        break;
                    }

                    case 4: {
                        System.out.print("Product ID: ");

                        if (!scanner.hasNextInt()) {
                            System.out.println("Invalid product ID.");
                            scanner.nextLine();
                            break;
                        }

                        int productId = scanner.nextInt();
                        scanner.nextLine();

                        if (productId <= 0) {
                            System.out.println("Product ID must be positive.");
                            break;
                        }

                        controller.findAvailableLotsByProduct(productId);
                        break;
                    }

                    case 5:
                        System.out.println("Returning to main menu...");
                        break;

                    default:
                        System.out.println("Invalid option.");
                        break;
                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }

        } while (option != 5);
    }

    private void printSale(Sale sale) {

        System.out.println("\n----------------------------");
        System.out.println("Sale ID: " + sale.getId());
        System.out.println("Sale number: " + sale.getSaleNumber());
        System.out.println("Customer ID: " + sale.getCustomerId());
        System.out.println("Sale date: " + sale.getSaleDate());
        System.out.println("Subtotal: " + sale.getSubtotal());
        System.out.println("Discount: " + sale.getTotalDiscount());
        System.out.println("Tax: " + sale.getTotalTax());
        System.out.println("Total: " + sale.getTotalAmount());
        System.out.println("Status ID: " + sale.getStatusId());
        System.out.println("Registered by: " + sale.getRegisteredBy());
        System.out.println("----------------------------");
    }
}