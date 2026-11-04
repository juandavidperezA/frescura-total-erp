/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.inventory.view;

import com.frescuratotal.erp.inventory.controller.InventoryController;
import com.frescuratotal.erp.inventory.model.InventoryLot;
import com.frescuratotal.erp.inventory.model.InventoryWaste;
import com.frescuratotal.erp.inventory.model.QualityInspection;
import com.frescuratotal.erp.inventory.model.QuarantineRecord;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author USUARIO
 */
public class InventoryView {
    Scanner sc= new Scanner(System.in);
    private InventoryController control;
    
    public InventoryView(InventoryController control){
        this.control=control;
    }
    
    public void start(){
        int option=0;
        
        do{
        System.out.println("\n===== INVENTORY MENU =====");
        System.out.println("1. Register inventory lot");
        System.out.println("2. Find lot by ID");
        System.out.println("3. List all lots");
        System.out.println("4. Find lots by product");
        System.out.println("5. Update expiration statuses");
        System.out.println("6. Register quarantine");
        System.out.println("7. List active quarantines");
        System.out.println("8. Resolve quarantine");
        System.out.println("9. Register quality inspection");
        System.out.println("10. Find inspections by lot");
        System.out.println("11. Register inventory waste");
        System.out.println("12. Find wastes by lot");
        System.out.println("13. Return to main menu");
        System.out.print("Select an option: ");            
        
        if (!sc.hasNextInt()) {
            System.out.println("Invalid option.");
            sc.nextLine();
            continue;
        }

        option = sc.nextInt();
        sc.nextLine();
        
            switch (option) {
                case 1:
                    try {
                        System.out.println("===== REGISTER INVENTORY LOT =====");

                        System.out.print("Enter product ID: ");
                        int productId = Integer.parseInt(sc.nextLine().trim());

                        System.out.print("Enter supplier ID: ");
                        int supplierId = Integer.parseInt(sc.nextLine().trim());

                        System.out.print("Enter expiration date (YYYY-MM-DD): ");
                        LocalDate expirationDate =
                                LocalDate.parse(sc.nextLine().trim());

                        System.out.print("Enter initial quantity: ");
                        BigDecimal initialQuantity =
                                new BigDecimal(sc.nextLine().trim());

                        System.out.print("Enter purchase price: ");
                        BigDecimal purchasePrice =
                                new BigDecimal(sc.nextLine().trim());

                        System.out.print("Enter sale price: ");
                        BigDecimal salePrice =
                                new BigDecimal(sc.nextLine().trim());

                        System.out.print("Enter aisle: ");
                        String aisle = sc.nextLine().trim();

                        System.out.print("Enter shelf: ");
                        String shelf = sc.nextLine().trim();

                        InventoryLot lot = new InventoryLot(
                                "AUTO",
                                productId,
                                supplierId,
                                expirationDate,
                                initialQuantity,
                                purchasePrice,
                                salePrice,
                                aisle,
                                shelf,
                                1
                        );

                        int generatedId = control.registerLot(lot);

                        if (generatedId > 0) {
                            System.out.println("Lot registered successfully.");
                            System.out.println("Generated lot ID: " + generatedId);
                        } else {
                            System.out.println("Could not register lot.");
                        }

                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid data: " + e.getMessage());
                    }
                    break;
                case 2:
                     try {
                        System.out.print("Enter lot ID: ");

                        if (!sc.hasNextInt()) {
                            System.out.println("Invalid lot ID.");
                            sc.nextLine();
                            break;
                        }

                        int lotId = sc.nextInt();
                        sc.nextLine();

                        InventoryLot lot = control.findLotById(lotId);

                        if (lot == null) {
                            System.out.println("Lot not found.");
                        } else {
                            System.out.println("===== LOT DETAILS =====");
                            System.out.println("ID: " + lot.getId());
                            System.out.println("Lot number: " + lot.getLotNumber());
                            System.out.println("Product ID: " + lot.getProductId());
                            System.out.println("Supplier ID: " + lot.getSupplierId());
                            System.out.println("Available quantity: " + lot.getQuantity());
                            System.out.println("Expiration date: " + lot.getExpirationDate());
                            System.out.println("Status ID: " + lot.getStatusId());
                        }

                    } catch (SQLException e) {
                        System.out.println("Error finding lot: " + e.getMessage());
                    }
                    break;
                case 3:
                        try {
                            List<InventoryLot> lots = control.findAllLots();

                            if (lots.isEmpty()) {
                                System.out.println("No inventory lots found.");
                            } else {
                                for (InventoryLot lot : lots) {
                                    System.out.println("------------------------");
                                    System.out.println("ID: " + lot.getId());
                                    System.out.println("Lot number: " + lot.getLotNumber());
                                    System.out.println("Product ID: " + lot.getProductId());
                                    System.out.println("Available quantity: " + lot.getQuantity());
                                    System.out.println("Expiration date: " + lot.getExpirationDate());
                                    System.out.println("Status ID: " + lot.getStatusId());
                                }
                            }
                        } catch (SQLException e) {
                            System.out.println("Error listing inventory lots: " + e.getMessage());
                        }
                    break;
                case 4:
                     try {
                        System.out.print("Enter product ID: ");

                        if (!sc.hasNextInt()) {
                            System.out.println("Invalid product ID.");
                            sc.nextLine();
                            break;
                        }

                        int productId = sc.nextInt();
                        sc.nextLine();

                        List<InventoryLot> lots = control.findLotsByProductId(productId);

                        if (lots.isEmpty()) {
                            System.out.println("No lots found for this product.");
                        } else {
                            for (InventoryLot lot : lots) {
                                System.out.println("------------------------");
                                System.out.println("ID: " + lot.getId());
                                System.out.println("Lot number: " + lot.getLotNumber());
                                System.out.println("Available quantity: " + lot.getQuantity());
                                System.out.println("Expiration date: " + lot.getExpirationDate());
                                System.out.println("Status ID: " + lot.getStatusId());
                            }
                        }

                    } catch (SQLException e) {
                        System.out.println("Error finding lots: " + e.getMessage());
                    }
                    break;
                case 5:
                        try {
                            int updatedLots = control.updateExpirationStatuses();

                            System.out.println("Expiration statuses updated successfully.");
                            System.out.println("Updated lots: " + updatedLots);

                        } catch (SQLException e) {
                            System.out.println("Error updating expiration statuses: " + e.getMessage());
                        }
                    break;
                case 6:
                    try {
                        System.out.print("Enter lot ID: ");
                        if (!sc.hasNextInt()) {
                            System.out.println("Invalid lot ID.");
                            sc.nextLine();
                            break;
                        }

                        int quarantineLotId = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter quarantine reason: ");
                        String reason = sc.nextLine().trim();

                        System.out.print("Enter responsible user: ");
                        String user = sc.nextLine().trim();

                        if (quarantineLotId <= 0 || reason.isEmpty() || user.isEmpty()) {
                            System.out.println("Invalid quarantine data.");
                            break;
                        }

                        QuarantineRecord record =
                                new QuarantineRecord(quarantineLotId, reason, user);

                        int quarantineId = control.registerQuarantine(record);

                        if (quarantineId > 0) {
                            System.out.println("Quarantine registered successfully.");
                            System.out.println("Quarantine ID: " + quarantineId);
                        } else {
                            System.out.println("Could not register quarantine.");
                        }

                    } catch (SQLException e) {
                        System.out.println("Error registering quarantine: " + e.getMessage());
                    }
                    break;
                case 7:
                        try {
                            List<QuarantineRecord> quarantines =
                                    control.findActiveQuarantines();

                            if (quarantines.isEmpty()) {
                                System.out.println("No active quarantines found.");
                            } else {
                                System.out.println("===== ACTIVE QUARANTINES =====");

                                for (QuarantineRecord record : quarantines) {
                                    System.out.println("------------------------");
                                    System.out.println("Quarantine ID: " + record.getId());
                                    System.out.println("Lot ID: " + record.getLotId());
                                    System.out.println("Reason: " + record.getReason());
                                    System.out.println("User: " + record.getUser());
                                    System.out.println("Registration date: "
                                            + record.getRegistrationDate());
                                }
                            }

                        } catch (SQLException e) {
                            System.out.println("Error listing quarantines: " + e.getMessage());
                        }                    
                    break;
                case 8:
                    try {
                        System.out.print("Enter quarantine ID: ");

                        if (!sc.hasNextInt()) {
                            System.out.println("Invalid quarantine ID.");
                            sc.nextLine();
                            break;
                        }

                        int quarantineId = sc.nextInt();
                        sc.nextLine();

                        if (quarantineId <= 0) {
                            System.out.println("Quarantine ID must be positive.");
                            break;
                        }

                        System.out.print("Enter resolution notes: ");
                        String resolutionNotes = sc.nextLine().trim();

                        if (resolutionNotes.isEmpty()) {
                            System.out.println("Resolution notes cannot be empty.");
                            break;
                        }

                        boolean resolved = control.resolveQuarantine(
                                quarantineId, resolutionNotes
                        );

                        if (resolved) {
                            System.out.println("Quarantine resolved successfully.");
                        } else {
                            System.out.println("Quarantine not found or already resolved.");
                        }

                    } catch (SQLException e) {
                        System.out.println("Error resolving quarantine: " + e.getMessage());
                    }
                    break;
                case 9:
                        try {
                                System.out.print("Enter lot ID: ");

                                if (!sc.hasNextInt()) {
                                    System.out.println("Invalid lot ID.");
                                    sc.nextLine();
                                    break;
                                }

                                int inspectionLotId = sc.nextInt();
                                sc.nextLine();

                                System.out.print("Enter inspector name: ");
                                String inspector = sc.nextLine().trim();

                                System.out.print("Enter result (Aprobado/Rechazado): ");
                                String result = sc.nextLine().trim();

                                if (!result.equals("Aprobado") && !result.equals("Rechazado")) {
                                    System.out.println("Invalid inspection result.");
                                    break;
                                }

                                String rejectionReason = null;

                                if (result.equals("Rechazado")) {
                                    System.out.print("Enter rejection reason: ");
                                    rejectionReason = sc.nextLine().trim();

                                    if (rejectionReason.isEmpty()) {
                                        System.out.println("Rejection reason is required.");
                                        break;
                                    }
                                }

                                System.out.print("Enter additional notes: ");
                                String notes = sc.nextLine().trim();

                                QualityInspection inspection = new QualityInspection(
                                        inspectionLotId,
                                        result,
                                        inspector,
                                        rejectionReason,
                                        notes
                                );

                                int inspectionId = control.registerInspection(inspection);

                                if (inspectionId > 0) {
                                    System.out.println("Inspection registered successfully.");
                                    System.out.println("Inspection ID: " + inspectionId);
                                } else {
                                    System.out.println("Could not register inspection.");
                                }

                            } catch (SQLException e) {
                                System.out.println("Error registering inspection: " + e.getMessage());
                            }                    
                    break;
                case 10:
                        try {
                            System.out.print("Enter lot ID: ");

                            if (!sc.hasNextInt()) {
                                System.out.println("Invalid lot ID.");
                                sc.nextLine();
                                break;
                            }

                            int inspectionLotId = sc.nextInt();
                            sc.nextLine();

                            List<QualityInspection> inspections =
                                    control.findInspectionsByLotId(inspectionLotId);

                            if (inspections.isEmpty()) {
                                System.out.println("No inspections found for this lot.");
                            } else {
                                for (QualityInspection inspection : inspections) {
                                    System.out.println("------------------------");
                                    System.out.println("Inspection ID: " + inspection.getInspectionId());
                                    System.out.println("Result: " + inspection.getResult());
                                    System.out.println("Inspector: " + inspection.getInspectorName());
                                    System.out.println("Rejection reason: "
                                            + inspection.getRejectionReason());
                                    System.out.println("Notes: " + inspection.getNotes());
                                }
                            }

                        } catch (SQLException e) {
                            System.out.println("Error finding inspections: " + e.getMessage());
                        }
                    break;
                case 11:
                    try {
                        System.out.print("Enter lot ID: ");

                        if (!sc.hasNextInt()) {
                            System.out.println("Invalid lot ID.");
                            sc.nextLine();
                            break;
                        }

                        int wasteLotId = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter inspection ID (0 if none): ");

                        if (!sc.hasNextInt()) {
                            System.out.println("Invalid inspection ID.");
                            sc.nextLine();
                            break;
                        }

                        int inputInspectionId = sc.nextInt();
                        sc.nextLine();

                        if (wasteLotId <= 0 || inputInspectionId < 0) {
                            System.out.println("IDs must be valid.");
                            break;
                        }

                        Integer inspectionId =
                                inputInspectionId == 0 ? null : inputInspectionId;

                        System.out.print("Enter lost quantity: ");

                        BigDecimal lostQuantity;

                        try {
                            lostQuantity = new BigDecimal(sc.nextLine().trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid quantity.");
                            break;
                        }

                        if (lostQuantity.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("Quantity must be positive.");
                            break;
                        }

                        System.out.print("Enter waste reason: ");
                        String wasteReason = sc.nextLine().trim();

                        System.out.print("Enter responsible user: ");
                        String wasteUser = sc.nextLine().trim();

                        if (wasteReason.isEmpty() || wasteUser.isEmpty()) {
                            System.out.println("Reason and user are required.");
                            break;
                        }

                        InventoryWaste waste = new InventoryWaste(
                                wasteLotId,
                                inspectionId,
                                lostQuantity,
                                wasteReason,
                                wasteUser
                        );

                        int wasteId = control.registerWaste(waste);

                        if (wasteId > 0) {
                            System.out.println("Waste registered successfully.");
                            System.out.println("Waste ID: " + wasteId);
                        } else {
                            System.out.println("Could not register waste.");
                        }

                    } catch (SQLException e) {
                        System.out.println("Error registering waste: " + e.getMessage());
                    }
                    break;
                case 12:
                    try {
                        System.out.print("Enter lot ID: ");

                        if (!sc.hasNextInt()) {
                            System.out.println("Invalid lot ID.");
                            sc.nextLine();
                            break;
                        }

                        int wasteLotId = sc.nextInt();
                        sc.nextLine();

                        if (wasteLotId <= 0) {
                            System.out.println("Lot ID must be positive.");
                            break;
                        }

                        List<InventoryWaste> wastes =
                                control.findWastesByLotId(wasteLotId);

                        if (wastes.isEmpty()) {
                            System.out.println("No waste records found for this lot.");
                        } else {
                            for (InventoryWaste waste : wastes) {
                                System.out.println("------------------------");
                                System.out.println("Waste ID: " + waste.getId());
                                System.out.println("Lot ID: " + waste.getLotId());
                                System.out.println("Lost quantity: " + waste.getLostQuantity());
                                System.out.println("Reason: " + waste.getReason());
                                System.out.println("User: " + waste.getUser());
                                System.out.println("Registration date: "
                                        + waste.getRegistrationDate());
                            }
                        }

                    } catch (SQLException e) {
                        System.out.println("Error finding waste records: " + e.getMessage());
                    }
                    break;
                case 13:
                    System.out.println("Returning to main menu...");
                    break;
                default:
                    System.out.println("Option not implemented yet.");
                break;
            }
        }while(option !=13);
    }
    
}
