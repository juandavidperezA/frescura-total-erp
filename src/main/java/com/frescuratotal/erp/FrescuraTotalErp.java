
package com.frescuratotal.erp;

import java.util.Scanner;
import sales.view.SaleView;
import sales.view.InvoiceView;
import logistics.view.DeliveryView;

public class FrescuraTotalErp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int option = 0;

        do {
            System.out.println("\n========== FRESCURA TOTAL ERP ==========");
            System.out.println("1. Sales");
            System.out.println("2. Invoices");
            System.out.println("3. Logistics");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid option.");
                scanner.nextLine();
                continue;
            }

            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {

                case 1:
                    SaleView saleView = new SaleView();
                    saleView.showMenu();
                    break;

                case 2:
                    InvoiceView invoiceView = new InvoiceView();
                    invoiceView.showMenu();
                    break;

                case 3:
                    DeliveryView deliveryView = new DeliveryView();
                    deliveryView.showMenu();
                    break;

                case 4:
                    System.out.println("Closing Frescura Total ERP...");
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }

        } while (option != 4);

        scanner.close();
    }
}
