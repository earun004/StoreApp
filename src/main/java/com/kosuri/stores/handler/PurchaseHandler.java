package com.kosuri.stores.handler;

import com.kosuri.stores.dao.PurchaseEntity;
import com.kosuri.stores.dao.PurchaseRepository;
import com.kosuri.stores.dao.StoreEntity;
import com.kosuri.stores.dao.StoreRepository;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.enums.StockUpdateRequestType;
import com.kosuri.stores.model.request.StockUpdateRequest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;


@Service
public class PurchaseHandler {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private StockHandler stockHandler;

    @Autowired
    private StoreRepository storeRepository;

    @Transactional
    public void createPurchaseEntityFromRequest(MultipartFile reapExcelDataFile, String storeId, String emailId) throws Exception {

        Optional<StoreEntity> store = storeRepository.findById(storeId);
        if (store.isPresent()) {
            String ownerEmail = store.get().getOwnerEmail();
            if (!ownerEmail.equals(emailId)) {
                throw new APIException("User does not has access to upload file");
            }
        } else {
            throw new APIException("Store not found for given store id");
        }
        List<PurchaseEntity> purchaseArrayList = getPurchaseEntities(reapExcelDataFile, storeId);

        for(PurchaseEntity purchaseEntity: purchaseArrayList) {
            updateStock(purchaseEntity, emailId);
        }
    }

    private List<PurchaseEntity> getPurchaseEntities(MultipartFile reapExcelDataFile, String storeId) throws Exception {
        List<PurchaseEntity> purchaseArrayList = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = worksheet.iterator();
        rowIterator.next();
        rowIterator.forEachRemaining(row -> {
            try {
                createPurchaseEntityAndSave((XSSFRow) row, purchaseArrayList, storeId);
            } catch (APIException e) {
                String message = e.getMessage();
                throw new RuntimeException(e.getMessage());
            }
        });
        return purchaseArrayList;
    }


