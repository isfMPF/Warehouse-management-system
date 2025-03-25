package com.project.wms.util;

import com.project.wms.dto.responsedto.OrderItemResponseDto;
import com.project.wms.dto.responsedto.OrderResponseDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelExporter {

    public static byte[] exportOrdersToExcel(List<OrderResponseDto> orders) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Лист с детализацией заказов
            Sheet ordersSheet = workbook.createSheet("Детали заказов");
            createOrdersSheet(ordersSheet, orders, workbook);

            // Лист со сводкой по товарам
            Sheet summarySheet = workbook.createSheet("Сводка по товарам");
            createProductSummarySheet(summarySheet, orders, workbook);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static void createOrdersSheet(Sheet sheet, List<OrderResponseDto> orders, Workbook workbook) {
        // Стили для оформления
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle orderHeaderStyle = createOrderHeaderStyle(workbook);
        CellStyle itemStyle = createItemStyle(workbook);
        CellStyle totalStyle = createTotalStyle(workbook);
        int rowNum = 0;

        for (OrderResponseDto order : orders) {
            // Строка с информацией о заказе
            Row orderRow = sheet.createRow(rowNum++);
            createOrderHeaderRow(orderRow, order, orderHeaderStyle);

            // Заголовки таблицы товаров
            Row itemHeaderRow = sheet.createRow(rowNum++);
            createItemHeaderRow(itemHeaderRow, headerStyle);

            double orderTotal = 0;
            int totalItems = 0;

            // Строки с товарами
            if (order.getItem() != null && !order.getItem().isEmpty()) {
                for (OrderItemResponseDto item : order.getItem()) {
                    Row itemRow = sheet.createRow(rowNum++);
                    createItemRow(itemRow, item, itemStyle);
                    orderTotal += item.getTotal();
                    totalItems += item.getAmount();
                }

                // Добавляем строку с итогами по заказу
                Row totalRow = sheet.createRow(rowNum++);
                createTotalRow(totalRow, totalItems, orderTotal, totalStyle, sheet);
            } else {
                Row emptyRow = sheet.createRow(rowNum++);
                Cell cell = emptyRow.createCell(0);
                cell.setCellValue("Нет товаров");
                cell.setCellStyle(itemStyle);
                sheet.addMergedRegion(new CellRangeAddress(
                        emptyRow.getRowNum(), emptyRow.getRowNum(), 0, 5
                ));
            }

            // Пустые строки для удобства печати (5 строки)
            for (int i = 0; i < 4; i++) {
                sheet.createRow(rowNum++);
            }
        }

        // Авто-размер колонок
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    private static void createProductSummarySheet(Sheet sheet, List<OrderResponseDto> orders, Workbook workbook) {
        // Собираем статистику по товарам
        Map<String, ProductSummary> productSummary = new HashMap<>();

        for (OrderResponseDto order : orders) {
            if (order.getItem() != null) {
                for (OrderItemResponseDto item : order.getItem()) {
                    String code = item.getCode().toString();
                    productSummary.merge(
                            code,
                            new ProductSummary(item.getName(), item.getVolume(), item.getAmount()),
                            (existing, newItem) -> {
                                existing.addAmount(newItem.getTotalAmount());
                                return existing;
                            }
                    );
                    // Здесь можно добавить расчет веса, когда он будет в базе
                    // totalWeight += item.getWeight();
                }
            }
        }

        // Создаем стили
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createItemStyle(workbook);
        CellStyle totalStyle = createTotalStyle(workbook);

        // Заголовки таблицы
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Код товара", "Наименование", "Объем", "Общее количество"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Заполняем данные
        int rowNum = 1;
        for (Map.Entry<String, ProductSummary> entry : productSummary.entrySet()) {
            Row row = sheet.createRow(rowNum++);

            // Явно создаем все ячейки перед установкой стиля
            Cell codeCell = row.createCell(0);
            codeCell.setCellValue(entry.getKey());
            codeCell.setCellStyle(dataStyle);

            Cell nameCell = row.createCell(1);
            nameCell.setCellValue(entry.getValue().getName());
            nameCell.setCellStyle(dataStyle);

            Cell volumeCell = row.createCell(2);
            volumeCell.setCellValue(entry.getValue().getVolume() + " л");
            volumeCell.setCellStyle(dataStyle);

            Cell amountCell = row.createCell(3);
            amountCell.setCellValue(entry.getValue().getTotalAmount());
            amountCell.setCellStyle(dataStyle);
        }

        // Добавляем строку с общим весом
        Row weightRow = sheet.createRow(rowNum);
        Cell weightLabelCell = weightRow.createCell(0);
        weightLabelCell.setCellValue("Общий вес:");
        weightLabelCell.setCellStyle(totalStyle);

        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 2));

        Cell weightValueCell = weightRow.createCell(3);
        weightValueCell.setCellValue("0 кг"); // Заменить на реальное значение, когда оно будет доступно
        weightValueCell.setCellStyle(totalStyle);

        // Авто-размер колонок
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }



    // Вспомогательный класс для сводки по товарам
    @Getter
    @Setter
    private static class ProductSummary {
        private final String name;
        private final String volume;
        private int totalAmount;

        public ProductSummary(String name, String volume, int amount) {
            this.name = name;
            this.volume = volume;
            this.totalAmount = amount;
        }

        public void addAmount(int amount) {
            this.totalAmount += amount;
        }

    }


    private static void createOrderHeaderRow(Row row, OrderResponseDto order, CellStyle style) {
        String orderInfo = String.format("Заказ #%d | Клиент: %s (%s) | Дата: %s",
                order.getId(), order.getClientName(), order.getCodeClient(),
                order.getDate());

        Cell cell = row.createCell(0);
        cell.setCellValue(orderInfo);
        cell.setCellStyle(style);

        // Объединение ячеек для заголовка заказа
        row.getSheet().addMergedRegion(new CellRangeAddress(
                row.getRowNum(), row.getRowNum(), 0, 5
        ));
    }

    private static void createItemHeaderRow(Row row, CellStyle style) {
        String[] headers = {"Код", "Наименование", "Объем", "Кол-во", "Цена", "Сумма"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private static void createItemRow(Row row, OrderItemResponseDto item, CellStyle style) {
        // Создаем ячейки и устанавливаем значения
        Cell cell0 = row.createCell(0);
        cell0.setCellValue(item.getCode());
        cell0.setCellStyle(style);

        Cell cell1 = row.createCell(1);
        cell1.setCellValue(item.getName());
        cell1.setCellStyle(style);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue(item.getVolume() + " л");
        cell2.setCellStyle(style);

        Cell cell3 = row.createCell(3);
        cell3.setCellValue(item.getAmount());
        cell3.setCellStyle(style);

        Cell cell4 = row.createCell(4);
        cell4.setCellValue(item.getPrice() + " ₽");
        cell4.setCellStyle(style);

        Cell cell5 = row.createCell(5);
        cell5.setCellValue(item.getTotal() + " ₽");
        cell5.setCellStyle(style);
    }

    private static void createTotalRow(Row row, int totalItems, double orderTotal, CellStyle style, Sheet sheet) {
        // Создаем все ячейки строки
        Cell cell0 = row.createCell(0);
        cell0.setCellValue("Итого:");
        cell0.setCellStyle(style);

        Cell cell1 = row.createCell(1);
        cell1.setCellStyle(style); // Пустая ячейка с границами

        Cell cell2 = row.createCell(2);
        cell2.setCellStyle(style); // Пустая ячейка с границами

        Cell cell3 = row.createCell(3);
        cell3.setCellValue(totalItems);
        cell3.setCellStyle(style);

        Cell cell4 = row.createCell(4);
        cell4.setCellStyle(style); // Пустая ячейка с границами

        Cell cell5 = row.createCell(5);
        cell5.setCellValue("Итог: " + orderTotal + " ₽");
        cell5.setCellStyle(style);

        // Объединяем ячейки для надписи "Итого:"
        sheet.addMergedRegion(new CellRangeAddress(
                row.getRowNum(), row.getRowNum(), 0, 2
        ));
    }


    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createOrderHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    private static CellStyle createItemStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // Устанавливаем границы для всех сторон
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // Границы со всех сторон
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
