
package sales.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import config.DatabaseConnection;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import sales.model.Invoice;

public class InvoiceImageGenerator {

    public Path generate(Invoice invoice)
            throws SQLException, IOException, WriterException {

        if (invoice == null || invoice.getId() <= 0) {
            throw new IllegalArgumentException("Invalid invoice");
        }

        String headerSql = "SELECT e.nombre_empresa, e.nit, "
                + "c.nombre AS cliente, c.documento "
                + "FROM ventas v "
                + "JOIN clientes c ON v.id_cliente = c.id_cliente "
                + "CROSS JOIN informacion_empresa e "
                + "WHERE v.id_venta = ? LIMIT 1";

        String detailSql = "SELECT p.nombre, d.cantidad_vendida, "
                + "d.precio_unitario, d.monto_descuento, "
                + "d.monto_impuesto, d.total_linea "
                + "FROM detalles_venta d "
                + "JOIN lotes_inventario l ON d.id_lote = l.id_lote "
                + "JOIN productos p ON l.id_producto = p.id_producto "
                + "WHERE d.id_venta = ?";

        String company;
        String nit;
        String customer;
        String document;
        List<String> products = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(headerSql)) {

                ps.setInt(1, invoice.getSaleId());

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new SQLException("Sale or customer not found");
                    }

                    company = rs.getString("nombre_empresa");
                    nit = rs.getString("nit");
                    customer = rs.getString("cliente");
                    document = rs.getString("documento");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(detailSql)) {

                ps.setInt(1, invoice.getSaleId());

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        products.add(
                                rs.getString("nombre")
                                + " | Qty: " + rs.getDouble("cantidad_vendida")
                                + " | Price: " + rs.getDouble("precio_unitario")
                                + " | Discount: " + rs.getDouble("monto_descuento")
                                + " | Tax: " + rs.getDouble("monto_impuesto")
                                + " | Total: " + rs.getDouble("total_linea")
                        );
                    }
                }
            }
        }

        if (products.isEmpty()) {
            throw new SQLException("Sale has no products");
        }

        int width = 1100;
        int height = 700 + products.size() * 35;

        BufferedImage image = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = image.createGraphics();

        try {

            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);

            graphics.setColor(Color.BLACK);
            graphics.setFont(new Font("SansSerif", Font.BOLD, 28));
            graphics.drawString(company, 50, 60);

            graphics.setFont(new Font("SansSerif", Font.PLAIN, 16));
            graphics.drawString("NIT: " + nit, 50, 90);
            graphics.drawString("FACTURA - SIMULACION ACADEMICA", 50, 120);

            graphics.drawLine(50, 145, 1050, 145);

            graphics.drawString(
                    "Invoice number: " + invoice.getInvoiceNumber(), 50, 180
            );
            graphics.drawString(
                    "Date: " + invoice.getIssueDate(), 50, 210
            );
            graphics.drawString("Customer: " + customer, 50, 240);
            graphics.drawString("Document: " + document, 50, 270);

            graphics.setFont(new Font("SansSerif", Font.BOLD, 18));
            graphics.drawString("PRODUCTS", 50, 320);

            graphics.setFont(new Font("SansSerif", Font.PLAIN, 14));

            int y = 360;

            for (String product : products) {
                graphics.drawString(product, 50, y);
                y += 35;
            }

            graphics.drawLine(50, y, 1050, y);
            y += 35;

            graphics.setFont(new Font("SansSerif", Font.PLAIN, 18));
            graphics.drawString(
                    "Subtotal: " + invoice.getSubtotal(), 50, y
            );
            y += 30;

            graphics.drawString(
                    "Discount: " + invoice.getTotalDiscount(), 50, y
            );
            y += 30;

            graphics.drawString(
                    "Tax: " + invoice.getTaxAmount(), 50, y
            );
            y += 35;

            graphics.setFont(new Font("SansSerif", Font.BOLD, 20));
            graphics.drawString(
                    "TOTAL: " + invoice.getTotalAmount(), 50, y
            );

            graphics.setFont(new Font("SansSerif", Font.PLAIN, 13));
            graphics.drawString(
                    "CUFE simulado: " + invoice.getCufe(),
                    50, height - 100
            );

            String qrData = invoice.getQrLink();

            if (qrData == null || qrData.isBlank()) {
                qrData = invoice.getCufe();
            }

            BitMatrix matrix = new QRCodeWriter().encode(
                    qrData, BarcodeFormat.QR_CODE, 150, 150
            );

            BufferedImage qrImage = new BufferedImage(
                    150, 150, BufferedImage.TYPE_INT_RGB
            );

            for (int x = 0; x < 150; x++) {
                for (int z = 0; z < 150; z++) {
                    qrImage.setRGB(
                            x, z,
                            matrix.get(x, z)
                                    ? Color.BLACK.getRGB()
                                    : Color.WHITE.getRGB()
                    );
                }
            }

            graphics.drawImage(
                    qrImage, 850, height - 200, null
            );

            graphics.drawString(
                    "CUFE y QR simulados - No valido ante DIAN",
                    50, height - 65
            );

        } finally {
            graphics.dispose();
        }

        Path folder = Path.of("invoices");
        Files.createDirectories(folder);

        Path file = folder.resolve(
                "invoice-" + invoice.getId() + ".png"
        );

        if (!ImageIO.write(image, "png", file.toFile())) {
            throw new IOException("Could not generate invoice image");
        }

        return file;
    }
}
