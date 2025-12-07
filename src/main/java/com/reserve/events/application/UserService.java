package com.reserve.events.application;


import com.reserve.events.controllers.domain.entity.Event;
import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.domain.entity.User;
import com.reserve.events.controllers.domain.model.EventSummary;
import com.reserve.events.controllers.domain.model.ReserveSummary;
import com.reserve.events.controllers.domain.repository.UserRepository;
import com.reserve.events.controllers.dto.LoginRequest;
import com.reserve.events.controllers.dto.UserRequest;
import com.reserve.events.controllers.exception.UserAlreadyExistsException;
import com.reserve.events.controllers.exception.UserNotFoundException;
import com.reserve.events.controllers.response.UserCreatedResponse;
import com.reserve.events.controllers.response.UserLoginResponse;
import com.reserve.events.controllers.response.UserResponse;
import com.reserve.events.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserCreatedResponse createUser(UserRequest request){
        // verificar si existe un usuario con el mismo Email
        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyExistsException("Ya existe un usuario con el correo electrónico: " + request.getEmail());
        }

        // TODO: Hashear la contraseña antes de guardarla
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        // Mapea UserRequest a la entidad User
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .city(request.getCity())
                .type(request.getType())
                .Password(hashedPassword)
                .build();
        // Guarda el usuario en la base de datos
        User savedUser = userRepository.save(user);

        // Mapea la entidad User a UserCreatedResponse
        return UserCreatedResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .city(savedUser.getCity())
                .type(savedUser.getType())
                .build();

    }

    public UserLoginResponse loginUser(LoginRequest request){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 2 Si la autenticación es exitosa, establecerla en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 3 Generar el token JWT
        String token = jwtTokenProvider.generateToken(authentication);

        // 4. Obtener el email del usuario autenticado
        String authenticatedEmail = authentication.getName();

        // 5 Buscar el usuario en la base de datos
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado después de la autenticación"));

        return UserLoginResponse.builder()
                .token(token)
                .user(
                        UserLoginResponse.UserInfo.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .fullName(user.getFullName())
                                .type(user.getType())
                                .build()
                )
                .build();

    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(String id){
        return userRepository.findById(id)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsersByType(String type){
        return userRepository.findByType(type)
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse mapToUserResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .city(user.getCity())
                .type(user.getType())
                .payments(user.getPayments() != null ? user.getPayments() : Collections.emptyList())
                .scheduledBookings(
                        user.getScheduledBookings() != null ?
                                user.getScheduledBookings().stream()
                                        .map(this::mapToReserveSummaryResponse)
                                        .collect(Collectors.toList())
                                : Collections.emptyList()
                )
                .completedBookings(
                        user.getCompletedBookings() != null ?
                                user.getCompletedBookings().stream()
                                        .map(this::mapToReserveSummaryResponse)
                                        .collect(Collectors.toList())
                                : Collections.emptyList()
                )
                .cancelledBookings(
                        user.getCancelledBookings() != null ?
                                user.getCancelledBookings().stream()
                                        .map(this::mapToReserveSummaryResponse)
                                        .collect(Collectors.toList())
                                : Collections.emptyList()
                )
                .build();
    }

    public UserResponse.ReserveSummaryResponse mapToReserveSummaryResponse(ReserveSummary reserve){
        return UserResponse.ReserveSummaryResponse.builder()
                .id(reserve.getId())
                .status(reserve.getStatus())
                .event(reserve.getEvent())
                .establishment(reserve.getEstablishment())
                .dates(reserve.getDates())
                .services(reserve.getServices())
                .build();
    }
}
