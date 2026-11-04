package sales.view;

import java.sql.SQLException;
import java.util.Scanner;
import sales.controller.InvoiceController;
import sales.model.Invoice;
import logging.AppLogger;

public class InvoiceView {

    private final Scanner scanner = new Scanner(System.in);
    private final InvoiceController controller = new InvoiceController();

    public void showMenu() {

        int option = 0;

        do {
            System.out.println("\n========== INVOICE MENU ==========");
            System.out.println("1. Generate invoice");
            System.out.println("2. Find invoice by sale ID");
            System.out.println("3. Return");
            System.out.print("Select an option: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid option.");
                scanner.nextLine();
                continue;
            }

            option = scanner.nextInt();
            scanner.nextLine();

            AppLogger.logAction("INVOICES", "Selected option: " + option);
            
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

                        Invoice invoice = controller.createInvoice(saleId);

                        System.out.println("Invoice generated successfully.");
                        printInvoice(invoice);
                        break;
                    }

                    case 2: {
                        System.out.print("Sale ID: ");

                        if (!scanner.hasNextInt()) {
                            System.out.println("Invalid sale ID.");
                            scanner.nextLine();
                            break;
                        }

                        int saleId = scanner.nextInt();
                        scanner.nextLine();

                        Invoice invoice = controller.findInvoiceBySaleId(saleId);

                        if (invoice == null) {
                            System.out.println("Invoice not found.");
                        } else {
                            printInvoice(invoice);
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

    private void printInvoice(Invoice invoice) {

        System.out.println("\n========== INVOICE ==========");
        System.out.println("Invoice ID: " + invoice.getId());
        System.out.println("Invoice number: " + invoice.getInvoiceNumber());
        System.out.println("Sale ID: " + invoice.getSaleId());
        System.out.println("Issue date: " + invoice.getIssueDate());
        System.out.println("Subtotal: " + invoice.getSubtotal());
        System.out.println("Discount: " + invoice.getTotalDiscount());
        System.out.println("Tax: " + invoice.getTaxAmount());
        System.out.println("Total: " + invoice.getTotalAmount());
        System.out.println("CUFE: " + invoice.getCufe());
        System.out.println("QR data: " + invoice.getQrLink());
        System.out.println("=============================");
    }
}