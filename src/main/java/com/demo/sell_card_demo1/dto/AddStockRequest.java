package com.demo.sell_card_demo1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AddStockRequest {
    private List<String> activationCodes;

    // Sửa thành LocalDate và thêm JsonFormat
    // Backend sẽ chấp nhận chuỗi dạng "yyyy-MM-dd" (VD: 2025-12-23)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate expirationDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate activationDate;
}