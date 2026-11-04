
package sales.view;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sales.controller.SaleController;
import sales.model.Sale;
import sales.model.SaleDetail;

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

            option = readInt("Select an option: ");

            try {
                switch (option) {

                    case 1: {

                        System.out.println("\n========== CREATE SALE ==========");

                        System.out.print("Sale number: ");
                        String saleNumber = scanner.nextLine().trim();

                        if (saleNumber.isEmpty() || saleNumber.length() > 50) {
                            System.out.println("Invalid sale number.");
                            break;
                        }

                        int customerId = readPositiveInt("Customer ID: ");

                        System.out.print("Registered by: ");
                        String registeredBy = scanner.nextLine().trim();

                        if (registeredBy.isEmpty() || registeredBy.length() > 100) {
                            System.out.println("Invalid user.");
                            break;
                        }

                        List<SaleDetail> details = new ArrayList<>();
                        String addMore;

                        do {
                            int productId = readPositiveInt("Product ID: ");

                            System.out.println("\nAvailable lots (FEFO):");
                            controller.findAvailableLotsByProduct(productId);

                            int lotId = readPositiveInt("Lot ID: ");
                            double quantity = readPositiveDouble("Quantity: ");

                            SaleDetail detail = new SaleDetail(
                                    0,
                                    lotId,
                                    quantity,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0,
                                    0
                            );

                            details.add(detail);

                            System.out.print("Add another product? (Y/N): ");
                            addMore = scanner.nextLine().trim();

                        } while (addMore.equalsIgnoreCase("Y"));

                        System.out.print("Confirm sale? (Y/N): ");
                        String confirm = scanner.nextLine().trim();

                        if (!confirm.equalsIgnoreCase("Y")) {
                            System.out.println("Sale cancelled.");
                            break;
                        }

                        Sale sale = new Sale(
                                0,
                                saleNumber,
                                customerId,
                                null,
                                0,
                                0,
                                0,
                                0,
                                1,
                                registeredBy
                        );

                        int saleId = controller.confirmSale(sale, details);

                        System.out.println("\nSale confirmed successfully.");
                        System.out.println("Sale ID: " + saleId);
                        System.out.println("Subtotal: " + sale.getSubtotal());
                        System.out.println("Discount: " + sale.getTotalDiscount());
                        System.out.println("Tax: " + sale.getTotalTax());
                        System.out.println("Total: " + sale.getTotalAmount());

                        break;
                    }

                    case 2: {

                        int id = readPositiveInt("Sale ID: ");
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

                        int productId = readPositiveInt("Product ID: ");
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

    private int readInt(String message) {

        while (true) {

            System.out.print(message);
            String input = scanner.nextLine();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }

    private int readPositiveInt(String message) {

        while (true) {

            int number = readInt(message);

            if (number > 0) {
                return number;
            }

            System.out.println("Number must be positive.");
        }
    }

    private double readPositiveDouble(String message) {

        while (true) {

            System.out.print(message);
            String input = scanner.nextLine();

            try {
                double number = Double.parseDouble(input);

                if (Double.isFinite(number)
                        && number > 0
                        && Math.abs(number * 100 - Math.rint(number * 100)) < 0.000001) {
                    return number;
                }

                System.out.println("Enter a positive quantity with at most two decimals.");

            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity.");
            }
        }
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