    private void createPurchaseEntityAndSave(XSSFRow row, List<PurchaseEntity> purchaseArrayList, String storeId) throws APIException {
        PurchaseEntity tempPurchase = new PurchaseEntity();
        XSSFCell cell0 = row.getCell(0);
        if(cell0 != null) {
            tempPurchase.setDoc_Number(BigDecimal.valueOf(cell0.getNumericCellValue()).toBigInteger());
        }
        tempPurchase.setReadableDocNo(row.getCell(1) != null ? row.getCell(1).getStringCellValue():"");

        tempPurchase.setDate(row.getCell(2) != null ?row.getCell(2).getDateCellValue():new Date());
        tempPurchase.setBillNo(row.getCell(3) != null ?row.getCell(3).getStringCellValue():"");
        tempPurchase.setBillDt(row.getCell(4) != null ?row.getCell(4).getDateCellValue():new Date());
       // tempPurchase.setItemCode(row.getCell(5) != null ? (int) row.getCell(5).getNumericCellValue() :0);
        tempPurchase.setItemName(row.getCell(6) != null ? row.getCell(6).getStringCellValue():"");
        tempPurchase.setBatchNo(row.getCell(7) != null? row.getCell(7).getStringCellValue():"");
        tempPurchase.setExpiryDate(row.getCell(8) != null?row.getCell(8).getDateCellValue():new Date());
        tempPurchase.setCatCode(row.getCell(9) != null ? row.getCell(9).getStringCellValue():"");
        tempPurchase.setCatName(row.getCell(10) != null ? row.getCell(10).getStringCellValue():"");
        tempPurchase.setMfacCode(row.getCell(11) != null ? row.getCell(11).getStringCellValue():"");
        tempPurchase.setMfacName(row.getCell(12) != null ? row.getCell(12).getStringCellValue():"");
        tempPurchase.setBrandName(row.getCell(13) != null? row.getCell(13).getStringCellValue():"");
        tempPurchase.setPacking(row.getCell(14) != null ? row.getCell(14).getStringCellValue():"");
       // tempPurchase.setDcYear(row.getCell(15) != null ? row.getCell(15).getStringCellValue():"");
        tempPurchase.setDcPrefix(row.getCell(16) != null? row.getCell(16).getStringCellValue():"");
       // tempPurchase.setDcSrno(row.getCell(17) != null ? (int)(row.getCell(17).getNumericCellValue()):0);
        tempPurchase.setQty(row.getCell(18) != null ? row.getCell(18).getNumericCellValue():0);
        tempPurchase.setPackQty(row.getCell(19) != null ? row.getCell(19).getNumericCellValue():0);
        tempPurchase.setLooseQty(row.getCell(20) != null ? row.getCell(20).getNumericCellValue():0);
        tempPurchase.setSchPackQty(row.getCell(21) != null ? row.getCell(21).getNumericCellValue():0);
        tempPurchase.setSchLooseQty(row.getCell(22) != null ? row.getCell(22).getNumericCellValue():0);
        tempPurchase.setSchDisc(row.getCell(23) != null ? row.getCell(23).getNumericCellValue():0);
        tempPurchase.setSaleRate(row.getCell(24) != null ? row.getCell(24).getNumericCellValue():0);
        tempPurchase.setPurRate(row.getCell(25) != null ? row.getCell(25).getNumericCellValue():0);
        tempPurchase.setMRP(row.getCell(26) != null ? row.getCell(26).getNumericCellValue():0);
        tempPurchase.setPurValue(row.getCell(27) != null ? row.getCell(27).getNumericCellValue():0);
        tempPurchase.setDiscPer(row.getCell(28) != null ? row.getCell(28).getNumericCellValue():0);
        tempPurchase.setMargin(row.getCell(29) != null ? row.getCell(29).getNumericCellValue():0);
        tempPurchase.setSuppCode((row.getCell(30).getStringCellValue() != null)?
                row.getCell(30).getStringCellValue():"");
        tempPurchase.setSuppName((row.getCell(31).getStringCellValue() != null)?
                row.getCell(31).getStringCellValue():"");
        tempPurchase.setDiscValue(row.getCell(32) != null ? (row.getCell(32).getNumericCellValue()):0);
        tempPurchase.setTaxableAmt(row.getCell(33) != null ? row.getCell(33).getNumericCellValue():0);
        tempPurchase.setGstCode(row.getCell(34) != null ? (int) row.getCell(34).getNumericCellValue() :0);
        tempPurchase.setCGSTPer(row.getCell(35) != null ? (int)row.getCell(35).getNumericCellValue():0);
        tempPurchase.setCGSTAmt(row.getCell(36) != null ? row.getCell(36).getNumericCellValue():0);
        tempPurchase.setSGSTPer(row.getCell(37) != null ? (int)row.getCell(37).getNumericCellValue():0);
        tempPurchase.setSGSTAmt(row.getCell(38) != null ? row.getCell(38).getNumericCellValue():0);
        tempPurchase.setIGSTPer(row.getCell(39) != null ? (int)row.getCell(39).getNumericCellValue():0);
        tempPurchase.setCessPer(row.getCell(41) != null ? (int)row.getCell(41).getNumericCellValue():0);
        tempPurchase.setCessAmt(row.getCell(42) != null ? row.getCell(42).getNumericCellValue():0);
        tempPurchase.setTotal(row.getCell(43) != null ? row.getCell(43).getNumericCellValue():0);
        tempPurchase.setPost(row.getCell(44) != null ? (int) row.getCell(44).getNumericCellValue() :0);
        tempPurchase.setStoreId(storeId);
        try {
            PurchaseEntity abc = purchaseRepository.save(tempPurchase);
            System.out.println(abc.getId());
        } catch (Exception e) {
            throw new APIException(e.getMessage());
        }
        purchaseArrayList.add(tempPurchase);
    }

    private void updateStock(PurchaseEntity purchaseEntity, String emailId) throws Exception {
        StockUpdateRequest stockUpdateRequest = new StockUpdateRequest();
        stockUpdateRequest.setExpiryDate(purchaseEntity.getExpiryDate());
        stockUpdateRequest.setBalLooseQuantity(purchaseEntity.getLooseQty());
        stockUpdateRequest.setBatch(purchaseEntity.getBatchNo());
        stockUpdateRequest.setStockUpdateRequestType(StockUpdateRequestType.PURCHASE);
        stockUpdateRequest.setQtyPerBox(purchaseEntity.getQty());
        stockUpdateRequest.setPackQuantity(purchaseEntity.getPackQty());
        stockUpdateRequest.setBalLooseQuantity(purchaseEntity.getLooseQty());
        stockUpdateRequest.setItemCategory(purchaseEntity.getItemCat());
        stockUpdateRequest.setItemCode(purchaseEntity.getItemCode().toString());
        stockUpdateRequest.setItemName(purchaseEntity.getItemName());
        stockUpdateRequest.setMfName(purchaseEntity.getMfacName());
        stockUpdateRequest.setManufacturer(purchaseEntity.getMfacCode());
        stockUpdateRequest.setStoreId(purchaseEntity.getStoreId());
        stockUpdateRequest.setMrpPack(purchaseEntity.getMRP());
        stockUpdateRequest.setTotalPurchaseValueAfterGST(purchaseEntity.getTotal());
        stockUpdateRequest.setSupplierName(purchaseEntity.getSuppName());
        stockUpdateRequest.setUpdatedBy(emailId);

        stockHandler.updateStock(stockUpdateRequest);
    }
}
