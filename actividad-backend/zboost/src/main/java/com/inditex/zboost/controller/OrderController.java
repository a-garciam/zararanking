package com.inditex.zboost.controller;

import com.inditex.zboost.entity.Order;
import com.inditex.zboost.entity.OrderDetail;
import com.inditex.zboost.exception.InvalidParameterException;
import com.inditex.zboost.service.OrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.spec.InvalidParameterSpecException;
import java.util.List;

@RestController
@Validated
public class OrderController {

    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public List<Order> getOrders(@RequestParam(name = "limit", required = true) int limit) {
        /**
         * : EJERCICIO 2.a) Parametro limit
         *
         * Como buena practica, es recomendable realizar las validaciones de los parametros de entrada de las peticiones 
         * en la capa externa de la arquitectura, por lo que este seria un buen lugar para validar lo que se comenta en
         * el enunciado del ejercicio ;)
         *
         * PISTA: Si el limit no es valido, puedes usar la excepcion InvalidParameterException para notificarlo.
         *
         */
        if(limit<1 || limit>100) throw new InvalidParameterException("Limit ", "not valid.");

        return orderService.findOrders(limit);
    }

    @GetMapping("/orders/{orderId}")
    public OrderDetail getProductDetail(@PathVariable(name = "orderId", required = true) long orderId) {
        return orderService.findOrderDetail(orderId);
    }
}
