package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Reserve;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReserveRepository extends MongoRepository  <Reserve, String> {
}
