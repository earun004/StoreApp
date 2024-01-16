package com.kosuri.stores.handler;

import com.kosuri.stores.dao.PurchaseEntity;
import com.kosuri.stores.dao.SaleEntity;
import com.kosuri.stores.dao.StockEntity;
import com.kosuri.stores.model.purchaseReport.PurchaseReportRecord;
import com.kosuri.stores.model.purchaseReport.SaleReportRecord;
import com.kosuri.stores.model.purchaseReport.StockRecord;
import com.kosuri.stores.model.request.GeneratePurchaseReportRequest;
import com.kosuri.stores.model.request.GenerateReportRequest;
import com.kosuri.stores.model.request.GenerateSaleReportRequest;
import com.kosuri.stores.model.request.GenerateStockReportRequest;
import com.kosuri.stores.model.response.GeneratePurchaseReportResponse;
import com.kosuri.stores.model.response.GenerateSaleReportResponse;
import com.kosuri.stores.model.response.GenerateStockReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class ReportHandler {
    @Autowired
    private RepositoryHandler repositoryHandler;

    public GeneratePurchaseReportResponse generatePurchaseReport(GeneratePurchaseReportRequest request) throws Exception {
        Optional<List<PurchaseEntity>> purchaseRecords = repositoryHandler.getPurchaseRecordsByStore(request.getStoreId());

        if (!purchaseRecords.isPresent() || purchaseRecords.get().isEmpty()) {
            throw new Exception("No records found for storeId");
        }
        List<PurchaseReportRecord> purchaseReport = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);


        for (PurchaseEntity purchaseEntity : purchaseRecords.get()) {
            if (validateRecord(request, purchaseEntity.getDate()) && validateVendorAndProduct(request, purchaseEntity.getSuppName(), purchaseEntity.getCatName())) {
                PurchaseReportRecord record = new PurchaseReportRecord();
                record.setStoreId(purchaseEntity.getStoreId());
                record.setDate(formatter.format(purchaseEntity.getDate()));
                record.setVendorName(purchaseEntity.getSuppName());
                record.setProductType(purchaseEntity.getCatName());
                record.setBatchNo(purchaseEntity.getBatchNo());
                record.setExpiryDate(formatter.format(purchaseEntity.getExpiryDate()));
                record.setMfgDate(formatter.format(purchaseEntity.getDate()));
                record.setMrp(purchaseEntity.getMRP());
                record.setDiscount(purchaseEntity.getDiscValue());
                record.setGst(purchaseEntity.getCGSTAmt());
                record.setPurchasePrice(purchaseEntity.getPurRate());
                record.setPurchaseAmount(purchaseEntity.getPurValue());

                purchaseReport.add(record);
            }
        }
        GeneratePurchaseReportResponse response = new GeneratePurchaseReportResponse();
        response.setPurchaseReport(purchaseReport);
        return response;
    }

    private boolean validateRecord(GenerateReportRequest request, Date entityDate) throws Exception {
        boolean isValid = true;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        if(!request.getDateFrom().isEmpty() && request.getDateFrom() != null && entityDate.before(formatter.parse(request.getDateFrom()))){
            isValid = false;
        }

        if(!request.getDateTo().isEmpty() && request.getDateTo() != null && entityDate.after(formatter.parse(request.getDateTo()))){
            isValid = false;
        }

        return isValid;
    }

    private boolean validateVendorAndProduct(GenerateReportRequest request, String vendor, String productType){
        boolean isValid = request.getVendorName() == null || request.getVendorName().isEmpty() || (vendor == null || vendor.equals(request.getVendorName()));

        if (request.getProductType() != null && !request.getProductType().isEmpty() && (productType != null && !productType.equals(request.getProductType()))){
            isValid = false;
        }

        return isValid;
    }

    public GenerateSaleReportResponse generateSaleReport(GenerateSaleReportRequest request) throws Exception {
        Optional<List<SaleEntity>> saleRecords = repositoryHandler.getSaleRecordsByStore(request.getStoreId());

        if (!saleRecords.isPresent() || saleRecords.get().isEmpty()) {
            throw new Exception("No records found for storeId");
        }

        List<SaleReportRecord> purchaseReport = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);


        for (SaleEntity saleEntity : saleRecords.get()) {
            if (validateRecord(request, saleEntity.getDate()) && validateVendorAndProduct(request, saleEntity.getSuppName(), saleEntity.getCatName())) {
                SaleReportRecord record = new SaleReportRecord();
                record.setStoreId(saleEntity.getStoreId());
                record.setDate(formatter.format(saleEntity.getDate()));
                record.setVendorName(saleEntity.getSuppName());
                record.setProductType(saleEntity.getCatName());
                record.setBatchNo(saleEntity.getBatchNo());
                record.setExpiryDate(formatter.format(saleEntity.getExpiryDate()));
                record.setMfgDate(formatter.format(saleEntity.getDate()));
                record.setMrp(saleEntity.getmRP());
                record.setDiscount(saleEntity.getDiscValue());
                record.setGst(saleEntity.getcGSTAmt());
                record.setPurchasePrice(saleEntity.getPurRate());
                record.setPurchaseAmount(saleEntity.getTotal());

                purchaseReport.add(record);
            }
        }

        GenerateSaleReportResponse response = new GenerateSaleReportResponse();
        response.setSaleReport(purchaseReport);

        return response;
    }

    public GenerateStockReportResponse generateStockReport(GenerateStockReportRequest request) throws Exception {
        Optional<List<StockEntity>> stockRecords = repositoryHandler.getStockRecordsByStore(request.getStoreId());

        if (!stockRecords.isPresent() || stockRecords.get().isEmpty()) {
            throw new Exception("No records found for storeId");
        }
        List<StockRecord> stockReport = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        for (StockEntity stockEntity: stockRecords.get()) {
            Instant instant = stockEntity.getUpdatedAt().toInstant(ZoneOffset.UTC);
            Date entityDate = Date.from(instant);
            if (validateRecord(request, entityDate) && validateVendorAndProduct(request, stockEntity.getSupplierName(), stockEntity.getItemCategory())) {
                StockRecord record = new StockRecord();
                record.setStoreId(request.getStoreId());
                record.setManufacturer(stockEntity.getManufacturer());
                record.setMfName(stockEntity.getMfName());
                record.setItemCode(stockEntity.getItemCode());
                record.setItemName(stockEntity.getItemName());
                record.setSupplierName(stockEntity.getSupplierName());
                record.setRack(stockEntity.getRack());
                record.setBatch(stockEntity.getBatch());
                record.setExpiryDate(formatter.format(stockEntity.getExpiryDate()));
                record.setBalQuantity(stockEntity.getBalQuantity());
                record.setBalPackQuantity(stockEntity.getBalPackQuantity());
                record.setBalLooseQuantity(stockEntity.getBalLooseQuantity());
                record.setTotal(stockEntity.getTotal());
                record.setMrpPack(stockEntity.getMrpPack());
                record.setPurRatePerPackAfterGST(stockEntity.getPurRatePerPackAfterGST());
                record.setMrpValue(stockEntity.getMrpValue());
                record.setItemCategory(stockEntity.getItemCategory());
                record.setOnlineYesNo(stockEntity.getOnlineYesNo());
                record.setStockValueMrp(stockEntity.getStockValueMrp());
                record.setStockValuePurrate(stockEntity.getStockValuePurrate());
                record.setUpdatedBy(stockEntity.getUpdatedBy());
                record.setUpdatedAt(formatter.format(entityDate));

                stockReport.add(record);
            }
        }
        GenerateStockReportResponse response = new GenerateStockReportResponse();
        response.setStockReport(stockReport);
        return response;
    }
}
