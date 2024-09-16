package com.kosuri.stores.controller;

import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.handler.LoyaltyPointsHandler;
import com.kosuri.stores.model.request.ConfigureLoyaltyPointsRequest;
import com.kosuri.stores.model.request.CustomerLoyaltyRequest;
import com.kosuri.stores.model.request.RedeemLoyaltyPointsRequest;
import com.kosuri.stores.model.response.ConfigureLoyaltyPointsResponse;
import com.kosuri.stores.model.response.CustomerLoyaltyResponseList;
import com.kosuri.stores.model.response.RedeemLoyaltyPointsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loyalty/points")
public class LoyaltyPointsController {

    @Autowired
    LoyaltyPointsHandler loyaltyPointsHandler;

    @PostMapping("/configure")
    public ResponseEntity<ConfigureLoyaltyPointsResponse> configureLoyaltyPoints(@RequestBody ConfigureLoyaltyPointsRequest request) throws Exception {
        ConfigureLoyaltyPointsResponse response = new ConfigureLoyaltyPointsResponse();
        try {
            response = loyaltyPointsHandler.configureLoyaltyPoints(request);
            response.setResponseMessage("Loyalty points added successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.setResponseMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @PostMapping("/redeem")
    public ResponseEntity<RedeemLoyaltyPointsResponse> redeemLoyaltyPointsResponse(@RequestBody RedeemLoyaltyPointsRequest request) {
        try {
            loyaltyPointsHandler.redeemLoyaltyPointsForCustomer(request);
        } catch (Exception e) {

        }

        return null;
    }

    @PostMapping("/checkDiscount")
    public ResponseEntity<CustomerLoyaltyResponseList> checkDiscount(@RequestBody CustomerLoyaltyRequest request) {
        CustomerLoyaltyResponseList responseList = new CustomerLoyaltyResponseList();
        try {
            responseList.setCustomerLoyaltyResponseList(loyaltyPointsHandler.getDiscountForCustomer(request));
            return ResponseEntity.status(HttpStatus.OK).body(responseList);
        } catch (APIException e) {
            responseList.setResponseMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseList);
        }
        catch (Exception e) {
            responseList.setResponseMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseList);
        }

    }
}
