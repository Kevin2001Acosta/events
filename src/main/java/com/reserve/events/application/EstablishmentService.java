package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Establishment;
import com.reserve.events.controllers.domain.repository.EstablishmentRepository;
import com.reserve.events.controllers.dto.EstablishmentRequest;
import com.reserve.events.controllers.response.EstablishmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;


    // Crear un nuevo establecimiento
    public EstablishmentResponse createEstablishment(EstablishmentRequest request) {
        // Primero revisamos si ya existe un establecimiento activo con el mismo nombre.
        if (establishmentRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new RuntimeException("Ya existe un establecimiento activo con el nombre: " + request.getName());
        }

        // se crea la entidad y la guardamos
        Establishment establishment = mapRequestToEntity(request);
        establishment.setActive(true); // siempre activo al crear
        Establishment saved = establishmentRepository.save(establishment);

        // Devolvemos un objeto que la API pueda usar en la respuesta
        return mapToResponse(saved);
    }


    // Listar todos los establecimientos activos
    public List<EstablishmentResponse> getAllActiveEstablishments() {
        // Buscamos solo los activos y los convertimos en responses para la API
        return establishmentRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    // Obtener un establecimiento por su id
    public EstablishmentResponse getEstablishmentById(String id) {
        // Buscamos por ID solo si está activo
        Establishment establishment = establishmentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado o inactivo"));
        return mapToResponse(establishment);
    }

    // Actualizar un establecimiento existente
    public EstablishmentResponse updateEstablishment(String id, EstablishmentRequest request) {
        // Primero buscamos el establecimiento
        Establishment existing = establishmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado con id: " + id));

        // Revisar que no exista otro establecimiento activo con el mismo nombre
        if (!existing.getName().equals(request.getName()) &&
                establishmentRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new RuntimeException("Ya existe otro establecimiento activo con ese nombre");
        }

        //Actualizar los campso con los nuevos datos
        existing.setName(request.getName());
        existing.setAddress(request.getAddress());
        existing.setPhone(request.getPhone());
        existing.setCity(request.getCity());
        existing.setCapacity(request.getCapacity());
        existing.setType(request.getType());
        existing.setCost(request.getCost());
        existing.setImageUrl(request.getImageUrl());

        //Guardamos los cambios y devolvemos la info actualizada
        Establishment saved = establishmentRepository.save(existing);
        return mapToResponse(saved);
    }


    // Borrado lógico de un establecimiento
    public void deleteEstablishment(String id) {
        // Buscamos el establecimiento
        Establishment establishment = establishmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Establecimiento no encontrado con id: " + id));

        // En vez de borrarlo de la base de datos, lo marcamos como inactivo
        establishment.setActive(false);
        establishmentRepository.save(establishment);
    }


    // Convierte la entidad interna en un objeto que se puede devolver al cliente
    // toma los datos del establecimiento y los pone en un formato como más limpio para la API

    private EstablishmentResponse mapToResponse(Establishment establishment) {
        return EstablishmentResponse.builder()
                .id(establishment.getId())
                .name(establishment.getName())
                .address(establishment.getAddress())
                .phone(establishment.getPhone())
                .city(establishment.getCity())
                .capacity(establishment.getCapacity())
                .type(establishment.getType())
                .cost(establishment.getCost())
                .imageUrl(establishment.getImageUrl())
                .active(establishment.getActive())
                .build();
    }

    // Convierte los datos que recibe la API en un objeto que se puede guardar en la base de datos
    // toma la información del request y crea un establishment listo para guardar

    private Establishment mapRequestToEntity(EstablishmentRequest request) {
        return Establishment.builder()
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .city(request.getCity())
                .capacity(request.getCapacity())
                .type(request.getType())
                .cost(request.getCost())
                .imageUrl(request.getImageUrl())
                .active(true) // siempre activo al crear
                .build();
    }
}
