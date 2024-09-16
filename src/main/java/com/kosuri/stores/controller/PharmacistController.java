package com.kosuri.stores.controller;

import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.PharmaHandler;
import com.kosuri.stores.model.request.PharmasistRequest;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/pharmacist")
public class PharmacistController {

    @Autowired
    private PharmaHandler pharmaHandler;

    @PostMapping("/addPharmacist")
    public ResponseEntity<GenericResponse> addPharmacist(@RequestBody PharmasistRequest request) {
        HttpStatus httpStatus;
        GenericResponse response = new GenericResponse();
        try {
            boolean isPharmacistAdded = pharmaHandler.addPharmacist(request);
            httpStatus = HttpStatus.OK;
            if (isPharmacistAdded){
                response.setResponseMessage("Pharmacist added successfully");
            }else{
                response.setResponseMessage("Error While Adding Pharmacist Details");
            }

        } catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setResponseMessage(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(response);
    }

    @PutMapping("/updatePharmacist")
    public ResponseEntity<GenericResponse> updatePharmacist(@RequestBody PharmasistRequest request) {
        HttpStatus httpStatus;
        GenericResponse response = new GenericResponse();
        try {
            boolean isPharmacistUpdated = pharmaHandler.updatePharmacist(request);
            httpStatus = HttpStatus.OK;
            if (isPharmacistUpdated){
                response.setResponseMessage("Pharmacist updated successfully");
            }else{
                response.setResponseMessage("Error While Updating Pharmacist Details");
            }
        } catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setResponseMessage(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(response);
    }

    @PutMapping("/searchPharmacist")
    public ResponseEntity<SearchResponse> searchPharmacist(@RequestParam String mobileNumber,
                                           @RequestParam String emailAddress,
                                           @RequestParam String availableLocation) {
        HttpStatus httpStatus;
        SearchResponse response = new SearchResponse();
        try {
            response = pharmaHandler.searchPharmacist(mobileNumber, emailAddress, availableLocation);
            httpStatus = HttpStatus.OK;

        } catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;

        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        }

        return ResponseEntity.status(httpStatus).body(response);
    }

}
