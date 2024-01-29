package com.kosuri.stores.controller;

import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.ReportHandler;
import com.kosuri.stores.model.request.GeneratePurchaseReportRequest;
import com.kosuri.stores.model.request.GenerateSaleReportRequest;
import com.kosuri.stores.model.request.GenerateStockReportRequest;
import com.kosuri.stores.model.response.GeneratePurchaseReportResponse;
import com.kosuri.stores.model.response.GenerateSaleReportResponse;
import com.kosuri.stores.model.response.GenerateStockReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportHandler reportHandler;
    @PostMapping("/generate/purchase")
    public ResponseEntity<GeneratePurchaseReportResponse> generatePurchaseReport(@RequestBody GeneratePurchaseReportRequest request) throws Exception{
        HttpStatus httpStatus;
        GeneratePurchaseReportResponse resp = new GeneratePurchaseReportResponse();
        try {
            resp = reportHandler.generatePurchaseReport(request);
            httpStatus = HttpStatus.OK;
        } catch (APIException e){
            httpStatus = HttpStatus.BAD_REQUEST;
            resp.setMsg(e.getMessage());
        } catch (Exception e){
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            resp.setMsg(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(resp);
    }

    @PostMapping("/generate/sale")
    public ResponseEntity<GenerateSaleReportResponse> generateSaleReport(@RequestBody GenerateSaleReportRequest request) throws Exception {
        HttpStatus httpStatus;
        GenerateSaleReportResponse resp = new GenerateSaleReportResponse();
        try {
            resp = reportHandler.generateSaleReport(request);
            httpStatus = HttpStatus.OK;
        } catch (APIException e){
            httpStatus = HttpStatus.BAD_REQUEST;
            resp.setMsg(e.getMessage());
        } catch (Exception e){
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            resp.setMsg(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(resp);
    }

    @PostMapping("/generate/stock")
    public ResponseEntity<GenerateStockReportResponse> generateSaleReport(@RequestBody GenerateStockReportRequest request) throws Exception {
        HttpStatus httpStatus;
        GenerateStockReportResponse resp = new GenerateStockReportResponse();
        try {
            resp = reportHandler.generateStockReport(request);
            httpStatus = HttpStatus.OK;
        } catch (APIException e){
            httpStatus = HttpStatus.BAD_REQUEST;
            resp.setMsg(e.getMessage());
        } catch (Exception e){
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            resp.setMsg(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(resp);
    }
}
