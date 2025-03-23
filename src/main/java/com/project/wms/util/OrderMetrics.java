package com.project.wms.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderMetrics {

    private double totalSum; // Общая сумма
    private long uniqueClientCount; // Количество уникальных клиентов
    private long orderCount; // Количество заказов
    private double averageCheck; // Средний чек

}
