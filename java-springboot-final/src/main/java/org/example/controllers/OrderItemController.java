package org.example.controllers;


import org.example.daos.OrderItemDao;
import org.example.models.OrderItem;
import org.example.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for order items.
 * This class is responsible for handling all HTTP requests related to order items.
 */
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/order-items")
public class OrderItemController {
    /**
     * The order item data access object.
     */
    @Autowired
    private OrderItemDao orderItemDao;

    /**
     * Gets all order items.
     *
     * @return A list of all order items.
     */
    @GetMapping
    public List<OrderItem> getListOfOrderItems(@RequestParam(required = false) Long orderId) {
        if (orderId != null) {
            return orderItemDao.getOrderItemsByOrderId(orderId);
        } else {
            return orderItemDao.getOrderItems();
        }
    }

    /**
     * Gets an order item by id.
     *
     * @param id The id of the order item.
     * @return The order item with the given id.
     */
    @GetMapping(path = "{id}")
    public OrderItem get(@PathVariable int id) {
        OrderItem orderItem = orderItemDao.getOrderItemById(id);
        if (orderItem == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found");
        return orderItem;
    }

    /**
     * Creates a new order item.
     *
     * @param orderItem The order item to create.
     * @return The order item created.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderItem create(@RequestBody OrderItem orderItem) {
        return orderItemDao.createOrderItem(orderItem);
    }

    /**
     * Updates an order item.
     *
     * @param orderItem The new order item.
     * @param id The id of the order item.
     * @return The updated order item.
     */
    @PutMapping(path = "/{id}")
    public OrderItem update(@RequestBody OrderItem orderItem, @PathVariable int id) {
        if (orderItemDao.getOrderItemById(id) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order item not found");
        orderItem.setId(id);
        return orderItemDao.updateOrderItem(orderItem);
    }

    /**
     * Deletes an order item.
     *
     * @param id The id of the order item.
     * @return The number of rows affected (1 if an order item was deleted, 0 if no order item was found).
     */
    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable int id) {
        if(orderItemDao.getOrderItemById(id) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        return orderItemDao.deleteOrderItem(id);
    }
}
