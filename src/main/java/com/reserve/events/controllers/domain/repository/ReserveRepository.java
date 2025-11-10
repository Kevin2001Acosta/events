package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.domain.model.StatusReserve;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ReserveRepository extends MongoRepository  <Reserve, String> {
    @Query("{'event.id': ?0, 'status': ?1}")
    long countByEventIdAndStatus(String eventId, StatusReserve status);
}
