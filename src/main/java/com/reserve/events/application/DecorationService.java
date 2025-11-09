package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Decoration;
import com.reserve.events.controllers.domain.repository.DecorationRepository;
import com.reserve.events.controllers.dto.DecorationRequest;
import com.reserve.events.controllers.response.DecorationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecorationService {

    private final DecorationRepository decorationRepository;

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

    private DecorationResponse mapToDecorationResponse(Decoration decoration) {
        return DecorationResponse.builder()
                .id(decoration.getId())
                .theme(decoration.getTheme())
                .articles(decoration.getArticles())
                .cost(decoration.getCost())
                .build();
    }
}
