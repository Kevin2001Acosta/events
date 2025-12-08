package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Entertainment;
import com.reserve.events.controllers.domain.repository.EntertainmentRepository;
import com.reserve.events.controllers.dto.EntertainmentRequest;
import com.reserve.events.controllers.exception.ServiceAlreadyExistsException;
import com.reserve.events.controllers.exception.ServiceNotFoundException;
import com.reserve.events.controllers.response.EntertainmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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

    @Transactional(readOnly = true)
    public EntertainmentResponse getEntertainmentById(String id) {
        return entertainmentRepository.findById(id)
                .map(this::mapToEntertainmentResponse)
                .orElseThrow(() -> new ServiceNotFoundException("Servicio no encontrado con ID: " + id));
    }

    @Transactional
    public EntertainmentResponse updateEntertainment(String id, EntertainmentRequest entertainmentRequest) {
        return entertainmentRepository.findById(id)
                .map(entertainment -> {
                    // Actualizar campos
                    entertainment.setDescription(entertainmentRequest.getDescription());
                    entertainment.setHourlyRate(entertainmentRequest.getHourlyRate());
                    entertainment.setType(entertainmentRequest.getType());


                    // Verificar si el nombre ha cambiado y si ya existe
                    if (!entertainment.getName().equals(entertainmentRequest.getName()) &&
                            entertainmentRepository.existsByName(entertainmentRequest.getName())) {
                        throw new ServiceAlreadyExistsException("Ya existe un servicio de decoración con el nombre: " + entertainmentRequest.getName());
                    }
                    entertainment.setName(entertainmentRequest.getName());

                    // Guardar el libro actualizado
                    Entertainment updatedEntertainment = entertainmentRepository.save(entertainment);
                    log.info("Servicio actualizado con ID: {}", id);

                    return mapToEntertainmentResponse(updatedEntertainment);
                })
                .orElseThrow(() -> new ServiceNotFoundException("No se puede actualizar. Servicio no encontrado con ID: " + id));
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
