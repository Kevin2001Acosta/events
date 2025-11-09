package com.reserve.events.aplication;


import com.reserve.events.controllers.domain.entity.User;
import com.reserve.events.controllers.domain.repository.UserRepository;
import com.reserve.events.controllers.dto.UserRequest;
import com.reserve.events.controllers.exception.UserAlreadyExistsException;
import com.reserve.events.controllers.response.UserCreatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

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

}
