package com.kosuri.stores.controller;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.PrimaryCareHandler;
import com.kosuri.stores.model.request.*;
import com.kosuri.stores.model.response.GenericResponse;
import com.kosuri.stores.model.response.GetAllPrimaryCareCentersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/primaryCare")
public class PrimaryCareController {
    @Autowired
    PrimaryCareHandler primaryCareHandler;


    @PostMapping("/addPrimaryCareCenter")
    public ResponseEntity<GenericResponse> addPrimaryCare(@RequestBody PrimaryCareUserRequest request) {

        HttpStatus httpStatus;
        GenericResponse response = new GenericResponse();
        boolean isPcAdded = false;
        try {
            isPcAdded =  primaryCareHandler.addPrimaryCare(request);
            httpStatus = HttpStatus.OK;
            if (isPcAdded){
                response.setResponseMessage("Primary Care Centre added successfully");
            } else{
                response.setResponseMessage("Primary Care Centre Cannot be added.");
            }

        }
        catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response.setResponseMessage(e.getMessage());
        }
        catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setResponseMessage(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(response);
    }

    @PutMapping("/updatePrimaryCareCenter")
    public ResponseEntity<GenericResponse> updatePrimaryCareCenter(@RequestBody PrimaryCareUserRequest request) {
        HttpStatus httpStatus;
        GenericResponse response = new GenericResponse();
        try {
            primaryCareHandler.updatePrimaryCareCenter(request);
            httpStatus = HttpStatus.OK;
            response.setResponseMessage("Primary Care Center updated successfully");
        } catch (APIException e) {
            httpStatus = HttpStatus.BAD_REQUEST;
            response.setResponseMessage(e.getMessage());
        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setResponseMessage(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(response);
    }

    @GetMapping("/getAllPrimaryCareCenters")
    public ResponseEntity<GetAllPrimaryCareCentersResponse> getAllPrimaryCareCenters() {
        HttpStatus httpStatus;
        GetAllPrimaryCareCentersResponse response = new GetAllPrimaryCareCentersResponse();

        try {
            response = primaryCareHandler.getAllPrimaryCareCenters();
            httpStatus = HttpStatus.OK;

        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setResponseMessage(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(response);
    }

    @GetMapping("/getPrimaryCareCenter")
    public ResponseEntity<GetAllPrimaryCareCentersResponse> getPrimaryCareCenter( @RequestParam(value = "location", required = false) String location,
                                                                    @RequestParam(value = "userId", required = false) String userId) {
        HttpStatus httpStatus;
        GetAllPrimaryCareCentersResponse response = new GetAllPrimaryCareCentersResponse();

        try {
            response = primaryCareHandler.getPrimaryCareCenterByLocationOrUserId(location, userId);
            httpStatus = HttpStatus.OK;

        } catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setResponseMessage(e.getMessage());
        }

        return ResponseEntity.status(httpStatus).body(response);
    }
}




