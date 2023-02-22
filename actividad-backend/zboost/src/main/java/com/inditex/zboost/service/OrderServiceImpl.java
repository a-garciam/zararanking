package com.inditex.zboost.service;

import com.inditex.zboost.entity.Order;
import com.inditex.zboost.entity.OrderDetail;
import com.inditex.zboost.entity.ProductOrderItem;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public OrderServiceImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Order> findOrders(int limit) {
        /**
         * TODO: EJERCICIO 2.a) Recupera un listado de los ultimos N pedidos (recuerda ordenar por fecha)
         */

        Map<String, Object> params = new HashMap<>();
        params.put("limit", limit);

        String sql = "SELECT * FROM ORDERS ORDER BY DATE DESC LIMIT :limit";

        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Order.class));
    }

    @Override
    public List<Order> findOrdersBetweenDates(Date fromDate, Date toDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", new java.sql.Date(fromDate.getTime()));
        params.put("toDate", new java.sql.Date(toDate.getTime()));
        String sql = """
                SELECT id, date, status
                FROM Orders 
                WHERE date BETWEEN :startDate AND :toDate
                """;

        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Order.class));
    }

    @Override
    public OrderDetail findOrderDetail(long orderId) {
        /**
         * TODO: EJERCICIO 2.b) Recupera los detalles de un pedido dado su ID
         *
         * Recuerda que, si un pedido no es encontrado por su ID, debes notificarlo debidamente como se recoge en el contrato
         * que estas implementando (codigo de estado HTTP 404 Not Found). Para ello puedes usar la excepcion {@link com.inditex.zboost.exception.NotFoundException}
         *
         */

        // Escribe la query para recuperar la entidad OrderDetail por ID
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        OrderDetail orderDetail = null;
        String productOrdersSql = "SELECT *, PRICE*QUANTITY as total_product, FROM ORDER_ITEMS ord JOIN PRODUCTS p WHERE ord.ORDER_ID = :orderId AND ord.PRODUCT_ID = p.ID GROUP BY PRODUCT_ID";
        List<String> orderList = jdbcTemplate.queryForList(productOrdersSql, (SqlParameterSource) params, String.class);

        // Una vez has conseguido recuperar los detalles del pedido, faltaria recuperar los productos que forman parte de el...
        String selected = "SELECT * FROM ORDER_ITEMS WHERE ORDER_ID = :orderId";
        List<ProductOrderItem> products = jdbcTemplate.query(selected, params, new BeanPropertyRowMapper<>(ProductOrderItem.class));

        orderDetail.setProducts(products);
        return orderDetail;
    }
}
