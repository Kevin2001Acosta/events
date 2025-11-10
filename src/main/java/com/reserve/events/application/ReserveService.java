package com.reserve.events.application;

import com.reserve.events.controllers.domain.repository.ReserveRepository;
import com.reserve.events.controllers.domain.repository.UserRepository;
import com.reserve.events.controllers.dto.ReserveRequest;
import com.reserve.events.controllers.response.ReserveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;

    //@Transactional
    //public ReserveResponse createReserve(ReserveRequest request){}
}
