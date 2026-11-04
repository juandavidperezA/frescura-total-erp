
package com.frescuratotal.erp;

import com.frescuratotal.erp.config.DatabaseConnection;

// Suppliers
import com.frescuratotal.erp.suppliers.controller.SupplierController;
import com.frescuratotal.erp.suppliers.dao.SupplierDAO;
import com.frescuratotal.erp.suppliers.dao.SupplierProductDAO;
import com.frescuratotal.erp.suppliers.dao.SupplierReceiptDAO;
import com.frescuratotal.erp.suppliers.view.SupplierView;

// Inventory
import com.frescuratotal.erp.inventory.controller.InventoryController;
import com.frescuratotal.erp.inventory.dao.InventoryLotDAO;
import com.frescuratotal.erp.inventory.dao.QuarantineDAO;
import com.frescuratotal.erp.inventory.dao.QualityInspectionDAO;
import com.frescuratotal.erp.inventory.dao.InventoryWasteDAO;
import com.frescuratotal.erp.inventory.view.InventoryView;

// Sales and logistics
import sales.view.SaleView;
import sales.view.InvoiceView;
import logistics.view.DeliveryView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class FrescuraTotalErp {

    public static void main(String[] args) {

        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Database connection successful.");
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }

        // Suppliers module
        SupplierDAO supplierDAO = new SupplierDAO();
        SupplierProductDAO supplierProductDAO = new SupplierProductDAO();
        SupplierReceiptDAO supplierReceiptDAO = new SupplierReceiptDAO();

        SupplierController supplierController = new SupplierController(
                supplierDAO,
                supplierProductDAO,
                supplierReceiptDAO
        );

        SupplierView supplierView = new SupplierView(supplierController);

        // Inventory module
        InventoryLotDAO inventoryLotDAO = new InventoryLotDAO();
        QuarantineDAO quarantineDAO = new QuarantineDAO();
        QualityInspectionDAO qualityInspectionDAO = new QualityInspectionDAO();
        InventoryWasteDAO inventoryWasteDAO = new InventoryWasteDAO();

        InventoryController inventoryController = new InventoryController(
                inventoryLotDAO,
                quarantineDAO,
                qualityInspectionDAO,
                inventoryWasteDAO
        );

        InventoryView inventoryView = new InventoryView(inventoryController);

        Scanner scanner = new Scanner(System.in);
        int option = 0;

        do {
            System.out.println("\n========== FRESCURA TOTAL ERP ==========");
            System.out.println("1. Inventory and Quality");
            System.out.println("2. Suppliers and Purchases");
            System.out.println("3. Sales");
            System.out.println("4. Invoices");
            System.out.println("5. Logistics");
            System.out.println("6. Exit");
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
                    inventoryView.start();
                    break;

                case 2:
                    supplierView.start();
                    break;

                case 3:
                    SaleView saleView = new SaleView();
                    saleView.showMenu();
                    break;

                case 4:
                    InvoiceView invoiceView = new InvoiceView();
                    invoiceView.showMenu();
                    break;

                case 5:
                    DeliveryView deliveryView = new DeliveryView();
                    deliveryView.showMenu();
                    break;

                case 6:
                    System.out.println("Closing Frescura Total ERP...");
                    break;

                default:
                    System.out.println("Invalid option.");
                    break;
            }

        } while (option != 6);
    }
}
