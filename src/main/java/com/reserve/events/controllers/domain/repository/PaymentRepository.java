package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, String> {
}
