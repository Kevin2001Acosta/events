package com.reserve.events.controllers;

import com.reserve.events.application.ReserveService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/reserve")
@Tag(name = "Controlador para las reservas")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;
}
