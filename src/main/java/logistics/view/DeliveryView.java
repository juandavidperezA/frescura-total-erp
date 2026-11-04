
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
            System.out.println("3. Create delivery route");
            System.out.println("4. Assign vehicle and driver");
            System.out.println("5. Add sale to route");
            System.out.println("6. Start delivery route");
            System.out.println("7. Complete delivery");
            System.out.println("8. Return");
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
                        int saleId = readPositiveInt();

                        int result = controller.prepareSale(saleId);

                        if (result > 0) {
                            System.out.println("Sale moved to preparation.");
                        } else {
                            System.out.println("Sale not found or invalid status.");
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
                                System.out.println("Total: " + sale.getTotalAmount());
                            }
                        }

                        break;
                    }

                    case 3: {

                        System.out.print("Route name: ");
                        String routeName = scanner.nextLine().trim();

                        System.out.print("Created by: ");
                        String username = scanner.nextLine().trim();

                        int routeId = controller.createRoute(routeName, username);

                        if (routeId > 0) {
                            System.out.println("Route created successfully.");
                            System.out.println("Route ID: " + routeId);
                        } else {
                            System.out.println("Could not create route.");
                        }

                        break;
                    }

                    case 4: {

                        System.out.print("Route ID: ");
                        int routeId = readPositiveInt();

                        System.out.print("Vehicle ID: ");
                        int vehicleId = readPositiveInt();

                        System.out.print("Driver ID: ");
                        int driverId = readPositiveInt();

                        int result = controller.assignDelivery(
                                routeId, vehicleId, driverId
                        );

                        if (result > 0) {
                            System.out.println("Vehicle and driver assigned successfully.");
                        } else {
                            System.out.println("Assignment failed. Check IDs and active status.");
                        }

                        break;
                    }

                    case 5: {

                        System.out.print("Route ID: ");
                        int routeId = readPositiveInt();

                        System.out.print("Sale ID: ");
                        int saleId = readPositiveInt();

                        System.out.print("Delivery order: ");
                        int deliveryOrder = readPositiveInt();

                        int result = controller.addSaleToRoute(
                                routeId, saleId, deliveryOrder
                        );

                        if (result > 0) {
                            System.out.println("Sale added to route successfully.");
                        } else {
                            System.out.println("Could not add sale to route.");
                        }

                        break;
                    }

                    case 6: {

                        System.out.print("Route ID: ");
                        int routeId = readPositiveInt();

                        int total = controller.startRoute(routeId);

                        System.out.println("Route started successfully.");
                        System.out.println("Sales in transit: " + total);

                        break;
                    }

                    case 7: {

                        System.out.print("Route ID: ");
                        int routeId = readPositiveInt();

                        System.out.print("Sale ID: ");
                        int saleId = readPositiveInt();

                        System.out.print("Received by: ");
                        String receivedBy = scanner.nextLine().trim();

                        int result = controller.completeDelivery(
                                routeId, saleId, receivedBy
                        );

                        if (result > 0) {
                            System.out.println("Delivery completed successfully.");
                        }

                        break;
                    }

                    case 8:
                        System.out.println("Returning to main menu...");
                        break;

                    default:
                        System.out.println("Invalid option.");
                        break;
                }

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }

        } while (option != 8);
    }

    private int readPositiveInt() {

        while (true) {

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid number. Try again:");
                scanner.nextLine();
                continue;
            }

            int number = scanner.nextInt();
            scanner.nextLine();

            if (number > 0) {
                return number;
            }

            System.out.println("Number must be positive. Try again:");
        }
    }
}
