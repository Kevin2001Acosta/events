package com.reserve.events.controllers.domain.entity;


import com.reserve.events.controllers.domain.model.EstablishmentSummary;
import com.reserve.events.controllers.domain.model.PaymentStatus;
import com.reserve.events.controllers.domain.model.StatusReserve;
import com.reserve.events.controllers.domain.model.UserSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
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

    @NotBlank(message = "El estado del pago es obligatorio")
    @Schema(description = "Estado del pago", example = "Pendiente", allowableValues = {"PENDIENTE", "COMPLETADO", "CANCELADO"})
    private PaymentStatus status;

    @NotNull(message = "El costo total del pago es obligatorio")
    @Schema(description = "Costo total del pago", example = "1500.0")
    private Double totalCost;

    @NotNull(message = "La información del cliente es obligatoria")
    @Schema(description = "Información mínima del cliente asociada al pago")
    private UserSummary client;

    @NotNull(message = "La información de la reserva es obligatoria")
    @Schema(description = "Información mínima de la reserva asociada al pago")
    private ReserveInfo reserve;

    @Schema(description = "Servicios cubiertos por este pago")
    private CoveredServices coveredServices;

    @NotNull(message = "La información del establecimiento es obligatoria")
    @Schema(description = "Información mínima del establecimiento asociado a la reserva")
    private EstablishmentSummary establishment;
    // Clases auxiliares que mapean la estructura de JSON proporcionada

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReserveInfo {

        @NotBlank(message = "El id de la reserva es obligatorio")
        @Schema(description = "Id de la reserva", example = "res_987")
        private String id;

        @NotBlank(message = "El estado de la reserva es obligatorio")
        @Schema(description = "Estado de la reserva", example = "CONFIRMED")
        private StatusReserve status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CoveredServices {

        @Builder.Default
        @Schema(description = "Servicios de entretenimiento incluidos en este bloque")
        private List<EntertainmentInfo> entertainment = new ArrayList<>();

        @Schema(description = "Decoración incluida en este bloque")
        private Decoration decoration;

        @Builder.Default
        @Schema(description = "Catering incluido en este bloque")
        private List<CateringInfo> catering = new ArrayList<>();

        @Builder.Default
        @Schema(description = "Servicios adicionales incluidos en este bloque")
        private List<additionalInfo> additionalServices = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntertainmentInfo {

        @NotBlank(message = "El id del entretenimiento es obligatorio")
        @Schema(description = "Id del entretenimiento", example = "svc_1")
        private String id;

        @NotBlank(message = "El nombre del entretenimiento es obligatorio")
        @Schema(description = "Nombre del entretenimiento", example = "Show de magia")
        private String name;

        @NotNull(message = "La tarifa por hora es obligatoria")
        @Schema(description = "Tarifa por hora del entretenimiento", example = "2000")
        private Double hourlyRate;

        @NotNull(message = "El número de horas contratadas es obligatorio")
        @Schema(description = "Número de horas contratadas", example = "3")
        private Integer hours;

        @NotNull(message = "El costo total del entretenimiento es obligatorio")
        @Schema(description = "Costo total del entretenimiento", example = "500.0")
        private Double totalCost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CateringInfo {

        @NotBlank(message = "El ID del catering es obligatorio si se incluye un catering")
        @Schema(description = "ID único del servicio de catering", example = "cat_123")
        private String id;

        @NotBlank(message = "La descripción del catering es obligatoria si se incluye un catering")
        @Schema(description = "Descripción del servicio de catering", example = "Catering para 50 personas")
        private String description;

        @NotNull(message = "El número de platos servidos es obligatorio si se incluye un catering")
        @Schema(description = "Número de platos servidos", example = "50")
        private Integer numberDish;

        @NotNull(message = "El costo por plato es obligatorio si se incluye un catering")
        @Schema(description = "Costo por cada plato servido", example = "10.0")
        private Double costDish;

        @NotNull(message = "El costo total del catering es obligatorio si se incluye un catering")
        @Schema(description = "Costo total del servicio de catering", example = "500.0")
        private Double totalCost;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class additionalInfo {

        @NotBlank(message = "El ID del servicio adicional es obligatorio si se incluye un servicio adicional")
        @Schema(description = "ID único del servicio adicional", example = "add_456")
        private String id;

        @NotBlank(message = "El nombre del servicio adicional es obligatorio si se incluye un servicio adicional")
        @Schema(description = "Nombre del servicio adicional", example = "Fotografía profesional")
        private String name;

        @NotNull(message = "El costo del servicio adicional es obligatorio si se incluye un servicio adicional")
        @Schema(description = "Costo del servicio adicional", example = "300.0")
        private Double cost;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Decoration {
        @NotBlank(message = "El ID de la decoración es obligatorio si se incluye una decoración")
        @Schema(description = "ID único de la decoración", example = "dec_789")
        private String id;

        @NotBlank(message = "Los artículos de decoración son obligatorios si se incluye una decoración")
        @Schema(description = "Artículos incluidos en la decoración", example = "Globos, manteles, flores")
        private String articles;

        @NotNull(message = "El costo de la decoración es obligatorio si se incluye una decoración")
        @Schema(description = "Costo total de la decoración", example = "200.0")
        private Double cost;
    }

}
