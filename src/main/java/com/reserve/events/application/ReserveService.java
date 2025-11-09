package com.reserve.events.application;

import com.reserve.events.controllers.domain.repository.UserRepository;
import com.reserve.events.controllers.dto.ReserveRequest;
import com.reserve.events.controllers.response.ReserveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveService {

    //@Transactional
    //public ReserveResponse createReserve(ReserveRequest request){

        // Verificar si el cliente existe

        // Verificar si el evento existe

        // Verificar si el establecimiento existe

        // fechas ¿ que hay que verificar en fechas? que si este disponible el establecimiento?

        // En el caso de que elija uno o mas servicios, verificar que existan

        //calcular automaticamente el costo total de los servicios

        // Guardar la reserva con estado inicial pendiente

        // Guardar la reserva en usuarios

        // Agregar las fechas reservadas al establecimiento

  //  }
}
