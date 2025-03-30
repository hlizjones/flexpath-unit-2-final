package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Order;
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
 * Data access object for orders.
 */
@Component
public class OrderDao {
    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates a new order data access object
     *
     * @param dataSource The data source for the DAO.
     */
    public OrderDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all orders.
     *
     * @return List of all orders.
     */
    public List<Order> getOrders() {
        return jdbcTemplate.query("SELECT * FROM orders", this::mapToOrder);
    }

    /**
     * Gets orders by username.
     *
     * @return List of orders with the given username.
     */
    public List<Order> getOrdersByUsername(String username) {
        return jdbcTemplate.query("SELECT * FROM orders WHERE username =?", this::mapToOrder, username);
    }

    /**
     * Gets an order by id.
     *
     * @param id The id of the order.
     * @return The order with the given id.
     */
    public Order getOrderById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM orders WHERE id = ?", this::mapToOrder, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new order.
     *
     * @param order The order to create.
     * @return The order created.
     */
    public Order createOrder(Order order) {
        try {
            PreparedStatementCreator psc = new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement("INSERT INTO orders (username) VALUES (?)", new String[]{"id"});
                    ps.setString(1, order.getUsername());
                    return ps;
                }
            };

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(psc, keyHolder);
            Number key = keyHolder.getKey();
            return getOrderById(key.intValue());
        } catch (EmptyResultDataAccessException e) {
            throw new DaoException("Failed to create order.");
        }
    }

    /**
     * Updates an order.
     *
     * @param order The order to update.
     * @return The updated order.
     */
    public Order updateOrder(Order order) {
      int rowsAffected = jdbcTemplate.update("UPDATE orders SET username = ? WHERE id = ?", order.getUsername(), order.getId());
        if (rowsAffected == 0)
            throw new DaoException("Zero rows affected, expected at least one.");
        return getOrderById(order.getId());
    }

    /**
     * Deletes a user.
     *
     * @param id The id of the user.
     * @return The number of rows affected (1 if an order was deleted, 0 if no order was found).
     */
    public int deleteOrder(int id) {
        return jdbcTemplate.update("DELETE FROM orders WHERE id = ?", id);
    }

    /**
     * Maps a row in the ResultSet to an Order object.
     *
     * @param rs The result set to map.
     * @param rowNum The row number.
     * @return The order object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private Order mapToOrder(ResultSet rs, int rowNum) throws SQLException {
        return new Order(
                rs.getInt("id"),
                rs.getString("username")
        );
    }
}
