package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Product;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data access object for products.
 */
@Component
public class ProductDao {
    /**
     * The JDBC template for querying the database.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates a new product data access object
     *
     * @param dataSource The data source for the DAO.
     */
    public ProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * Gets all products.
     *
     * @return List of all products.
     */
    public List<Product> getProducts() {
        return jdbcTemplate.query("SELECT * FROM products", this::mapToProduct);
    }

    /**
     * Gets a product by id.
     *
     * @param id The id of the product.
     * @return The product with the given id.
     */
    public Product getProductById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM products WHERE id = ?", this::mapToProduct, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * Creates a new product.
     *
     * @param product The product to create.
     * @return The product created.
     */
    public Product createProduct(Product product) {
        try {
            PreparedStatementCreator psc = con -> {
                PreparedStatement ps = con.prepareStatement("INSERT INTO products (name, price) VALUES (?,?)", new String[]{"id"});
                ps.setString(1, product.getName());
                ps.setString(2, product.getPrice().toString());
                return ps;
            };

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(psc, keyHolder);
            Number key = keyHolder.getKey();
            return getProductById(key.intValue());
        } catch (EmptyResultDataAccessException e) {
            throw new DaoException("Failed to create product.");
        }
    }

    /**
     * Updates a product.
     *
     * @param product The product to update.
     * @return The updated product.
     */
    public Product updateProduct(Product product) {
        int rowsAffected = jdbcTemplate.update("UPDATE products SET name = ?, price = ? WHERE id = ?", product.getName(), product.getPrice(), product.getId());
        if (rowsAffected == 0)
            throw new DaoException("Zero rows affected, expected at least one.");
        return getProductById(product.getId());
    }

    /**
     * Deletes a product.
     *
     * @param id The id of the product.
     * @return The number of rows affected (1 if a product was deleted, 0 if no product was found).
     */
    public int deleteProduct(int id) {
        return jdbcTemplate.update("DELETE FROM products WHERE id = ?", id);
    }

    /**
     * Maps a row in the ResultSet to a Product object.
     *
     * @param rs The result set to map.
     * @param rowNum The row number.
     * @return The product object.
     * @throws SQLException If an error occurs while mapping the result set.
     */
    private Product mapToProduct(ResultSet rs, int rowNum) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getBigDecimal("price")
        );
    }
}

