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

    // TO DO: Verificar que los invitados no excedan el cupo max del establecimiento
    // TO DO: Agregar los errores que no están al global exception (curso)
    // TO DO: Hacer el get de los servicios y la reserva
    // TO DO: Revisar que las fechas dadas en la lista de Dates sean del presente o del futuro, que no se puedan fechas pasadas (check)
    // TO DO: Revisar colección payments

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

        // Guardar la reserva en usuario y establecimiento
        updatedUserWithNewReserve(savedReserve, user.getId());
        updatedEstablishmentWithNewReserve(savedReserve, request.getEstablishmentId(), userSummary);

        // Crear pago FALTA

        //Covertir a response y retornar
        return mapToReserveResponse(savedReserve);
    }

    private void updatedUserWithNewReserve(Reserve reserve, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException("No existe el usuario con id:" + userId));

        ReserveSummary summaryReserve = createReserveSummary(reserve);

        user.getEventBookings().add(summaryReserve);
        userRepository.save(user);
        log.info("Usuario '{}' actualizado con la reserva '{}'", user.getFullName(), summaryReserve.getId());
    }

    private void updatedEstablishmentWithNewReserve(Reserve reserve, String establishmentId, UserSummary user) {
        Establishment establishment = establishmentRepository.findById(establishmentId)
                .orElseThrow(() -> new EstablishmentNotFoundException("No existe el establishment con id:" + establishmentId));

        Establishment.ReserveSummary summaryReserve = createReserveSummaryForEstablishment(reserve, user);

        establishment.getBookings().add(summaryReserve);
        establishmentRepository.save(establishment);
        log.info("Establecimiento '{}' actualizado con la reserva '{}'", establishment.getName(), summaryReserve.getId());
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

    @Transactional
    public Reserve cancelarReserva(UserDetails userDetails, String id) {
        Reserve reserva = reserveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

        // traer al usuario logueado
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + userDetails.getUsername()));

        // verifica que si el usuario es cliente, sea el dueño de la reserva a cancelar
        if(!user.getType().equals(UserType.CLIENTE) || !reserva.getClient().getId().equals(user.getId())) {
            throw new RuntimeException("Como CLIENTE no puedes cancelar una reserva que no es tuya.");
        }

        // Verificar que no esté ya cancelada o completada
        if (reserva.getStatus() == StatusReserve.CANCELADA) {
            throw new RuntimeException("La reserva ya está cancelada.");
        }
        if (reserva.getStatus() == StatusReserve.COMPLETADA) {
            throw new RuntimeException("No se puede cancelar una reserva completada.");
        }

        // Actualizar estado a CANCELADA
        reserva.setStatus(StatusReserve.CANCELADA);
        // eliminar Pago asociado a la reserva
        paymentRepository.deletePaymentByReserve_Id(reserva.getId());

        // Actualizar el estado de la reserva en las reservas del usuario
        updateReserveToCanceledInUser(reserva.getId(), user);

        // Buscar el establecimiento y actualizar su reserva
        updateReserveToCanceledInEstablishment(reserva.getEstablishment().getId(), reserva.getId());



        return reserveRepository.save(reserva);
    }

    private void updateReserveToCanceledInUser(String reservaId, User user) {
        // Actualizar el estado de la reserva en las reservas del usuario
        user.getEventBookings().stream()
                .filter(booking -> booking.getId().equals(reservaId)) // Buscar la reserva por ID
                .findFirst()
                .ifPresent(booking -> booking.setStatus(StatusReserve.CANCELADA)); // Actualizar el estado

        // Guardar los cambios en el usuario
        userRepository.save(user);
    }

    private void updateReserveToCanceledInEstablishment(String establishmentId, String reservaId){
        establishmentRepository.findById(establishmentId)
                .ifPresent(establishment ->{
                    establishment.getBookings().stream().filter(booking -> booking.getId().equals(reservaId))
                            .findFirst()
                            .ifPresent(booking -> booking.setStatus(StatusReserve.CANCELADA));
                    // Guarda los cambios en el establecimiento
                    establishmentRepository.save(establishment);
                });
    }

    private ReserveResponse mapToReserveResponse(Reserve reserve) {
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
}
