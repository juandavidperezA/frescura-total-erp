
package sales.view;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import config.DatabaseConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import sales.controller.SaleController;
import sales.controller.InvoiceController;
import sales.model.Sale;
import sales.model.SaleDetail;
import sales.model.Invoice;
import sales.service.InvoiceImageGenerator;
import logging.AppLogger;

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
            
            AppLogger.logAction("SALES", "Selected option: " + option);
                    
            try {
                switch (option) {

                    case 1: {

                        System.out.println("\n========== CREATE SALE ==========");

                        System.out.print("Sale number: ");
                        String saleNumber = scanner.nextLine().trim();

                        if (saleNumber.isEmpty() || saleNumber.length() > 50) {
                            System.out.println("Invalid sale number.");
                            AppLogger.logError("SALES", "INVALID_SALE_NUMBER");
                            break;
                        }

                        int customerId = readPositiveInt("Customer ID: ");

                        System.out.print("Registered by: ");
                        String registeredBy = scanner.nextLine().trim();

                        if (registeredBy.isEmpty() || registeredBy.length() > 100) {
                            System.out.println("Invalid user.");
                            AppLogger.logError("SALES", "INVALID_REGISTERED_USER");
                            break;
                        }

                        List<SaleDetail> details = new ArrayList<>();
                        String addMore;

                        do {

                            int productId = readPositiveInt("Product ID: ");
                                System.out.println("\nAvailable lots (FEFO):");
                                controller.findAvailableLotsByProduct(productId);

                                int lotId;

                                while (true) {
                                    lotId = readPositiveInt("Lot ID: ");

                                    if (isLotFromProduct(lotId, productId)) {
                                        break;
                                    }

                                    System.out.println("Error: Lot does not belong to selected product.");

                                    AppLogger.logError("SALES",
                                            "INVALID_LOT_PRODUCT | Product ID: " + productId
                                            + " | Lot ID: " + lotId);
                                }

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
                            AppLogger.logAction("SALES",
                            "SALE_CANCELLED | Sale number: " + saleNumber);
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
                        AppLogger.logAction("SALES",
                            "SALE_CONFIRMED | Sale ID: " + saleId
                            + " | Sale number: " + saleNumber
                            + " | Customer ID: " + customerId
                            + " | User: " + registeredBy
                            + " | Total: " + sale.getTotalAmount());
                        
                        for (SaleDetail detail : details) {
                                AppLogger.logAction("SALES",
                                    "SALE_DETAIL | Sale ID: " + saleId
                                    + " | Lot ID: " + detail.getLotId()
                                    + " | Quantity: " + detail.getQuantitySold()
                                    + " | Unit price: " + detail.getUnitPrice());
                            }

                        System.out.println("\nSale confirmed successfully.");
                        System.out.println("Sale ID: " + saleId);
                        System.out.println("Subtotal: " + sale.getSubtotal());
                        System.out.println("Discount: " + sale.getTotalDiscount());
                        System.out.println("Tax: " + sale.getTotalTax());
                        System.out.println("Total: " + sale.getTotalAmount());

                        try {

                            InvoiceController invoiceController = new InvoiceController();

                            Invoice invoice = invoiceController.createInvoice(saleId);

                            InvoiceImageGenerator generator = new InvoiceImageGenerator();

                            Path invoicePath = generator.generate(invoice);

                        System.out.println("Invoice saved: " + invoicePath);

                        AppLogger.logAction("INVOICES",
                            "INVOICE_SAVED | Sale ID: " + saleId
                            + " | Invoice ID: " + invoice.getId()
                            + " | File: " + invoicePath);

                        } catch (Exception e) {
                            AppLogger.logError("INVOICES",
                                "INVOICE_GENERATION_FAILED | Sale ID: "
                                + saleId + " | " + e.getMessage());
                            System.out.println(
                                    "Sale confirmed, but invoice generation failed: "
                                    + e.getMessage()
                            );
                        }

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
                AppLogger.logError("SALES",
                    "DATABASE_ERROR | " + e.getMessage());
                System.out.println("Database error: " + e.getMessage());
            }

        } while (option != 5);
    }

    private int readInt(String message) {

        while (true) {

            System.out.print(message);
            String input = scanner.nextLine().trim();

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
            String input = scanner.nextLine().trim();

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

    // Ensures the selected lot belongs to the requested product.
    private boolean isLotFromProduct(int lotId, int productId)
            throws SQLException {

        String sql = "SELECT id_producto FROM lotes_inventario WHERE id_lote = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, lotId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("id_producto") == productId;
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
