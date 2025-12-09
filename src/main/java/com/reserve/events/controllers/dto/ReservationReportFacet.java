package com.reserve.events.controllers.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReservationReportFacet {
    private List<TotalReservations> totalReservations;
    private List<StatusCount> byStatus;
    private List<EventTypeCount> topEventTypes;
}
