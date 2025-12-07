package com.reserve.events.application;


import com.reserve.events.controllers.domain.entity.User;
import com.reserve.events.controllers.domain.repository.UserRepository;
import com.reserve.events.controllers.dto.LoginRequest;
import com.reserve.events.controllers.dto.UserRequest;
import com.reserve.events.controllers.exception.UserAlreadyExistsException;
import com.reserve.events.controllers.exception.UserNotFoundException;
import com.reserve.events.controllers.response.UserCreatedResponse;
import com.reserve.events.controllers.response.UserLoginResponse;
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
            throw new UserAlreadyExistsException("Esta dirección de correo electrónico ya está registrada");
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

}
