package com.example.demo.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class poiController {

    @GetMapping("/upload")
    public String uploadForm() {
        return "uploadForm";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "파일을 선택해주세요.");
            return "redirect:/upload";
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<List<String>> allRowsData = new ArrayList<>();

            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                for (Cell cell : row) {
                    String cellValueAsString = "";
                    CellType cellType = cell.getCellType();

                    switch (cellType) {
                        case STRING:
                            cellValueAsString = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                cellValueAsString = cell.getDateCellValue().toString();
                            } else {
                                double numericValue = cell.getNumericCellValue();
                                // 값이 정수이면 소수점 이하 제거
                                if (numericValue == Math.floor(numericValue)) {
                                    cellValueAsString = String.valueOf((int) numericValue);
                                } else {
                                    cellValueAsString = String.valueOf(numericValue);
                                }
                            }
                            break;
                        case BOOLEAN:
                            cellValueAsString = String.valueOf(cell.getBooleanCellValue());
                            break;
                        case FORMULA:
                            CellValue cellValue = workbook.getCreationHelper().createFormulaEvaluator().evaluate(cell);
                            switch (cellValue.getCellType()) {
                                case STRING:
                                    cellValueAsString = cellValue.getStringValue();
                                    break;
                                case NUMERIC:
                                    double numericFormulaValue = cellValue.getNumberValue();
                                    // 값이 정수이면 소수점 이하 제거
                                    if (numericFormulaValue == Math.floor(numericFormulaValue)) {
                                        cellValueAsString = String.valueOf((int) numericFormulaValue);
                                    } else {
                                        cellValueAsString = String.valueOf(numericFormulaValue);
                                    }
                                    break;
                                case BOOLEAN:
                                    cellValueAsString = String.valueOf(cellValue.getBooleanValue());
                                    break;
                                case ERROR:
                                    cellValueAsString = "Error: " + FormulaError.forInt(cellValue.getErrorValue()).getString();
                                    break;
                                default:
                                    cellValueAsString = "";
                            }
                            break;
                        case BLANK:
                            cellValueAsString = "";
                            break;
                        case ERROR:
                            cellValueAsString = "Error: " + FormulaError.forInt(cell.getErrorCellValue()).getString();
                            break;
                        default:
                            cellValueAsString = "";
                    }
                    rowData.add(cellValueAsString);
                }
                allRowsData.add(rowData);
            }

            redirectAttributes.addFlashAttribute("message", "파일 업로드 성공!");
            redirectAttributes.addFlashAttribute("allRowsData", allRowsData);
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "파일 업로드 실패: " + e.getMessage());
        }

        return "redirect:/upload";
    }
}