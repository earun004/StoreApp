package com.kosuri.stores.handler;
import com.kosuri.stores.dao.*;
import com.kosuri.stores.exception.APIException;
import com.kosuri.stores.model.enums.StockUpdateRequestType;
import com.kosuri.stores.model.request.StockUpdateRequest;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class SaleHandler {
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private StockHandler stockHandler;

    @Autowired
    private StoreRepository storeRepository;

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class})
    public void createSaleEntityFromRequest(MultipartFile reapExcelDataFile, String storeId, String emailId) throws Exception {

        Optional<StoreEntity> store = storeRepository.findById(storeId);
        if (store.isPresent()) {
            String ownerEmail = store.get().getOwnerEmail();
            if (!ownerEmail.equals(emailId)) {
                throw new APIException("User does not has access to upload file");
            }
        } else {
            throw new APIException("Store not found for given id");
        }

        List<SaleEntity> saleArrayList = new ArrayList<SaleEntity>();
        XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            SaleEntity tempSale = new SaleEntity();
            XSSFRow row = worksheet.getRow(i);
            tempSale.setDoc_Number(String.valueOf(BigDecimal.valueOf(row.getCell(0).getNumericCellValue()).toBigInteger()));
            tempSale.setReadableDocNo(row.getCell(1).getStringCellValue());
            tempSale.setDate(row.getCell(2).getDateCellValue());
            tempSale.setTime(row.getCell(3).getDateCellValue());
            tempSale.setCustCode(row.getCell(4).getStringCellValue());
            tempSale.setCustName(row.getCell(5).getStringCellValue());

            tempSale.setPatientName(row.getCell(6).getStringCellValue());
            tempSale.setCreatedUser(String.valueOf(BigDecimal.valueOf(row.getCell(7).getNumericCellValue()).toBigInteger()));
            try {
                tempSale.setItemCode(row.getCell(8).getStringCellValue());
            } catch (Exception e) {
                tempSale.setItemCode(String.valueOf(row.getCell(8).getNumericCellValue()));
            }
            try {
                tempSale.setItemName(row.getCell(9).getStringCellValue());
            } catch (Exception e) {
                tempSale.setItemName(String.valueOf(row.getCell(9).getNumericCellValue()));
            }

            try {
                tempSale.setBatchNo(row.getCell(10).getStringCellValue());
            } catch (Exception e) {
                tempSale.setBatchNo(String.valueOf(BigDecimal.valueOf(row.getCell(10).getNumericCellValue()).toBigInteger()));
            }
            tempSale.setExpiryDate(row.getCell(11).getDateCellValue());
            tempSale.setMfacCode(row.getCell(12).getStringCellValue());
            tempSale.setMfacName(row.getCell(13).getStringCellValue());
            tempSale.setCatCode(row.getCell(14).getStringCellValue());
            tempSale.setCatName(row.getCell(15).getStringCellValue());
            tempSale.setBrandName(row.getCell(16).getStringCellValue());
            tempSale.setPacking(row.getCell(17).getStringCellValue());
            tempSale.setQtyBox(row.getCell(18).getNumericCellValue());
            tempSale.setQty(BigDecimal.valueOf(row.getCell(19).getNumericCellValue()).doubleValue());
            tempSale.setSchQty((int) row.getCell(20).getNumericCellValue());
            tempSale.setSchDisc(row.getCell(21).getNumericCellValue());
            tempSale.setSaleRate(row.getCell(22).getNumericCellValue());
            tempSale.setmRP(BigDecimal.valueOf(row.getCell(23).getNumericCellValue()).doubleValue());
            tempSale.setSaleValue(BigDecimal.valueOf(row.getCell(24).getNumericCellValue()).doubleValue());
            tempSale.setDiscPerct(row.getCell(25).getNumericCellValue());
            tempSale.setDiscValue(row.getCell(26).getNumericCellValue());
            tempSale.setTaxableAmt(row.getCell(27).getNumericCellValue());
            tempSale.setcGSTPer((int) row.getCell(28).getNumericCellValue());
            tempSale.setcGSTAmt(row.getCell(29).getNumericCellValue());
            tempSale.setsGSTPer((int) row.getCell(30).getNumericCellValue());
            tempSale.setsGSTAmt(row.getCell(31).getNumericCellValue());
            tempSale.setiGSTPer((int) row.getCell(32).getNumericCellValue());
            tempSale.setiGSTAmt(row.getCell(33).getNumericCellValue());
            tempSale.setCessPer((int) row.getCell(34).getNumericCellValue());
            tempSale.setCessAmt(row.getCell(35).getNumericCellValue());
            tempSale.setAddCessPer((int) row.getCell(36).getNumericCellValue());
            tempSale.setAddCessAmt(row.getCell(37).getNumericCellValue());
            tempSale.setTotal(row.getCell(38).getNumericCellValue());
            tempSale.setRoundOff(row.getCell(39).getNumericCellValue());
            try {
                tempSale.setSuppBillNo(row.getCell(40).getStringCellValue());
            } catch (Exception e) {
                tempSale.setSuppBillNo(String.valueOf(row.getCell(41).getNumericCellValue()));
            }

            tempSale.setSuppCode(row.getCell(42).getStringCellValue());
            tempSale.setSuppName(row.getCell(43).getStringCellValue());
            tempSale.setProfessional(row.getCell(44).getStringCellValue());


            tempSale.setMobile(String.valueOf(BigDecimal.valueOf(row.getCell(45).getNumericCellValue()).toBigInteger()));
            try {
                tempSale.setLcCode(row.getCell(46).getStringCellValue());
            } catch (Exception e) {
                tempSale.setLcCode(String.valueOf(row.getCell(46).getNumericCellValue()));
            }

            tempSale.setPurRate(row.getCell(47).getNumericCellValue());
            tempSale.setPurRateWithGsT(row.getCell(48).getNumericCellValue());
            tempSale.setStoreId(storeId);

            saleArrayList.add(tempSale);
        }

        saleRepository.saveAll(saleArrayList);

        for (SaleEntity saleEntity : saleArrayList) {
            updateStock(saleEntity, emailId);
        }
    }

    private void updateStock(SaleEntity saleEntity, String email) throws Exception {
        StockUpdateRequest stockUpdateRequest = new StockUpdateRequest();
        stockUpdateRequest.setExpiryDate(saleEntity.getExpiryDate());
        stockUpdateRequest.setBatch(saleEntity.getBatchNo());
        stockUpdateRequest.setStockUpdateRequestType(StockUpdateRequestType.SALE);
        stockUpdateRequest.setQtyPerBox(saleEntity.getQtyBox());
        stockUpdateRequest.setBalLooseQuantity(saleEntity.getQty());
        stockUpdateRequest.setItemCode(saleEntity.getItemCode());
        stockUpdateRequest.setItemName(saleEntity.getItemName());
        stockUpdateRequest.setMfName(saleEntity.getMfacName());
        stockUpdateRequest.setManufacturer(saleEntity.getMfacCode());
        stockUpdateRequest.setStoreId(saleEntity.getStoreId());
        stockUpdateRequest.setMrpPack(saleEntity.getmRP() * saleEntity.getQtyBox());
        stockUpdateRequest.setSupplierName(saleEntity.getSuppName());
        stockUpdateRequest.setUpdatedBy(email);

        stockHandler.updateStock(stockUpdateRequest);
    }
}
