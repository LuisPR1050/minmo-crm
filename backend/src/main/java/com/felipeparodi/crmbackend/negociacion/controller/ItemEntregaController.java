package com.felipeparodi.crmbackend.negociacion.controller;

import com.felipeparodi.crmbackend.negociacion.dto.ItemEntregaResponse;
import com.felipeparodi.crmbackend.negociacion.service.ItemEntregaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items-entrega")
public class ItemEntregaController {

    private final ItemEntregaService itemEntregaService;

    public ItemEntregaController(ItemEntregaService itemEntregaService) {
        this.itemEntregaService = itemEntregaService;
    }

    @GetMapping
    public List<ItemEntregaResponse> listarTodos() {
        return itemEntregaService.listarTodos();
    }
}