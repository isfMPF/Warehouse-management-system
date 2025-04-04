package com.project.wms.util;

import com.project.wms.dto.responsedto.OrderItemResponseDto;
import com.project.wms.dto.responsedto.OrderResponseDto;
import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
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

            // Лист со сводкой по клиентам
            Sheet clientsSheet = workbook.createSheet("Сводка по клиентам");
            createClientsSummarySheet(clientsSheet, orders, workbook);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray()   ;
        }
    }

    private static void createOrdersSheet(Sheet sheet, List<OrderResponseDto> orders, Workbook workbook) {
        // Стили для оформления
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle itemStyle = createItemStyle(workbook);
        CellStyle totalStyle = createTotalStyle(workbook);
        CellStyle clientInfoStyle = createClientInfoStyle(workbook);
        CellStyle documentTitleStyle = createDocumentTitleStyle(workbook);
        CellStyle signatureStyle = createSignatureStyle(workbook);

        // Установка ширины колонок
        sheet.setColumnWidth(0, 15*256); // Код
        sheet.setColumnWidth(1, 25*256); // Наименование
        sheet.setColumnWidth(3, 10*256); // Кол-во
        sheet.setColumnWidth(4, 15*256); // Цена
        sheet.setColumnWidth(5, 15*256); // Сумма

        // Настройки печати
        sheet.setAutobreaks(false);
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        printSetup.setFitHeight((short) 0);  // Отключаем подгонку по высоте
        printSetup.setFitWidth((short) 1);   // Подгоняем только по ширине

        // Параметры страницы для Pantum M6500
        final int MAX_ROWS_PER_PAGE = 48;    // Оптимальное значение для A4
        final int ORDERS_PER_PAGE = 2;       // 2 заказа на страницу
        final int BUFFER_ROWS = 5;           // Буферные строки для надежности

        int rowNum = 0;
        int orderCount = 0;
        int pageStartRow = 0;

        for (OrderResponseDto order : orders) {
            int orderStartRow = rowNum;
            // Шапка документа
            Row headerRow1 = sheet.createRow(rowNum++);
            Cell headerCell1 = headerRow1.createCell(0);
            headerCell1.setCellValue("Тахвилкунанда     ЧДММ \"ПОЧОЕВ И\"");
            headerCell1.setCellStyle(documentTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 4));

            Row headerRow2 = sheet.createRow(rowNum++);
            Cell headerCell2 = headerRow2.createCell(0);
            headerCell2.setCellValue("Анбор раф.    ЧМШ Исфара");
            headerCell2.setCellStyle(documentTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 4));

            // Информация о клиенте
            Row clientRow = sheet.createRow(rowNum++);
            Cell clientCell = clientRow.createCell(0);
            clientCell.setCellValue("Кабулкунанда : " + order.getClientName());
            clientCell.setCellStyle(clientInfoStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 4));

            // Информация о машине
            Row carRow = sheet.createRow(rowNum++);
            Cell carCell = carRow.createCell(0);
            carCell.setCellValue("Бо автомашинаи :  Портер TJ __________");
            carCell.setCellStyle(clientInfoStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 4));

            // Номер заказа и дата
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formattedDate = order.getDate().format(formatter);

            Row orderNumRow = sheet.createRow(rowNum++);
            Cell orderNumCell = orderNumRow.createCell(0);
            orderNumCell.setCellValue("Б О Р Х А Т И   Х А Р О Ч О Т И № " + order.getId() + "   " + formattedDate);
            orderNumCell.setCellStyle(clientInfoStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 4));

            // Пустая строка
            rowNum++;

            // Заголовки таблицы товаров
            Row itemHeaderRow = sheet.createRow(rowNum++);
            createItemHeaderRow(itemHeaderRow, headerStyle);

            double orderTotal = 0;
            int totalItems = 0;
            double orderWeight = 0;

            // Строки с товарами
            if (order.getItem() != null && !order.getItem().isEmpty()) {
                for (OrderItemResponseDto item : order.getItem()) {
                    Row itemRow = sheet.createRow(rowNum++);
                    createItemRow(itemRow, item, itemStyle);
                    orderTotal += item.getTotal();
                    totalItems += item.getAmount();
                    orderWeight += item.getWeight() * item.getAmount();
                }

                // Строка с итогами (вес и сумма)
                Row totalRow = sheet.createRow(rowNum++);
                Cell weightCell = totalRow.createCell(0);
                weightCell.setCellValue(String.format("%.1f кг", orderWeight));
                weightCell.setCellStyle(totalStyle);

                Cell emptyB = totalRow.createCell(1);
                emptyB.setCellStyle(totalStyle);



                // Переносим количество под столбец "Кол-во"
                Cell quantityValueCell = totalRow.createCell(2); // Теперь в столбце D (3)
                quantityValueCell.setCellValue(totalItems);
                quantityValueCell.setCellStyle(totalStyle);

                Cell totalLabelCell = totalRow.createCell(3);
                totalLabelCell.setCellValue("Итого:");
                totalLabelCell.setCellStyle(totalStyle);

                Cell totalValueCell = totalRow.createCell(4);
                totalValueCell.setCellValue(String.format("%.2f смн.", orderTotal));
                totalValueCell.setCellStyle(totalStyle);

                // Контактная информация
                Row contactRow = sheet.createRow(rowNum++);
                Cell contactCell = contactRow.createCell(0);
                contactCell.setCellValue("По всем вопросам звонить: Тел: (+992) 98 818 90 00 Dc Почоев Илхом");
                contactCell.setCellStyle(clientInfoStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 4));

                // Пустая строка
                rowNum++;

                // Подписи
                Row signatureRow = sheet.createRow(rowNum++);
                Cell deliveryCell = signatureRow.createCell(0);
                deliveryCell.setCellValue("Супорид _______________");
                deliveryCell.setCellStyle(signatureStyle);

                Cell receiverCell = signatureRow.createCell(3);
                receiverCell.setCellValue("Кабул кард _____________");
                receiverCell.setCellStyle(signatureStyle);

                // Объединяем ячейки для подписей
                sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 1));
                sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 3, 4));
            } else {
                Row emptyRow = sheet.createRow(rowNum++);
                Cell cell = emptyRow.createCell(0);
                cell.setCellValue("Нет товаров");
                cell.setCellStyle(itemStyle);
                sheet.addMergedRegion(new CellRangeAddress(
                        emptyRow.getRowNum(), emptyRow.getRowNum(), 0, 5
                ));
            }

            // Добавляем пустые строки между заказами
            rowNum += 4;

            // 2. Подсчет строк в текущем заказе
            int orderRowCount = rowNum - orderStartRow;
            orderCount++;

            // 3. Управление разрывами страниц
            if (orderCount % ORDERS_PER_PAGE == 0 ||
                    (rowNum - pageStartRow) > (MAX_ROWS_PER_PAGE - BUFFER_ROWS)) {

                // Устанавливаем разрыв страницы
                sheet.setRowBreak(rowNum - 1);
                pageStartRow = rowNum;

                // Добавляем разделитель (необязательно)
                Row separator = sheet.createRow(rowNum++);
                separator.setHeightInPoints(5f);
            }
        }
        // Финализация настроек печати
        sheet.setFitToPage(false);  // Важно отключить для фиксированного масштаба
        sheet.setRowSumsBelow(false);

        // Жестко задаем поля (в дюймах)
        sheet.setMargin(Sheet.LeftMargin, 0.7);
        sheet.setMargin(Sheet.RightMargin, 0.7);
        sheet.setMargin(Sheet.TopMargin, 0.75);
        sheet.setMargin(Sheet.BottomMargin, 0.75);
    }



    // Новый метод для создания стиля с центрированием
    private static CellStyle createCenteredStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }


    // Стиль для заголовка документа
    private static CellStyle createDocumentTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    // Стиль для информации о клиенте
    private static CellStyle createClientInfoStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);  // Жирный шрифт
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    // Стиль для подписей
    private static CellStyle createSignatureStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        return style;
    }


    private static void createProductSummarySheet(Sheet sheet, List<OrderResponseDto> orders, Workbook workbook) {
        // Собираем статистику по товарам
        Map<String, ProductSummary> productSummary = new HashMap<>();
        double totalWeightAll = 0;
        int totalQuantityAll = 0;

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
                    // Суммируем общий вес и количество
                    totalWeightAll += item.getAmount() * item.getWeight();
                    totalQuantityAll += item.getAmount();
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

        // Сортируем записи перед выводом
        List<Map.Entry<String, ProductSummary>> sortedEntries = productSummary.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue().getNumericVolume(),
                        e1.getValue().getNumericVolume()))
                .toList();

        // Заполняем данные
        int rowNum = 1;
        for (Map.Entry<String, ProductSummary> entry : sortedEntries) {
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

        // Добавляем строку с общим количеством товаров
        Row totalQuantityRow = sheet.createRow(rowNum++);
        Cell totalQuantityLabelCell = totalQuantityRow.createCell(0);
        totalQuantityLabelCell.setCellValue("Общее количество товаров:");
        totalQuantityLabelCell.setCellStyle(totalStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 2));

        Cell totalQuantityValueCell = totalQuantityRow.createCell(3);
        totalQuantityValueCell.setCellValue(totalQuantityAll);
        totalQuantityValueCell.setCellStyle(totalStyle);

        // Добавляем нижнюю границу для строки с количеством
        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.cloneStyleFrom(totalStyle);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        totalQuantityLabelCell.setCellStyle(borderStyle);
        totalQuantityValueCell.setCellStyle(borderStyle);

        // Добавляем строку с общим весом всех товаров
        Row totalWeightRow = sheet.createRow(rowNum);
        Cell totalWeightLabelCell = totalWeightRow.createCell(0);
        totalWeightLabelCell.setCellValue("Общий вес товаров:");
        totalWeightLabelCell.setCellStyle(totalStyle);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 2));

        Cell totalWeightValueCell = totalWeightRow.createCell(3);
        totalWeightValueCell.setCellValue(String.format("%.1f кг", totalWeightAll));
        totalWeightValueCell.setCellStyle(totalStyle);

        // Добавляем нижнюю границу для строки с весом
        totalWeightLabelCell.setCellStyle(borderStyle);
        totalWeightValueCell.setCellStyle(borderStyle);

        // Авто-размер колонок
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // Вспомогательный класс для сводки по товарам
    @Getter
    private static class ProductSummary {
        private final String name;
        private final String volume;
        private final double numericVolume; // Добавляем поле для числового значения объема
        private int totalAmount;

        public ProductSummary(String name, String volume, int amount) {
            this.name = name;
            this.volume = volume;
            this.numericVolume = parseVolume(volume); // Парсим объем при создании
            this.totalAmount = amount;
        }

        public void addAmount(int amount) {
            this.totalAmount += amount;
        }

        private double parseVolume(String volume) {
            if (volume == null) return 0.0;
            try {
                return Double.parseDouble(volume.replace(" л", "").replace(",", ".").trim());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }

    private static void createClientsSummarySheet(Sheet sheet, List<OrderResponseDto> orders, Workbook workbook) {
        // Создаем единый стиль для всей таблицы
        Font tableFont = workbook.createFont();
        tableFont.setFontName("Arial");
        tableFont.setFontHeightInPoints((short)11);

        // Стиль для заголовков
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(tableFont);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Стиль для обычных ячеек с границами
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setFont(tableFont);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        // Стиль для денежных значений с границами
        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setFont(tableFont);
        moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        moneyStyle.setBorderTop(BorderStyle.THIN);
        moneyStyle.setBorderBottom(BorderStyle.THIN);
        moneyStyle.setBorderLeft(BorderStyle.THIN);
        moneyStyle.setBorderRight(BorderStyle.THIN);

        // Заголовки таблицы
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Номер заказа", "Клиент", "Сумма заказа", "Оплата", "Долг", "Примечание"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Заполняем данные
        int rowNum = 1;
        for (OrderResponseDto order : orders) {
            Row row = sheet.createRow(rowNum++);

            // Номер заказа
            Cell orderIdCell = row.createCell(0);
            orderIdCell.setCellValue(order.getId());
            orderIdCell.setCellStyle(dataStyle);

            // Клиент
            Cell clientCell = row.createCell(1);
            clientCell.setCellValue(order.getClientName());
            clientCell.setCellStyle(dataStyle);

            // Сумма заказа (с границами)
            Cell totalCell = row.createCell(2);
            totalCell.setCellValue(order.getTotal());
            totalCell.setCellStyle(moneyStyle);

            // Оплата
            Cell paymentCell = row.createCell(3);
            paymentCell.setCellValue("");
            paymentCell.setCellStyle(dataStyle);

            // Долг
            Cell debtCell = row.createCell(4);
            debtCell.setCellValue("");
            debtCell.setCellStyle(dataStyle);

            // Примечание
            Cell noteCell = row.createCell(5);
            noteCell.setCellValue("");
            noteCell.setCellStyle(dataStyle);
        }

        // Авто-размер колонок
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    private static void createItemHeaderRow(Row row, CellStyle style) {
        String[] headers = {"Код", "Наименование", "Кол-во", "Цена", "Сумма"};
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
        cell1.setCellValue(item.getName() + " " + item.getVolume() + "L" + "x" + item.getQuantity());
        cell1.setCellStyle(style);

        Cell cell3 = row.createCell(2);
        cell3.setCellValue(item.getAmount());
        cell3.setCellStyle(style);

        Cell cell4 = row.createCell(3);
        cell4.setCellValue(item.getPrice());
        cell4.setCellStyle(style);

        Cell cell5 = row.createCell(4);
        cell5.setCellValue(item.getTotal());
        cell5.setCellStyle(style);

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
        style.setAlignment(HorizontalAlignment.CENTER); // Добавляем выравнивание
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }


    private static CellStyle createItemStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // Устанавливаем границы для всех сторон
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // Выравнивание по центру
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private static CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Выравнивание по центру
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // Границы со всех сторон
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
