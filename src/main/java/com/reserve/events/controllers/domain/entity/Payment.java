package com.reserve.events.controllers.domain.entity;


import com.reserve.events.controllers.domain.model.UserSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Pagos")
@Schema(description = "Entidad que representa un pago realizado o por realizar de un usuario sobre una reserva")
public class Payment {

    @Id
    private String id;

    @Schema(description = "Descripción del pago", example = "Pago inicial por reserva de salón")
    private String description;

    @Schema(description = "Estado del pago", example = "Pendiente", allowableValues = {"Pendiente", "Completado", "Cancelado"})
    private String status;

    @Schema(description = "Costo total del pago", example = "1500.0")
    private Double totalCost;

    @Schema(description = "Información mínima del cliente asociada al pago")
    private UserSummary client;

    @Schema(description = "Información mínima de la reserva asociada al pago")
    private ReserveInfo reserve;

    @Schema(description = "Servicios cubiertos por este pago")
    private List<CoveredServices> coveredServices = new ArrayList<>();

    // Clases auxiliares que mapean la estructura de JSON proporcionada

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReserveInfo {
        @Schema(description = "Id de la reserva", example = "res_987")
        private String id;

        @Schema(description = "Estado de la reserva", example = "CONFIRMED")
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CoveredServices {

        @Schema(description = "Servicios de entretenimiento incluidos en este bloque")
        private List<EntertainmentInfo> entertainment = new ArrayList<>();

        @Schema(description = "Decoración incluida en este bloque")
        private Decoration decoration;

        @Schema(description = "Catering incluido en este bloque")
        private List<CateringInfo> catering = new ArrayList<>();

        @Schema(description = "Servicios adicionales incluidos en este bloque")
        private List<additionalInfo> additionalServices = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntertainmentInfo {

        @Schema(description = "Id del entretenimiento", example = "svc_1")
        private String id;

        @Schema(description = "Nombre del entretenimiento", example = "Show de magia")
        private String name;

        @Schema(description = "Tarifa por hora del entretenimiento", example = "2000")
        private Double hourlyRate;

        @Schema(description = "Número de horas contratadas", example = "3")
        private Integer hours;

        @Schema(description = "Costo total del entretenimiento", example = "500.0")
        private Double totalCost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CateringInfo {

        @Schema(description = "Id del entretenimiento", example = "cat_1")
        private String id;

        @Schema(description = "descripción del servicio", example = "Servicio de catering básico")
        private String description;

        @Schema(description = "numero de platos", example = "50")
        private Integer numberDish;

        @Schema(description = "costo por plato", example = "10.0")
        private Double costDish;

        @Schema(description = "Costo total del entretenimiento", example = "500.0")
        private Double totalCost;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class additionalInfo{

        @Schema(description = "Id del servicio adicional", example = "add_1")
        private String id;

        @Schema(description = "Nombre del servicio adicional", example = "Servicio de fotografía")
        private String name;

        @Schema(description = "Costo del servicio adicional", example = "300.0")
        private Double cost;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Decoration {
        @Schema(description = "Id de la decoración", example = "dec_1")
        private String id;

        @Schema(description = "Artículos de decoración (resumen o lista en texto)", example = "Globos, manteles")
        private String articles;

        @Schema(description = "Costo de la decoración", example = "200.0")
        private Double cost;
    }

}
