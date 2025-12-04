package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Event;
import com.reserve.events.controllers.domain.entity.Establishment;
import com.reserve.events.controllers.domain.entity.User;
import com.reserve.events.controllers.domain.repository.EventRepository;
import com.reserve.events.controllers.domain.repository.EstablishmentRepository;
import com.reserve.events.controllers.domain.repository.UserRepository;
import com.reserve.events.controllers.domain.repository.ReserveRepository;
import com.reserve.events.controllers.domain.repository.PaymentRepository;
import com.reserve.events.controllers.dto.ReserveRequest;
import com.reserve.events.controllers.exception.AvailableEstablishmentNotFoundException;
import com.reserve.events.controllers.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReserveServiceTest {

    @Mock
    private ReserveRepository reserveRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private EstablishmentRepository establishmentRepository;

    @Mock
    private EstablishmentService establishmentService;

    @InjectMocks
    private ReserveService reserveService;

    @Test
    void createReserve_whenUserNotFound_throwsUserNotFound() {
        ReserveRequest request = ReserveRequest.builder().eventId("evt").establishmentId("est").build();

        when(userRepository.findByEmail("no-existe@x.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> reserveService.createReserve(request, "no-existe@x.com"));
    }

    @Test
    void createReserve_whenDatesNotAvailable_throwsAvailableEstablishmentNotFound() {
        ReserveRequest request = ReserveRequest.builder()
                .eventId("evt")
                .establishmentId("est")
                .dates(java.util.List.of(java.time.LocalDate.now().plusDays(1)))
                .guestNumber(10)
                .build();

        User user = User.builder().id("u1").email("u@u.com").fullName("User").build();
        when(userRepository.findByEmail("u@u.com")).thenReturn(Optional.of(user));

        when(eventRepository.findById("evt")).thenReturn(Optional.of(Event.builder().id("evt").type("T").build()));
        when(establishmentRepository.findById("est")).thenReturn(Optional.of(Establishment.builder().id("est").build()));

        when(establishmentService.areDatesAvailableForEstablishment(request.getDates(), "est")).thenReturn(false);

        assertThrows(AvailableEstablishmentNotFoundException.class, () -> reserveService.createReserve(request, "u@u.com"));
    }
}
