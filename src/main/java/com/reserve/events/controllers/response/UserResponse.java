package com.reserve.events.controllers.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.domain.entity.User;
import com.reserve.events.controllers.domain.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta de la API para un usuario")
public class UserResponse {

    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String city;
    private UserType type;

    private List<ReserveSummaryResponse> scheduledBookings;
    private List<ReserveSummaryResponse> completedBookings;
    private List<ReserveSummaryResponse> cancelledBookings;

    private List<User.PaymentInfo> payments;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReserveSummaryResponse{
        private String id;
        private StatusReserve status;

        private EventSummary event;

        private EstablishmentSummary establishment;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private List<LocalDate> dates;

        private CoveredServicesReserve services;
    }
}
