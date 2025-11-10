package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Catering;
import com.reserve.events.controllers.domain.repository.CateringRepository;
import com.reserve.events.controllers.dto.CateringRequest;
import com.reserve.events.controllers.response.CateringResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CateringService {

    private final CateringRepository cateringRepository;

    @Transactional
    public CateringResponse createCatering(CateringRequest request){

        // Mapear cateringRequest a la entidad catering
        Catering catering = Catering.builder()
                .menuType(request.getMenuType())
                .description(request.getDescription())
                .costDish(request.getCostDish())
                .build();

        // Guardar el servicio
        Catering savedCatering = cateringRepository.save(catering);
        log.info("Servicio de entretenimiento creado con ID: {}", savedCatering.getId());

        // Convertir a DTO y retornar
        return mapToCateringResponse(savedCatering);
    }

    private CateringResponse mapToCateringResponse(Catering catering){
        return CateringResponse.builder()
                .id(catering.getId())
                .menuType(catering.getMenuType())
                .description(catering.getDescription())
                .costDish(catering.getCostDish())
                .build();
    }
}
