package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Decoration;
import com.reserve.events.controllers.domain.repository.DecorationRepository;
import com.reserve.events.controllers.dto.DecorationRequest;
import com.reserve.events.controllers.exception.ForbiddenException;
import com.reserve.events.controllers.exception.ServiceNotFoundException;
import com.reserve.events.controllers.response.DecorationResponse;
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
@Transactional
public class DecorationService {

    private final DecorationRepository decorationRepository;

    @Transactional
    public DecorationResponse createDecoration(DecorationRequest decorationRequest) {

        // Mapear DecorationRequest a la entidad Decoration
        Decoration decoration = Decoration.builder()
                .theme(decorationRequest.getTheme())
                .articles(decorationRequest.getArticles())
                .cost(decorationRequest.getCost())
                .build();

        Decoration savedDecoration = decorationRepository.save(decoration);
        log.info("Servicio de decoración creado con ID: {}", savedDecoration.getId());

        // Convertir a DTO y retornar
        return mapToDecorationResponse(savedDecoration);
    }

    @Transactional(readOnly = true)
    public List<DecorationResponse> getAllDecoration() {
        return decorationRepository.findAll().stream()
                .map(this::mapToDecorationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DecorationResponse getDecorationById(String id) {
        return decorationRepository.findById(id)
                .map(this::mapToDecorationResponse)
                .orElseThrow(() -> new ServiceNotFoundException("Servicio no encontrado con ID: " + id));
    }

    @Transactional
    public DecorationResponse updateDecoration(String id, DecorationRequest decorationRequest){
        return decorationRepository.findById(id)
                .map(decoration -> {
                    if (!(decorationRequest.getTheme().equals(decoration.getTheme()))) {
                        throw new ForbiddenException("El theme no puede ser editado");
                    }

                    // Actualizar campos
                    decoration.setArticles(decorationRequest.getArticles());
                    decoration.setCost(decorationRequest.getCost());
                    decoration.setTheme(decorationRequest.getTheme());


                    // Guardar el libro actualizado
                    Decoration updatedDecoration = decorationRepository.save(decoration);
                    log.info("Servicio de decoración actualizado con ID: {}", id);

                    return mapToDecorationResponse(updatedDecoration);
                })
                .orElseThrow(() -> new ServiceNotFoundException("No se puede actualizar. Servicio de decoración no encontrado con ID: " + id));

    }

    private DecorationResponse mapToDecorationResponse(Decoration decoration) {
        return DecorationResponse.builder()
                .id(decoration.getId())
                .theme(decoration.getTheme())
                .articles(decoration.getArticles())
                .cost(decoration.getCost())
                .scheduledBookings(decoration.getScheduledBookings())
                .completedBookings(decoration.getCompletedBookings())
                .cancelledBookings(decoration.getCancelledBookings())
                .build();
    }
}
