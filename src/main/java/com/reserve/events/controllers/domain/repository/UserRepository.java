package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Verifica si existe un usuario con el correo especificado.
     * @param email Número de tarjeta a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);


    /**
     * Busca un usuario por su cardNum
     * @param email Número de tarjeta del usuario a buscar
     * @return Optional con el usuario si se encuentra
     */
    Optional<User> findByEmail(String email);
}
