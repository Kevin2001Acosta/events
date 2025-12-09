package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.*;
import com.reserve.events.controllers.domain.model.*;
import com.reserve.events.controllers.domain.repository.*;
import com.reserve.events.controllers.dto.ReserveRequest;
import com.reserve.events.controllers.exception.*;
import com.reserve.events.controllers.response.ReserveResponse;
import com.reserve.events.controllers.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final EstablishmentRepository establishmentRepository;
    private final EstablishmentService establishmentService;
    private final EntertainmentRepository entertainmentRepository;
    private final DecorationRepository decorationRepository;
    private final CateringRepository cateringRepository;
    private final AdittionalRepository adittionalRepository;

    // DONE: Verificar que los invitados no excedan el cupo max del establecimiento
    // TO DO: Agregar los errores que no están al global exception
    // TO DO: Hacer el get de los servicios y la reserva
    // DONE: Revisar que las fechas dadas en la lista de Dates sean del presente o del futuro, que no se puedan fechas pasadas

    @Transactional
    public ReserveResponse createReserve(ReserveRequest request, String email){

        // Validar que el cliente exista
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No existe un usuario con el correo: " + email));

        // Validar que el evento exista
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("No existe un evento con el id: " + request.getEventId()));

        // Validar que el establecimiento exista
        Establishment establishment = establishmentRepository.findById(request.getEstablishmentId())
                .orElseThrow(() -> new EstablishmentNotFoundException("No existe un establecimiento con el id: " + request.getEstablishmentId()));

        // Validar que el cupo no exceda el máximo del establecimiento
        boolean guestCapacity = establishment.getCapacity() < request.getGuestNumber();
        if (guestCapacity){
            throw new BadRequestException("El número de invitados excede la capacidad del local. Su capacidad es de " + establishment.getCapacity());
        }

        // Validar que todas las fechas sean futuras
        java.time.LocalDate today = java.time.LocalDate.now();
        for (java.time.LocalDate date : request.getDates()) {
            if (date.isBefore(today)) {
                throw new BadRequestException("No se pueden reservar fechas pasadas. La fecha " + date + " es anterior a hoy.");
            }
        }

        // Validar que las fechas en las que se quiere reservar si estan disponibles
        boolean datesAvailable = establishmentService.areDatesAvailableForEstablishment(request.getDates(), request.getEstablishmentId());
        if (!datesAvailable) {
            throw new AvailableEstablishmentNotFoundException("El establecimiento escogido para la reserva no tiene disponibilidad en las fechas: " + request.getDates());
        }

        // Iniciar el costo total de la reserva, en ambos casos, el costo del establecimiento es el costo base.
        double costReserveTotal = establishment.getCost();

        // Verificar si no se escogio ningun servicio
        boolean isWithoutServices = noServices(request.getServices());

        // objeto de servicios vacio
        CoveredServicesReserve covered = new CoveredServicesReserve();

        // Calcular el costo total si se adquirieron servicios, además crear el obj de servicios
        if(!isWithoutServices){

            if (!request.getServices().getEntertainment().isEmpty()) {
                for (ReserveRequest.EntertainmentRequest entReq: request.getServices().getEntertainment()) {
                    //Buscar el servicio
                    Entertainment ent = entertainmentRepository.findById(entReq.getId())
                            .orElseThrow(()-> new ServiceNotFoundException("No existe el servicio de entretenimiento con id:" + entReq.getId()));

                    //Crear el summary
                    EntertainmentSummary summaryEntertainment = createEntertainmentSummary(ent, entReq.getHours());

                    // 3. Agregar al objeto de servicios cubiertos
                    covered.getEntertainment().add(summaryEntertainment);

                    // 4. Sumar el costo total
                    costReserveTotal += summaryEntertainment.getTotalCost();
                }
            }

            if (!(request.getServices().getDecoration() == null)){
                String idDecoracion = request.getServices().getDecoration().getId();
                Decoration decoration = decorationRepository.findById(idDecoracion)
                        .orElseThrow(() -> new ServiceNotFoundException("No existe el servicio de decoración con id: " + idDecoracion));

                DecorationSummary decorationSummary = createDecorationSummary(decoration);
                covered.setDecoration(decorationSummary);

                costReserveTotal += decorationSummary.getCost();
            }

            if (!request.getServices().getCatering().isEmpty()) {
                for (ReserveRequest.CateringRequest catReq: request.getServices().getCatering()) {
                    //Buscar el servicio
                    Catering cat = cateringRepository.findById(catReq.getId())
                            .orElseThrow(()-> new ServiceNotFoundException("No existe el servicio de catering con id:" + catReq.getId()));

                    //Crear el summary
                    CateringSummary summaryCatering = createCateringSummary(cat, catReq.getNumberDish());

                    // 3. Agregar al objeto de servicios cubiertos
                    covered.getCatering().add(summaryCatering);

                    // 4. Sumar el costo total
                    costReserveTotal += summaryCatering.getTotalCost();
                }
            }

            if (!(request.getServices().getAdditionalServices().isEmpty())){
                for (ReserveRequest.AdditionalRequest addReq: request.getServices().getAdditionalServices()) {
                    //Buscar el servicio
                    Adittional add = adittionalRepository.findById(addReq.getId())
                            .orElseThrow(()-> new ServiceNotFoundException("No existe el servicio adicional con id:" + addReq.getId()));

                    //Crear el summary
                    AdittionalSummary summaryAdittional = createAdittionalSummary(add, addReq.getQuantity());

                    // 3. Agregar al objeto de servicios cubiertos
                    covered.getAdditionalServices().add(summaryAdittional);

                    // 4. Sumar el costo total
                    costReserveTotal += summaryAdittional.getTotalCost();
                }
            }
        }

        // Crear los summary
        UserSummary userSummary = createUserSummary(user);
        EventSummary eventSummary = createEventSummary(event);
        EstablishmentSummary establishmentSummary = createEstablishmentSummary(establishment, request.getDates().size());

        // Establecer estado en PROGRAMADO
        StatusReserve status = StatusReserve.PROGRAMADA;

        // Mapear el ReserveRequest a la entidad Reserve
        Reserve reserve = Reserve.builder()
                .status(status)
                .guestNumber(request.getGuestNumber())
                .dates(request.getDates())
                .totalCost(costReserveTotal)
                .comments(request.getComments())
                .client(userSummary)
                .event(eventSummary)
                .establishment(establishmentSummary)
                .services(covered)
                .build();

        // Guardar la reserva
        Reserve savedReserve = reserveRepository.save(reserve);
        log.info("Reserva creada con ID: {}", savedReserve.getId());

        // Guardar la reserva en usuario, establecimiento, evento y decoración (si aplica)
        updatedUserWithNewReserve(savedReserve, user.getId());
        updatedEstablishmentWithNewReserve(savedReserve, request.getEstablishmentId(), userSummary);
        updatedEventWithNewReserve(savedReserve, request.getEventId(), userSummary, establishmentSummary);

        // Si tiene decoración, agregar la reserva a la decoración
        if (request.getServices().getDecoration() != null) {
            updatedDecorationWithNewReserve(savedReserve, request.getServices().getDecoration().getId(), userSummary, eventSummary, establishmentSummary);
        }

        // Crear el pago de la reserva en estado PENDIENTE
        createPaymentForReserve(savedReserve, userSummary, establishmentSummary);

        //Covertir a response y retornar
        return mapToReserveResponse(savedReserve);
    }

    /**
     * Lista las reservas del usuario autenticado identificado por su email
     */
    public List<ReserveResponse> listReservesByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No existe un usuario con el correo: " + email));

        java.util.List<Reserve> reserves = reserveRepository.findByClientId(user.getId());

        return reserves.stream().map(this::mapToReserveResponse).collect(Collectors.toList());
    }

    /**
     * Obtiene el detalle de una reserva por id para el usuario autenticado (o ADMIN)
     */
    public ReserveResponse getReserveById(UserDetails userDetails, String id) {
        Reserve reserve = reserveRepository.findById(id)
            .orElseThrow(() -> new ReserveNotFoundException("Reserva no encontrada con ID: " + id));

        // Si el usuario no es ADMIN, validar que sea el dueño
        User currentUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + userDetails.getUsername()));

        if(!currentUser.getType().equals(UserType.ADMIN) && !reserve.getClient().getId().equals(currentUser.getId())){
            throw new ForbiddenException("No tienes permisos para ver esta reserva");
        }

        return mapToReserveResponse(reserve);
    }

    /**
     * Actualiza una reserva (solo si está en estado PROGRAMADA)
     */
    @Transactional
    public ReserveResponse updateReserve(UserDetails userDetails, String id, ReserveRequest request){
        Reserve reserva = reserveRepository.findById(id)
            .orElseThrow(() -> new ReserveNotFoundException("Reserva no encontrada con ID: " + id));

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + userDetails.getUsername()));

        if(!reserva.getClient().getId().equals(user.getId())){
            throw new ForbiddenException("Como CLIENTE no puedes modificar una reserva que no es tuya.");
        }

        if (reserva.getStatus() != StatusReserve.PROGRAMADA) {
            throw new BadRequestException("Solo se pueden editar reservas en estado PROGRAMADA.");
        }

        // Validar que todas las fechas sean futuras
        java.time.LocalDate today = java.time.LocalDate.now();
        for (java.time.LocalDate date : request.getDates()) {
            if (date.isBefore(today)) {
                throw new BadRequestException("No se pueden reservar fechas pasadas. La fecha " + date + " es anterior a hoy.");
            }
        }

        // Validar establecimiento si cambia
        Establishment establishment = establishmentRepository.findById(request.getEstablishmentId())
                .orElseThrow(() -> new EstablishmentNotFoundException("No existe un establecimiento con el id: " + request.getEstablishmentId()));

        // Validar disponibilidad de fechas
        boolean datesAvailable = establishmentService.areDatesAvailableForEstablishment(request.getDates(), request.getEstablishmentId());
        if (!datesAvailable) {
            throw new AvailableEstablishmentNotFoundException("El establecimiento escogido para la reserva no tiene disponibilidad en las fechas: " + request.getDates());
        }

        // Actualizar campos básicos
        reserva.setGuestNumber(request.getGuestNumber());
        reserva.setDates(request.getDates());
        reserva.setComments(request.getComments());
        reserva.setEstablishment(createEstablishmentSummary(establishment, request.getDates().size()));

        // Recalcular costo total de la reserva (similar a createReserve)
        double costReserveTotal = establishment.getCost() * request.getDates().size();
        CoveredServicesReserve covered = new CoveredServicesReserve();

        if(!noServices(request.getServices())){
            if (!request.getServices().getEntertainment().isEmpty()) {
                for (ReserveRequest.EntertainmentRequest entReq: request.getServices().getEntertainment()) {
                    Entertainment ent = entertainmentRepository.findById(entReq.getId())
                            .orElseThrow(()-> new ServiceNotFoundException("No existe el servicio de entretenimiento con id:" + entReq.getId()));
                    EntertainmentSummary summaryEntertainment = createEntertainmentSummary(ent, entReq.getHours());
                    covered.getEntertainment().add(summaryEntertainment);
                    costReserveTotal += summaryEntertainment.getTotalCost();
                }
            }

            if (!(request.getServices().getDecoration() == null)){
                String idDecoracion = request.getServices().getDecoration().getId();
                Decoration decoration = decorationRepository.findById(idDecoracion)
                        .orElseThrow(() -> new ServiceNotFoundException("No existe el servicio de decoración con id: " + idDecoracion));

                DecorationSummary decorationSummary = createDecorationSummary(decoration);
                covered.setDecoration(decorationSummary);

                costReserveTotal += decorationSummary.getCost();
            }

            if (!request.getServices().getCatering().isEmpty()) {
                for (ReserveRequest.CateringRequest catReq: request.getServices().getCatering()) {
                    Catering cat = cateringRepository.findById(catReq.getId())
                            .orElseThrow(()-> new ServiceNotFoundException("No existe el servicio de catering con id:" + catReq.getId()));
                    CateringSummary summaryCatering = createCateringSummary(cat, catReq.getNumberDish());
                    covered.getCatering().add(summaryCatering);
                    costReserveTotal += summaryCatering.getTotalCost();
                }
            }

            if (!(request.getServices().getAdditionalServices().isEmpty())){
                for (ReserveRequest.AdditionalRequest addReq: request.getServices().getAdditionalServices()) {
                    Adittional add = adittionalRepository.findById(addReq.getId())
                            .orElseThrow(()-> new ServiceNotFoundException("No existe el servicio adicional con id:" + addReq.getId()));
                    AdittionalSummary summaryAdittional = createAdittionalSummary(add, addReq.getQuantity());
                    covered.getAdditionalServices().add(summaryAdittional);
                    costReserveTotal += summaryAdittional.getTotalCost();
                }
            }
        }

        reserva.setServices(covered);
        reserva.setTotalCost(costReserveTotal);

        Reserve saved = reserveRepository.save(reserva);

        // actualizar reserva en scheduledBookings del usuario
        user.getScheduledBookings().stream()
                .filter(b -> b.getId().equals(saved.getId()))
                .findFirst()
                .ifPresent(b -> {
                    b.setStatus(saved.getStatus());
                    b.setDates(saved.getDates());
                    b.setServices(saved.getServices());
                });
        userRepository.save(user);

        // actualizar reserva en scheduledBookings del establecimiento
        updateReserveInEstablishment(saved);

        // actualizar reserva en scheduledBookings del evento
        updateReserveInEvent(saved);

        // Si tiene decoración, actualizar la reserva en la decoración
        if (saved.getServices() != null && saved.getServices().getDecoration() != null) {
            updateReserveInDecoration(saved);
        }

        return mapToReserveResponse(saved);
    }

    private void updatedUserWithNewReserve(Reserve reserve, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("No existe el usuario con id:" + userId));

        ReserveSummary summaryReserve = createReserveSummary(reserve);

        // Nueva reserva siempre va a scheduledBookings
        user.getScheduledBookings().add(summaryReserve);
        userRepository.save(user);
        log.info("Usuario '{}' actualizado con la reserva '{}'", user.getFullName(), summaryReserve.getId());
    }

    private void updatedEstablishmentWithNewReserve(Reserve reserve, String establishmentId, UserSummary user) {
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new EstablishmentNotFoundException("No existe el establishment con id:" + establishmentId));

        Establishment.ReserveSummary summaryReserve = createReserveSummaryForEstablishment(reserve, user);

        // Nueva reserva siempre va a scheduledBookings
        establishment.getScheduledBookings().add(summaryReserve);
        establishmentRepository.save(establishment);
        log.info("Establecimiento '{}' actualizado con la reserva '{}'", establishment.getName(), summaryReserve.getId());
    }

    private void updatedEventWithNewReserve(Reserve reserve, String eventId, UserSummary user, EstablishmentSummary establishmentSummary) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No existe el evento con id:" + eventId));

        Event.ReserveSummary summaryReserve = createReserveSummaryForEvent(reserve, user, establishmentSummary);

        // Nueva reserva siempre va a scheduledBookings
        event.getScheduledBookings().add(summaryReserve);
        eventRepository.save(event);
        log.info("Evento '{}' actualizado con la reserva '{}'", event.getType(), summaryReserve.getId());
    }

    private void updatedDecorationWithNewReserve(Reserve reserve, String decorationId, UserSummary user, EventSummary eventSummary, EstablishmentSummary establishmentSummary) {
        Decoration decoration = decorationRepository.findById(decorationId)
                .orElseThrow(() -> new ServiceNotFoundException("No existe la decoración con id:" + decorationId));

        Decoration.ReserveSummary summaryReserve = createReserveSummaryForDecoration(reserve, user, eventSummary, establishmentSummary);

        // Nueva reserva siempre va a scheduledBookings
        decoration.getScheduledBookings().add(summaryReserve);
        decorationRepository.save(decoration);
        log.info("Decoración '{}' actualizada con la reserva '{}'", decoration.getTheme(), summaryReserve.getId());
    }

    private boolean noServices(ReserveRequest.CoveredServicesRequest services){
        return services.getEntertainment().isEmpty() &&
                services.getCatering().isEmpty() &&
                services.getDecoration() == null &&
                services.getAdditionalServices().isEmpty();
    }

    private UserSummary createUserSummary(User user) {
        return UserSummary.builder()
                .id(user.getId())
                .name(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    private EventSummary createEventSummary(Event event) {
        return EventSummary.builder()
                .id(event.getId())
                .type(event.getType())
                .build();
    }

    private EstablishmentSummary createEstablishmentSummary(Establishment establishment, Integer days) {
        double costTotalEstablishment = establishment.getCost() * days;
        return EstablishmentSummary.builder()
                .id(establishment.getId())
                .name(establishment.getName())
                .address(establishment.getAddress())
                .phone(establishment.getPhone())
                .totalCost(costTotalEstablishment)
                .build();
    }

    private EntertainmentSummary createEntertainmentSummary(Entertainment entertainment, int hours) {
        double totalCost = entertainment.getHourlyRate() * hours;
        return EntertainmentSummary.builder()
                .id(entertainment.getId())
                .name(entertainment.getName())
                .type(entertainment.getType())
                .hours(hours)
                .totalCost(totalCost)
                .build();
    }

    private DecorationSummary createDecorationSummary(Decoration decoration) {
        return DecorationSummary.builder()
                .id(decoration.getId())
                .articles(decoration.getArticles())
                .cost(decoration.getCost())
                .build();
    }

    private CateringSummary createCateringSummary(Catering catering, int numberDish) {
        double totalCost = catering.getCostDish() * numberDish;
        return CateringSummary.builder()
                .id(catering.getId())
                .menuType(catering.getMenuType())
                .description(catering.getDescription())
                .numberDish(numberDish)
                .totalCost(totalCost)
                .build();
    }

    private AdittionalSummary createAdittionalSummary(Adittional adittional, int quantity) {
        double totalCost = adittional.getCost() * quantity;
        return AdittionalSummary.builder()
                .id(adittional.getId())
                .name(adittional.getName())
                .description(adittional.getDescription())
                .quantity(quantity)
                .totalCost(totalCost)
                .build();
    }

    private ReserveSummary createReserveSummary(Reserve reserve) {
        return ReserveSummary.builder()
                .id(reserve.getId())
                .status(reserve.getStatus())
                .event(reserve.getEvent())
                .establishment(reserve.getEstablishment())
                .dates(reserve.getDates())
                .services(reserve.getServices())
                .build();
    }

    private Establishment.ReserveSummary createReserveSummaryForEstablishment(Reserve reserve, UserSummary user) {
        return Establishment.ReserveSummary.builder()
                .id(reserve.getId())
                .status(reserve.getStatus())
                .user(user)
                .event(reserve.getEvent())
                .dates(reserve.getDates())
                .services(reserve.getServices())
                .build();
    }

    private Event.ReserveSummary createReserveSummaryForEvent(Reserve reserve, UserSummary user, EstablishmentSummary establishment) {
        return Event.ReserveSummary.builder()
                .id(reserve.getId())
                .status(reserve.getStatus())
                .user(user)
                .establishment(establishment)
                .dates(reserve.getDates())
                .services(reserve.getServices())
                .build();
    }

    private Decoration.ReserveSummary createReserveSummaryForDecoration(Reserve reserve, UserSummary user, EventSummary event, EstablishmentSummary establishment) {
        return Decoration.ReserveSummary.builder()
                .id(reserve.getId())
                .status(reserve.getStatus())
                .user(user)
                .event(event)
                .establishment(establishment)
                .dates(reserve.getDates())
                .build();
    }

    @Transactional
    public Reserve cancelarReserva(UserDetails userDetails, String id) {
        Reserve reserva = reserveRepository.findById(id)
                .orElseThrow(() -> new ReserveNotFoundException("Reserva no encontrada con ID: " + id));

        // traer al usuario logueado
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + userDetails.getUsername()));

        // verifica que si el usuario es cliente, sea el dueño de la reserva a cancelar
        if(!user.getType().equals(UserType.CLIENTE) || !reserva.getClient().getId().equals(user.getId())) {
            throw new ForbiddenException("Como CLIENTE no puedes cancelar una reserva que no es tuya.");
        }

        // Verificar que no esté ya cancelada o completada
        if (reserva.getStatus() == StatusReserve.CANCELADA) {
            throw new ReservationAlreadyCancelledException("La reserva ya está cancelada.");
        }
        if (reserva.getStatus() == StatusReserve.COMPLETADA) {
            throw new ReservationCompletedCannotCancelException("No se puede cancelar una reserva completada.");
        }

        // Actualizar estado a CANCELADA
        reserva.setStatus(StatusReserve.CANCELADA);
        // eliminar Pago asociado a la reserva
        paymentRepository.deletePaymentByReserve_Id(reserva.getId());

        // Actualizar el estado de la reserva en las reservas del usuario
        updateReserveToCanceledInUser(reserva.getId(), user);

        // Buscar el establecimiento y actualizar su reserva
        updateReserveToCanceledInEstablishment(reserva.getEstablishment().getId(), reserva.getId());

        // Buscar el evento y actualizar su reserva
        updateReserveToCanceledInEvent(reserva.getEvent().getId(), reserva.getId());

        // Si tiene decoración, actualizar la reserva en la decoración
        if (reserva.getServices() != null && reserva.getServices().getDecoration() != null) {
            updateReserveToCanceledInDecoration(reserva.getServices().getDecoration().getId(), reserva.getId());
        }

        return reserveRepository.save(reserva);
    }

    private void updateReserveToCanceledInUser(String reservaId, User user) {
        // Buscar la reserva en scheduledBookings, actualizar status y mover a cancelledBookings
        user.getScheduledBookings().stream()
                .filter(booking -> booking.getId().equals(reservaId))
                .findFirst()
                .ifPresent(booking -> {
                    booking.setStatus(StatusReserve.CANCELADA);
                    user.getScheduledBookings().remove(booking);
                    user.getCancelledBookings().add(booking);
                });

        // Guardar los cambios en el usuario
        userRepository.save(user);
    }

    private void updateReserveToCanceledInEstablishment(String establishmentId, String reservaId){
        establishmentRepository.findById(establishmentId)
                .ifPresent(establishment -> {
                    // Buscar la reserva en scheduledBookings, actualizar status y mover a cancelledBookings
                    establishment.getScheduledBookings().stream()
                            .filter(booking -> booking.getId().equals(reservaId))
                            .findFirst()
                            .ifPresent(booking -> {
                                booking.setStatus(StatusReserve.CANCELADA);
                                establishment.getScheduledBookings().remove(booking);
                                establishment.getCancelledBookings().add(booking);
                            });
                    // Guardar los cambios en el establecimiento
                    establishmentRepository.save(establishment);
                });
    }

    private void updateReserveToCanceledInEvent(String eventId, String reservaId){
        eventRepository.findById(eventId)
                .ifPresent(event -> {
                    // Buscar la reserva en scheduledBookings, actualizar status y mover a cancelledBookings
                    event.getScheduledBookings().stream()
                            .filter(booking -> booking.getId().equals(reservaId))
                            .findFirst()
                            .ifPresent(booking -> {
                                booking.setStatus(StatusReserve.CANCELADA);
                                event.getScheduledBookings().remove(booking);
                                event.getCancelledBookings().add(booking);
                            });
                    // Guardar los cambios en el evento
                    eventRepository.save(event);
                });
    }

    private void updateReserveToCanceledInDecoration(String decorationId, String reservaId){
        decorationRepository.findById(decorationId)
                .ifPresent(decoration -> {
                    // Buscar la reserva en scheduledBookings, actualizar status y mover a cancelledBookings
                    decoration.getScheduledBookings().stream()
                            .filter(booking -> booking.getId().equals(reservaId))
                            .findFirst()
                            .ifPresent(booking -> {
                                booking.setStatus(StatusReserve.CANCELADA);
                                decoration.getScheduledBookings().remove(booking);
                                decoration.getCancelledBookings().add(booking);
                            });
                    // Guardar los cambios en la decoración
                    decorationRepository.save(decoration);
                });
    }

    private void updateReserveInEstablishment(Reserve reserve) {
        establishmentRepository.findById(reserve.getEstablishment().getId())
                .ifPresent(establishment -> {
                    establishment.getScheduledBookings().stream()
                            .filter(booking -> booking.getId().equals(reserve.getId()))
                            .findFirst()
                            .ifPresent(booking -> {
                                booking.setDates(reserve.getDates());
                                booking.setServices(reserve.getServices());
                            });
                    establishmentRepository.save(establishment);
                });
    }

    private void updateReserveInEvent(Reserve reserve) {
        eventRepository.findById(reserve.getEvent().getId())
                .ifPresent(event -> {
                    event.getScheduledBookings().stream()
                            .filter(booking -> booking.getId().equals(reserve.getId()))
                            .findFirst()
                            .ifPresent(booking -> {
                                booking.setDates(reserve.getDates());
                                booking.setServices(reserve.getServices());
                            });
                    eventRepository.save(event);
                });
    }

    private void updateReserveInDecoration(Reserve reserve) {
        if (reserve.getServices() != null && reserve.getServices().getDecoration() != null) {
            decorationRepository.findById(reserve.getServices().getDecoration().getId())
                    .ifPresent(decoration -> {
                        decoration.getScheduledBookings().stream()
                                .filter(booking -> booking.getId().equals(reserve.getId()))
                                .findFirst()
                                .ifPresent(booking -> {
                                    booking.setDates(reserve.getDates());
                                });
                        decorationRepository.save(decoration);
                    });
        }
    }

    public ReserveResponse mapToReserveResponse(Reserve reserve) {
        return ReserveResponse.builder()
                .id(reserve.getId())
                .status(reserve.getStatus())
                .dates(reserve.getDates())
                .guestNumber(reserve.getGuestNumber())
                .client(reserve.getClient())
                .event(reserve.getEvent())
                .establishment(reserve.getEstablishment())
                .services(reserve.getServices())
                .totalCost(reserve.getTotalCost())
                .comments(reserve.getComments())
                .build();
    }

    /**
     * Crea un pago en estado PENDIENTE para la reserva recién creada.
     */
    private void createPaymentForReserve(Reserve reserve, UserSummary client, EstablishmentSummary establishment) {
        // Crear ReserveInfo para el pago
        Payment.ReserveInfo reserveInfo = Payment.ReserveInfo.builder()
                .id(reserve.getId())
                .status(reserve.getStatus())
                .build();

        // Convertir servicios de reserva a servicios de pago
        Payment.CoveredServices coveredServices = mapToCoveredServicesPayment(reserve.getServices());

        // Crear descripción del pago
        String description = "Pago por reserva de " + reserve.getEvent().getType() +
                             " en " + establishment.getName();

        // Crear el pago
        Payment payment = Payment.builder()
                .description(description)
                .status(PaymentStatus.PENDIENTE)
                .totalCost(reserve.getTotalCost())
                .client(client)
                .reserve(reserveInfo)
                .establishment(establishment)
                .coveredServices(coveredServices)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Pago creado con ID: {} para la reserva: {}", savedPayment.getId(), reserve.getId());
    }

    /**
     * Convierte CoveredServicesReserve a Payment.CoveredServices
     */
    private Payment.CoveredServices mapToCoveredServicesPayment(CoveredServicesReserve services) {
        if (services == null) {
            return Payment.CoveredServices.builder().build();
        }

        // Mapear entretenimiento
        List<Payment.EntertainmentInfo> entertainmentList = new ArrayList<>();
        if (services.getEntertainment() != null) {
            for (EntertainmentSummary ent : services.getEntertainment()) {
                entertainmentList.add(Payment.EntertainmentInfo.builder()
                        .id(ent.getId())
                        .name(ent.getName())
                        .hourlyRate(ent.getTotalCost() / ent.getHours()) // Calcular hourlyRate desde totalCost/hours
                        .hours(ent.getHours())
                        .totalCost(ent.getTotalCost())
                        .build());
            }
        }

        // Mapear decoración
        Payment.Decoration decoration = null;
        if (services.getDecoration() != null) {
            decoration = Payment.Decoration.builder()
                    .id(services.getDecoration().getId())
                    .articles(services.getDecoration().getArticles())
                    .cost(services.getDecoration().getCost())
                    .build();
        }

        // Mapear catering
        List<Payment.CateringInfo> cateringList = new ArrayList<>();
        if (services.getCatering() != null) {
            for (CateringSummary cat : services.getCatering()) {
                cateringList.add(Payment.CateringInfo.builder()
                        .id(cat.getId())
                        .description(cat.getDescription())
                        .numberDish(cat.getNumberDish())
                        .costDish(cat.getTotalCost() / cat.getNumberDish()) // Calcular costDish desde totalCost/numberDish
                        .totalCost(cat.getTotalCost())
                        .build());
            }
        }

        // Mapear servicios adicionales
        List<Payment.additionalInfo> additionalList = new ArrayList<>();
        if (services.getAdditionalServices() != null) {
            for (AdittionalSummary add : services.getAdditionalServices()) {
                additionalList.add(Payment.additionalInfo.builder()
                        .id(add.getId())
                        .name(add.getName())
                        .cost(add.getTotalCost() / add.getQuantity()) // Calcular cost unitario
                        .build());
            }
        }

        return Payment.CoveredServices.builder()
                .entertainment(entertainmentList)
                .decoration(decoration)
                .catering(cateringList)
                .additionalServices(additionalList)
                .build();
    }
}
