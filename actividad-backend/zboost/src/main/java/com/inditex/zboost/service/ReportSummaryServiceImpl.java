package com.inditex.zboost.service;

import com.inditex.zboost.entity.Order;
import com.inditex.zboost.entity.OrderDetail;
import com.inditex.zboost.entity.ReportSummary;
import com.inditex.zboost.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ReportSummaryServiceImpl implements ReportSummaryService {

    @Autowired
    OrderServiceImpl orderService;

    private NamedParameterJdbcTemplate jdbcTemplate;
    private ProductService productService;

    public ReportSummaryServiceImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ReportSummary reportSummary() {
        /**
         * TODO: EJERCICIO 3. Reporte sumarizado
         */

        // Total de productos existentes en el catálogo
        String totalProductsSql = "SELECT COUNT(*) as PRODUCTOS FROM PRODUCTS";
        int totalProducts = jdbcTemplate.queryForObject(totalProductsSql, Map.of(), Integer.class);

        // Total de pedidos realizados
        String totalOrdersSql = "SELECT COUNT(*) as PEDIDOS FROM ORDERS";
        int totalOrders = jdbcTemplate.queryForObject(totalOrdersSql, Map.of(), Integer.class);

        // Importe total acumulado de todas las ventas
        String ordersSql = "SELECT * FROM ORDERS";
        List<Order> orders = jdbcTemplate.query(ordersSql, Map.of(), new BeanPropertyRowMapper<>(Order.class));

        if(orders.size()==0)
            throw new NotFoundException("orderId", "No orders in the system.");

        double totalSales = 0;
        for (Order order : orders) {
            totalSales += orderService.findOrderDetail(order.getId()).getTotalPrice();
        }

        // Total de productos existentes desgranados por categoría
        String totalProductsByCategorySql = "SELECT category, COUNT(*) as total_products FROM PRODUCTS GROUP BY category";
        Map<String, Integer> totalProductsByCategory = new HashMap<>();
        jdbcTemplate.query(totalProductsByCategorySql, rs -> {
            String category = rs.getString("category");
            int count = rs.getInt("total_products");
            totalProductsByCategory.put(category, count);
        });

        ReportSummary reportSummary = new ReportSummary();
        reportSummary.setTotalProducts(totalProducts);
        reportSummary.setTotalOrders(totalOrders);
        reportSummary.setTotalSales(totalSales);
        reportSummary.setTotalProductsByCategory(totalProductsByCategory);

        return reportSummary;
    }
}
