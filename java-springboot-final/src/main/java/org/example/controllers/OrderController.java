package org.example.controllers;

import org.example.daos.OrderDao;
import org.example.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * Controller for orders.
 * This class is responsible for handling all HTTP requests related to orders.
 */
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/orders")
public class OrderController {
    /**
     * The order data access object.
     */
    @Autowired
    private OrderDao orderDao;

    /**
     * Gets all orders.
     *
     * @return A list of all orders.
     */
    @GetMapping
    public List<Order> getListOfOrders(@RequestParam(required = false) String username) {
        if (username != null) {
            return orderDao.getOrdersByUsername(username);
        } else {
            return orderDao.getOrders();
        }
    }

    /**
     * Gets an order by id.
     *
     * @param id The id of the order.
     * @return The order with the given id.
     */
    @GetMapping(path = "/{id}")
    public Order get(@PathVariable int id) {
        Order order = orderDao.getOrderById(id);
        if (order == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        return order;
    }

    /**
     * Creates a new order.
     *
     * @param order The order to create.
     * @return The order created.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Order create(@RequestBody Order order, Principal principal) {
        String username = principal.getName();
        order.setUsername(username);
        return orderDao.createOrder(order);
    }

    /**
     * Updates an order.
     *
     * @param order The new order.
     * @param id The id of the order.
     * @return The updated order.
     */
    @PutMapping(path = "/{id}")
    public Order update(@RequestBody Order order, @PathVariable int id) {
        if (orderDao.getOrderById(id) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        order.setId(id);
        return orderDao.updateOrder(order);
    }

    /**
     * Deletes an order.
     *
     * @param id The id of the order.
     * @return The number of rows affected (1 if an order was deleted, 0 if no order was found).
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        if (orderDao.getOrderById(id) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        return orderDao.deleteOrder(id);
    }
}
