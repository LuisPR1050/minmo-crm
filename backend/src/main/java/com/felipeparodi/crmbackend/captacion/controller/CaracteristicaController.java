package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.captacion.dto.CaracteristicaResponse;
import com.felipeparodi.crmbackend.captacion.service.CaracteristicaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/caracteristicas")
public class CaracteristicaController {

    private final CaracteristicaService caracteristicaService;

    public CaracteristicaController(CaracteristicaService caracteristicaService) {
        this.caracteristicaService = caracteristicaService;
    }

    @GetMapping
    public List<CaracteristicaResponse> listar(@RequestParam(required = false) String categoria) {
        if (categoria != null) {
            return caracteristicaService.listarPorCategoria(categoria);
        }
        return caracteristicaService.listarTodas();
    }
}