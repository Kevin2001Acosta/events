package com.reserve.events.application;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.reserve.events.controllers.domain.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Servicio para generar comprobantes de pago en formato PDF.
 */
@Slf4j
@Service
public class PaymentPdfService {

    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(52, 73, 94);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    private static final String LOGO_PATH = "static/eventify.png";

    /**
     * Genera un PDF con el comprobante de pago.
     *
     * @param payment El pago completado
     * @return byte[] con el contenido del PDF
     */
    public byte[] generatePaymentReceipt(Payment payment) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Logo de Eventify
            addLogo(document);

            // Título
            addTitle(document, "COMPROBANTE DE PAGO");

            // Información del pago
            addPaymentInfo(document, payment);

            // Información del cliente
            addClientInfo(document, payment);

            // Información de la reserva
            addReserveInfo(document, payment);

            // Servicios cubiertos
            addCoveredServices(document, payment);

            // Total
            addTotal(document, payment);

            // Pie de página
            addFooter(document);

            document.close();
            log.info("PDF generado exitosamente para el pago ID: {}", payment.getId());
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error al generar PDF para el pago {}: {}", payment.getId(), e.getMessage());
            throw new RuntimeException("Error al generar el comprobante PDF", e);
        }
    }

    /**
     * Agrega el logo de Eventify al documento.
     */
    private void addLogo(Document document) {
        try {
            ClassPathResource resource = new ClassPathResource(LOGO_PATH);
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] logoBytes = inputStream.readAllBytes();
                ImageData imageData = ImageDataFactory.create(logoBytes);
                Image logo = new Image(imageData);

                // Escalar el logo a un tamaño apropiado (ancho de 150 puntos)
                logo.setWidth(150);
                logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                logo.setMarginBottom(15);

                document.add(logo);
            }
        } catch (Exception e) {
            log.warn("No se pudo cargar el logo de Eventify: {}", e.getMessage());
            // Si no se puede cargar el logo, simplemente no lo agregamos
        }
    }

    private void addTitle(Document document, String title) {
        Paragraph titleParagraph = new Paragraph(title)
                .setFontSize(24)
                .setBold()
                .setFontColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(titleParagraph);

        // Fecha de generación
        Paragraph dateParagraph = new Paragraph("Generado el: " + LocalDateTime.now().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20);
        document.add(dateParagraph);
    }

    private void addPaymentInfo(Document document, Payment payment) {
        addSectionTitle(document, "Información del Pago");

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        addTableRow(table, "ID del Pago:", payment.getId());
        addTableRow(table, "Estado:", payment.getStatus().toString());
        addTableRow(table, "Descripción:", payment.getDescription());

        document.add(table);
        document.add(new Paragraph().setMarginBottom(15));
    }

    private void addClientInfo(Document document, Payment payment) {
        if (payment.getClient() == null) return;

        addSectionTitle(document, "Información del Cliente");

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        addTableRow(table, "Nombre:", payment.getClient().getName());
        addTableRow(table, "Email:", payment.getClient().getEmail());
        if (payment.getClient().getPhone() != null) {
            addTableRow(table, "Teléfono:", payment.getClient().getPhone());
        }

        document.add(table);
        document.add(new Paragraph().setMarginBottom(15));
    }

    private void addReserveInfo(Document document, Payment payment) {
        if (payment.getReserve() == null) return;

        addSectionTitle(document, "Información de la Reserva");

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        addTableRow(table, "ID de Reserva:", payment.getReserve().getId());
        addTableRow(table, "Estado:", payment.getReserve().getStatus().toString());

        document.add(table);
        document.add(new Paragraph().setMarginBottom(15));
    }

    private void addCoveredServices(Document document, Payment payment) {
        if (payment.getCoveredServices() == null) return;

        addSectionTitle(document, "Servicios Cubiertos");

        Payment.CoveredServices services = payment.getCoveredServices();

        // Entretenimiento
        if (services.getEntertainment() != null && !services.getEntertainment().isEmpty()) {
            document.add(new Paragraph("Entretenimiento:").setBold().setFontSize(11));
            Table entTable = createServicesTable();
            for (Payment.EntertainmentInfo ent : services.getEntertainment()) {
                entTable.addCell(new Cell().add(new Paragraph(ent.getName())));
                entTable.addCell(new Cell().add(new Paragraph(ent.getHours() + " horas")));
                entTable.addCell(new Cell().add(new Paragraph(formatCurrency(ent.getTotalCost()))));
            }
            document.add(entTable);
        }

        // Decoración
        if (services.getDecoration() != null) {
            document.add(new Paragraph("Decoración:").setBold().setFontSize(11).setMarginTop(10));
            Table decTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                    .setWidth(UnitValue.createPercentValue(100));
            decTable.addCell(new Cell().add(new Paragraph("Artículos")));
            decTable.addCell(new Cell().add(new Paragraph("Costo")));
            decTable.addCell(new Cell().add(new Paragraph(services.getDecoration().getArticles() != null ?
                    services.getDecoration().getArticles() : "N/A")));
            decTable.addCell(new Cell().add(new Paragraph(formatCurrency(services.getDecoration().getCost()))));
            document.add(decTable);
        }

        // Catering
        if (services.getCatering() != null && !services.getCatering().isEmpty()) {
            document.add(new Paragraph("Catering:").setBold().setFontSize(11).setMarginTop(10));
            Table catTable = createServicesTable();
            for (Payment.CateringInfo cat : services.getCatering()) {
                catTable.addCell(new Cell().add(new Paragraph(cat.getDescription())));
                catTable.addCell(new Cell().add(new Paragraph(cat.getNumberDish() + " platos")));
                catTable.addCell(new Cell().add(new Paragraph(formatCurrency(cat.getTotalCost()))));
            }
            document.add(catTable);
        }

        // Servicios adicionales
        if (services.getAdditionalServices() != null && !services.getAdditionalServices().isEmpty()) {
            document.add(new Paragraph("Servicios Adicionales:").setBold().setFontSize(11).setMarginTop(10));
            Table addTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                    .setWidth(UnitValue.createPercentValue(100));
            addTable.addCell(createHeaderCell("Nombre"));
            addTable.addCell(createHeaderCell("Costo"));
            for (Payment.additionalInfo add : services.getAdditionalServices()) {
                addTable.addCell(new Cell().add(new Paragraph(add.getName())));
                addTable.addCell(new Cell().add(new Paragraph(formatCurrency(add.getCost()))));
            }
            document.add(addTable);
        }

        document.add(new Paragraph().setMarginBottom(15));
    }

    private void addTotal(Document document, Payment payment) {
        Table totalTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        Cell labelCell = new Cell()
                .add(new Paragraph("TOTAL A PAGAR:").setBold().setFontSize(14))
                .setBackgroundColor(PRIMARY_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(10);

        Cell valueCell = new Cell()
                .add(new Paragraph(formatCurrency(payment.getTotalCost())).setBold().setFontSize(14))
                .setBackgroundColor(PRIMARY_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(10);

        totalTable.addCell(labelCell);
        totalTable.addCell(valueCell);

        document.add(totalTable);
    }

    private void addFooter(Document document) {
        document.add(new Paragraph().setMarginTop(30));

        Paragraph footer = new Paragraph("Este documento es un comprobante válido de su pago.\n" +
                "Gracias por confiar en nuestros servicios.")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);
        document.add(footer);

        Paragraph contact = new Paragraph("Para cualquier consulta, contáctenos a través de nuestra plataforma.")
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY)
                .setMarginTop(5);
        document.add(contact);
    }

    private void addSectionTitle(Document document, String title) {
        Paragraph sectionTitle = new Paragraph(title)
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10);
        document.add(sectionTitle);
    }

    private void addTableRow(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()).setBorder(null));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "N/A")).setBorder(null));
    }

    private Table createServicesTable() {
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100));
        table.addCell(createHeaderCell("Servicio"));
        table.addCell(createHeaderCell("Detalle"));
        table.addCell(createHeaderCell("Costo"));
        return table;
    }

    private Cell createHeaderCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(new DeviceRgb(236, 240, 241))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private String formatCurrency(Double value) {
        if (value == null) return "$0";
        return CURRENCY_FORMAT.format(value);
    }
}

