package com.kosuri.stores.controller;

import com.kosuri.stores.handler.RepositoryHandler;
import com.kosuri.stores.handler.SaleHandler;
import com.kosuri.stores.model.response.GenericResponse;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/sale")
public class SaleController {
    @Autowired
    private RepositoryHandler repositoryHandler;
    @Autowired
    private SaleHandler saleHandler;
    @PostMapping("/import")
    public ResponseEntity<GenericResponse> mapReapExcelDatatoDB(@RequestParam("file") MultipartFile reapExcelDataFile,
                                                                @Nonnull @RequestParam("store_id") String storeId, @RequestParam("email_id") String emailId) {
        GenericResponse response = new GenericResponse();
        try {
            saleHandler.createSaleEntityFromRequest(reapExcelDataFile, storeId, emailId);
            response.setResponseMessage("Successfully uploaded the file!");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            response.setResponseMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.setResponseMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}