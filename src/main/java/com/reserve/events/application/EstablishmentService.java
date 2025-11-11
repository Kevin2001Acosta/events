package com.reserve.events.application;

import java.util.Map;
import com.reserve.events.controllers.domain.entity.Establishment;
import com.reserve.events.controllers.domain.model.StatusReserve;
import com.reserve.events.controllers.domain.model.EstablishmentType;
import com.reserve.events.controllers.domain.repository.EstablishmentRepository;
import com.reserve.events.controllers.dto.EstablishmentRequest;
import com.reserve.events.controllers.exception.EstablishmentNotFoundException;
import com.reserve.events.controllers.exception.InvalidReservationDatesException;
import com.reserve.events.controllers.response.EstablishmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;


    // Crear un nuevo establecimiento
    public EstablishmentResponse createEstablishment(EstablishmentRequest request) {
        if (establishmentRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new com.reserve.events.controllers.exception.EstablishmentAlreadyExistsException("Ya existe un establecimiento activo con el nombre: " + request.getName());
        }
        validateCapacityByType(request);
        Establishment establishment = mapRequestToEntity(request);
        establishment.setActive(true);
        Establishment saved = establishmentRepository.save(establishment);
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
        Establishment establishment = establishmentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EstablishmentNotFoundException("Establecimiento no encontrado o inactivo"));
        return mapToResponse(establishment);
    }


    // Logica put que es para actulizar todos los campos
    public EstablishmentResponse updateEstablishment(String id, EstablishmentRequest request) {
        Establishment existing = establishmentRepository.findById(id)
                .orElseThrow(() -> new EstablishmentNotFoundException("Establecimiento no encontrado con id: " + id));
        if (!existing.getName().equals(request.getName()) &&
                establishmentRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new com.reserve.events.controllers.exception.EstablishmentAlreadyExistsException("Ya existe otro establecimiento activo con ese nombre");
        }
        validateCapacityByType(request);
        // ...existing code...
    }


    // Actualización parcial (Es decir, de solo los campos que queramos) (PATCH)
    public EstablishmentResponse patchEstablishment(String id, Map<String, Object> updates) {
        Establishment existing = establishmentRepository.findById(id)
                .orElseThrow(() -> new EstablishmentNotFoundException("Establecimiento no encontrado con id: " + id));
        if (updates.containsKey("name")) {
            String newName = updates.get("name").toString();
            if (!existing.getName().equals(newName) &&
                    establishmentRepository.existsByNameAndActiveTrue(newName)) {
                throw new com.reserve.events.controllers.exception.EstablishmentAlreadyExistsException("Ya existe otro establecimiento activo con ese nombre");
            }
            existing.setName(newName);
        }
        // ...existing code...
    }


    // Borrado lógico (DELETE)
    public void deleteEstablishment(String id) {
        Establishment establishment = establishmentRepository.findById(id)
                .orElseThrow(() -> new EstablishmentNotFoundException("Establecimiento no encontrado"));
        boolean hasScheduledBookings = establishment.getBookings() != null &&
                establishment.getBookings().stream()
                        .anyMatch(b -> b.getStatus() == StatusReserve.PROGRAMADA);
        if (hasScheduledBookings) {
            throw new com.reserve.events.controllers.exception.EstablishmentDeletionNotAllowedException("No se puede eliminar el establecimiento: tiene reservas programadas");
        }
        establishment.setActive(false); // borrado lógico
        establishmentRepository.save(establishment);
    }

    /** Obtiene las fechas ocupadas de un establecimiento por su ID
     * Filtrar las fechas de reservas que no están canceladas y que son futuras o presentes
     *
     * @param id ID del establecimiento
     * @return List de fechas ocupadas (futuras y presentes) sin duplicados y ordenadas
     * @throws EstablishmentNotFoundException si el establecimiento no existe o está inactivo
     */
    public List<LocalDate> getOccupiedDatesByEstablishmentId(String id){
        // Un establecimeinto inactivo no puede tener fechas futuras ocupadas
        Establishment establishment = establishmentRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new EstablishmentNotFoundException("Establecimiento no encontrado o inactivo"));

        return establishment.getBookings().stream()
                .filter(reserveSummary -> !reserveSummary.getStatus().equals(StatusReserve.CANCELADA)) // Filtrar reservas no canceladas
                .flatMap(reserveSummary -> reserveSummary.getDates().stream()) // Unir todas las listas de fechas
                .filter(date -> !date.isBefore(LocalDate.now())) // Filtrar fechas >= hoy
                .distinct() // Eliminar fechas duplicadas
                .sorted() // Ordenar las fechas
                .toList();
    }

    /** Verifica si las fechas solicitadas están disponibles para un establecimiento
     *  Las fechas recibidas deben ser futuras a la fecha actual
     * @param requestedDates Lista de fechas solicitadas
     * @param establishmentId ID del establecimiento
     * @return true si todas las fechas están disponibles -> no hacen match con las ocupadas, false si alguna está ocupada
     * @throws InvalidReservationDatesException si alguna fecha no es futura
     */
    public boolean areDatesAvailableForEstablishment(List<LocalDate> requestedDates, String establishmentId) {


        LocalDate today = LocalDate.now();
        boolean allAfterToday = requestedDates != null && requestedDates.stream().allMatch(date -> date != null && date.isAfter(today));

        if (!allAfterToday) {
            throw new InvalidReservationDatesException("Todas las fechas solicitadas deben ser futuras a la fecha actual");
        }
        List<LocalDate> occupiedDates = getOccupiedDatesByEstablishmentId(establishmentId);
        return requestedDates.stream().noneMatch(occupiedDates::contains);
    }


    // Validación de capacidad según tipo
    private void validateCapacityByType(EstablishmentRequest request) {
        if (request.getType() == null) return;

        switch (request.getType()) {
            case SMALL -> {
                if (request.getCapacity() > 50) {
                    throw new RuntimeException("Un establecimiento SMALL no puede tener capacidad mayor a 50");
                }
            }
            case MEDIUM -> {
                if (request.getCapacity() > 200) {
                    throw new RuntimeException("Un establecimiento MEDIUM no puede tener capacidad mayor a 200");
                }
            }
            case LARGE -> {
                if (request.getCapacity() <= 200) {
                    throw new RuntimeException("Un establecimiento LARGE debe tener capacidad mayor a 200");
                }
            }
        }
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

    // Convierte los datos del request en una entidad lista para guardar
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

