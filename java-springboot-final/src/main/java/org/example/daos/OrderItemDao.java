package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.OrderItem;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data access object for order items.
 */
@Component
public class OrderItemDao {

    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates a new order items data access object
     *
     * @param dataSource The data source for the DAO.
     */
    public OrderItemDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all order items.
     *
     * @return List of all order items.
     */
    public List<OrderItem> getOrderItems() {
        return jdbcTemplate.query("SELECT * FROM order_items", this::mapToOrderItem);
    }

    /**
     * Gets order items by order id.
     *
     * @return List of order items with the given order id.
     */
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return jdbcTemplate.query("SELECT * FROM order_items WHERE order_id =?", this::mapToOrderItem, orderId);
    }

    /**
     * Gets an order item by id.
     *
     * @param id The id of the order item.
     * @return The order item with the given id.
     */
    public OrderItem getOrderItemById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM order_items WHERE id = ?", this::mapToOrderItem, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new order item.
     *
     * @param orderItem The order item to create.
     * @return The order items created.
     */
    public OrderItem createOrderItem(OrderItem orderItem) {
        try {
            PreparedStatementCreator psc = con -> {
                PreparedStatement ps = con.prepareStatement("INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)", new String[]{"id"});
                ps.setString(1, String.valueOf(orderItem.getOrderId()));
                ps.setString(2, String.valueOf(orderItem.getProductId()));
                ps.setString(3, String.valueOf(orderItem.getQuantity()));
                return ps;
            };

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(psc, keyHolder);
            Number key = keyHolder.getKey();
            return getOrderItemById(key.intValue());
        } catch (EmptyResultDataAccessException e) {
            throw new DaoException("Failed to create order item.");
        }
    }

    /**
     * Updates an order item.
     *
     * @param orderItem The order item to update.
     * @return The updated order item.
     */
    public OrderItem updateOrderItem(OrderItem orderItem) {
        int rowsAffected = jdbcTemplate.update("UPDATE order_items SET order_id = ?, product_id = ?, quantity =? WHERE id =?", orderItem.getOrderId(), orderItem.getProductId(), orderItem.getQuantity(), orderItem.getId());
        if (rowsAffected == 0)
            throw new DaoException("Zero rows affected, expected at least one.");
        return getOrderItemById(orderItem.getId());
    }

    /**
     * Deletes an order item.
     *
     * @param id The id of the order item.
     * @return The number of rows affected (1 if a product was deleted, 0 if no order was found).
     */
    public int deleteOrderItem(int id) {
        return jdbcTemplate.update("DELETE FROM products WHERE id = ?", id);
    }

    private OrderItem mapToOrderItem(ResultSet rs, int rowNum) throws SQLException {
        return new OrderItem(
                rs.getInt("id"),
                rs.getInt("order_id"),
                rs.getInt("product_id"),
                rs.getInt("quantity")
        );
    }
}
