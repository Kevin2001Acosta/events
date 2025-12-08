package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Adittional;
import com.reserve.events.controllers.domain.repository.AdittionalRepository;
import com.reserve.events.controllers.dto.AdittionalRequest;
import com.reserve.events.controllers.exception.ServiceAlreadyExistsException;
import com.reserve.events.controllers.exception.ServiceNotFoundException;
import com.reserve.events.controllers.response.AdittionalResponse;
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
public class AdittionalService {

    private final AdittionalRepository adittionalRepository;

    @Transactional
    public AdittionalResponse createAdittional(AdittionalRequest adittionalRequest) {

        // Verificar si el servicio adicional existe
        if(adittionalRepository.existsByName(adittionalRequest.getName())){
            throw new ServiceAlreadyExistsException("Ya existe un servicio adicional con el mismo nombre: " + adittionalRequest.getName());
        }

        // Mapear adittionalRequest a la entidad Adittional
        Adittional adittional = Adittional.builder()
                .name(adittionalRequest.getName())
                .description(adittionalRequest.getDescription())
                .cost(adittionalRequest.getCost())
                .build();

        // Guardar el servicio
        Adittional savedAdittional = adittionalRepository.save(adittional);
        log.info("Servicio adicional creado con ID: {}", savedAdittional.getId());

        // Convertir a DTO y retornar
        return mapToAdittionalResponse(savedAdittional);
    }

    @Transactional(readOnly = true)
    public List<AdittionalResponse> getAllAdittional() {
        return adittionalRepository.findAll().stream()
                .map(this::mapToAdittionalResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdittionalResponse getAdittionalById(String id) {
        return adittionalRepository.findById(id)
                .map(this::mapToAdittionalResponse)
                .orElseThrow(() -> new ServiceNotFoundException("Servicio no encontrado con ID: " + id));
    }

    @Transactional
    public AdittionalResponse updateAdittional(String id, AdittionalRequest adittionalRequest) {
        return adittionalRepository.findById(id)
                .map(additional -> {

                    // Actualizar campos
                    additional.setDescription(adittionalRequest.getDescription());
                    additional.setCost(adittionalRequest.getCost());

                    // Verificar si el nombre ha cambiado y si ya existe
                    if (!additional.getName().equals(adittionalRequest.getName()) &&
                            adittionalRepository.existsByName(adittionalRequest.getName())) {
                        throw new ServiceAlreadyExistsException("Ya existe un servicio adicional con el nombre: " + adittionalRequest.getName());
                    }
                    additional.setName(adittionalRequest.getName());

                    // Guardar el servicio actualizado
                    Adittional updatedAdittional = adittionalRepository.save(additional);
                    log.info("Servicio actualizado con ID: {}", id);

                    return mapToAdittionalResponse(updatedAdittional);})
                .orElseThrow(() -> new ServiceNotFoundException("No se puede actualizar. Servicio adicional no encontrado con ID: " + id));
    }

    private AdittionalResponse mapToAdittionalResponse(Adittional adittional) {
        return AdittionalResponse.builder()
                .id(adittional.getId())
                .name(adittional.getName())
                .description(adittional.getDescription())
                .cost(adittional.getCost())
                .build();
    }
}
