package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Entertainment;
import com.reserve.events.controllers.domain.repository.EntertainmentRepository;
import com.reserve.events.controllers.dto.EntertainmentRequest;
import com.reserve.events.controllers.exception.ServiceAlreadyExistsException;
import com.reserve.events.controllers.response.EntertainmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntertainmentService {

    private final EntertainmentRepository entertainmentRepository;

    @Transactional
    public EntertainmentResponse createEntertainment(EntertainmentRequest request){

        // Verificar si el entretenimiento ya existe
        if(entertainmentRepository.existsByName(request.getName())){
            throw new ServiceAlreadyExistsException("Ya existe un servicio de entretenimiento con el mismo nombre: " + request.getName());
        }

        // Mapear entertainmentRequest a la entidad entertainment
        Entertainment entertainment = Entertainment.builder()
                .name(request.getName())
                .type(request.getType())
                .description(request.getDescription())
                .hourlyRate(request.getHourlyRate())
                .build();

        // Guardar el servicio
        Entertainment savedEntertainment = entertainmentRepository.save(entertainment);
        log.info("Servicio de entretenimiento creado con ID: {}", savedEntertainment.getId());

        // Convertir a DTO y retornar
        return mapToEntertainmentResponse(savedEntertainment);
    }

    @Transactional(readOnly = true)
    public List<EntertainmentResponse> getAllEntertainment() {
        return entertainmentRepository.findAll().stream()
                .map(this::mapToEntertainmentResponse)
                .collect(Collectors.toList());
    }

    private EntertainmentResponse mapToEntertainmentResponse(Entertainment entertainment){
        return EntertainmentResponse.builder()
                .id(entertainment.getId())
                .name(entertainment.getName())
                .type(entertainment.getType())
                .description(entertainment.getDescription())
                .hourlyRate(entertainment.getHourlyRate())
                .build();
    }
}
