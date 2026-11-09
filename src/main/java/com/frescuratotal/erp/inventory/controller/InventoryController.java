/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.frescuratotal.erp.inventory.controller;

import com.frescuratotal.erp.inventory.dao.InventoryLotDAO;
import com.frescuratotal.erp.inventory.dao.InventoryWasteDAO;
import com.frescuratotal.erp.inventory.dao.QualityInspectionDAO;
import com.frescuratotal.erp.inventory.dao.QuarantineDAO;
import com.frescuratotal.erp.inventory.model.InventoryLot;
import com.frescuratotal.erp.inventory.model.InventoryWaste;
import com.frescuratotal.erp.inventory.model.QualityInspection;
import com.frescuratotal.erp.inventory.model.QuarantineRecord;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author USUARIO
 */
public class InventoryController {
    private final InventoryLotDAO inventoryLotDAO;
    private final QuarantineDAO quarantineDAO;
    private final QualityInspectionDAO qualityInspectionDAO;
    private final InventoryWasteDAO inventoryWasteDAO;

    public InventoryController(InventoryLotDAO inventoryLotDAO, QuarantineDAO quarantineDAO, QualityInspectionDAO qualityInspectionDAO, InventoryWasteDAO inventoryWasteDAO) {
        this.inventoryLotDAO = inventoryLotDAO;
        this.quarantineDAO = quarantineDAO;
        this.qualityInspectionDAO = qualityInspectionDAO;
        this.inventoryWasteDAO = inventoryWasteDAO;
    }
    
      public int registerLot(InventoryLot lot) throws SQLException {
        return inventoryLotDAO.insertLot(lot);
    }

    public InventoryLot findLotById(int id) throws SQLException {
        return inventoryLotDAO.findById(id);
    }

    public List<InventoryLot> findAllLots() throws SQLException {
        return inventoryLotDAO.findAll();
    }

    public List<InventoryLot> findLotsByProductId(int productId)
            throws SQLException {
        return inventoryLotDAO.findByProductId(productId);
    }

    public int updateExpirationStatuses() throws SQLException {
        return inventoryLotDAO.updateExpirationStatuses();
    }

    public int decreaseQuantity(int lotId, BigDecimal quantity)
            throws SQLException {
        return inventoryLotDAO.decreaseQuantity(lotId, quantity);
    }

    // Quarantine operations

    public int registerQuarantine(QuarantineRecord record)
            throws SQLException {
        return quarantineDAO.insertQuarantine(record);
    }

    public List<QuarantineRecord> findActiveQuarantines()
            throws SQLException {
        return quarantineDAO.findActiveQuarantines();
    }

    public boolean resolveQuarantine(int quarantineId, String resolutionNotes)
            throws SQLException {
        return quarantineDAO.resolveQuarantine(quarantineId, resolutionNotes);
    }

    // Quality inspection operations

    public int registerInspection(QualityInspection inspection)
            throws SQLException {
        return qualityInspectionDAO.insertInspection(inspection);
    }

    public List<QualityInspection> findInspectionsByLotId(int lotId)
            throws SQLException {
        return qualityInspectionDAO.findByLotId(lotId);
    }

    // Waste operations

    public int registerWaste(InventoryWaste waste) throws SQLException {
        return inventoryWasteDAO.insertWaste(waste);
    }

    public List<InventoryWaste> findWastesByLotId(int lotId)
            throws SQLException {
        return inventoryWasteDAO.findByLotId(lotId);
    }
    
    public InventoryLot infonecesario()throws SQLException{
        return inventoryLotDAO.informacionnecesaria();
    }
}

