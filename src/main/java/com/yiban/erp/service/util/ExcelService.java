package com.yiban.erp.service.util;

import com.yiban.erp.constant.SellOrderStatus;
import com.yiban.erp.entities.SellOrder;
import org.apache.http.client.utils.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ExcelService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    public void exportSellList(List<SellOrder> orderList, HttpServletResponse response) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        String fileName = "export";
        XSSFSheet sheet = workbook.createSheet(fileName);

        int rowNum = 0;
        int colNum = 0;
        String[] headers = new String[]{"销售单号", "制单日", "仓库点", "客户", "状态", "付款状态",
                "总计金额", "总计数量", "免零金额", "整单折扣率", "销售员", "制单人", "提货员",
                "收货人", "收货电话", "收货地址", "温控方式", "运输方式", "运输工具"};
        Row r = sheet.createRow(rowNum++);
        for (int rn = 0; rn < headers.length; rn++) {
            r.createCell(colNum++).setCellValue(headers[rn]);
        }

        CellStyle style;
        DataFormat format = workbook.createDataFormat();

        for (SellOrder sellOrder : orderList) {
            Row row = sheet.createRow(rowNum++);
            colNum = 0;
            Cell cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getOrderNumber());

            cell = row.createCell(colNum++);
            cell.setCellValue(DateUtils.formatDate(sellOrder.getCreateOrderDate(), "yyyy-MM-dd"));

            cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getWarehouseName());

            cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getCustomerName());

            cell = row.createCell(colNum++);
            cell.setCellValue(getSellOrderStatus(sellOrder.getStatus()));

            cell = row.createCell(colNum++);
            cell.setCellValue(getPayStatus(sellOrder));

            cell = row.createCell(colNum++);
            cell.setCellValue(String.valueOf(sellOrder.getTotalAmount()));
            style = workbook.createCellStyle();
            style.setDataFormat(format.getFormat("#,##0.00"));
            cell.setCellStyle(style);

            cell = row.createCell(colNum++);
            cell.setCellValue(String.valueOf(sellOrder.getTotalQuantity()));
            cell.setCellStyle(style);

            cell = row.createCell(colNum++);
            cell.setCellValue(String.valueOf(sellOrder.getFreeAmount()));
            cell.setCellStyle(style);

            cell = row.createCell(colNum++);
            cell.setCellValue(String.valueOf(sellOrder.getDisRate()));
            cell.setCellStyle(style);

            cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getSaleNickName());

            cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getCreateBy());

            cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getTakeGoodsUser());

            cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getCustomerRepName());

            cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getCustomerRepContactPhone());

            cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getCustomerRepRepertoryAddress());

            cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getTemperControlName());

            cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getShipMethodName());

            cell = row.createCell(colNum++);
            cell.setCellValue(sellOrder.getShipToolName());
        }

        response.reset();
        response.setHeader("Cache-control", "private");
        response.setHeader("Cache-Control", "maxage=3600");
        response.setHeader("Pragma", "public");
        response.setContentType("application/vnd.ms-excel");
//        response.setContentType("application/octet-stream;charset=UTF-8;");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Accept-Ranges", "bytes");

        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName + ".xls").getBytes(), "iso-8859-1"));
        } catch (UnsupportedEncodingException ex) {
            logger.error("Failed to export, {}", ex.getMessage());
        }

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IOException ex) {
            logger.error("Failed to export, {}", ex.getMessage());
        }

        try {
            workbook.write(out);
            out.flush();
        } catch (FileNotFoundException e) {
            logger.error("Failed to export, {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Failed to export, {}", e.getMessage());
        } finally {
            try {
                workbook.close();
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                logger.error("Failed to export, {}", e.getMessage());
            }
        }

        logger.info("Successfully to export sell list");
    }

    private String getSellOrderStatus(String status) {
        if (SellOrderStatus.INIT.name().equalsIgnoreCase(status)) {
            return "制单初始";
        } else if (SellOrderStatus.QUALITY_CHECKED.name().equalsIgnoreCase(status)) {
            return "质量审核完成";
        } else if (SellOrderStatus.SALE_CHECKED.name().equalsIgnoreCase(status)) {
            return "销售审核完成";
        } else if (SellOrderStatus.TEMP_STORAGE.name().equalsIgnoreCase(status)) {
            return "暂挂";
        } else if (SellOrderStatus.QUALITY_REJECT.name().equalsIgnoreCase(status)) {
            return "质审拒绝";
        }
        return status;
    }

    private String getPayStatus(SellOrder sellOrder) {
        BigDecimal remainingAmt = sellOrder.getTotalAmount().subtract(sellOrder.getPaidAmount());
        if (BigDecimal.ZERO.equals(remainingAmt)) {
            return "已支付";
        } else if (remainingAmt.compareTo(BigDecimal.ZERO) > 0 && BigDecimal.ZERO.equals(sellOrder.getPaidAmount())) {
            return "未支付";
        } else if (remainingAmt.compareTo(BigDecimal.ZERO) > 0 && sellOrder.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            return "余￥" + remainingAmt.toString();
        } else if (remainingAmt.compareTo(BigDecimal.ZERO) < 0) {
            return "超额支付￥" + remainingAmt.negate().toString();
        }
        return "";
    }
}
