package com.kosuri.stores.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.Date;


@Getter
@Setter
@ToString
@Entity
@Table(name = "pharma_purchase_detail")
public class PurchaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private @Column(name = "id") Integer id;
    private @Column(name = "Doc_Number") BigInteger doc_Number;
    private @Column(name = "Ref_Doc_Number") String readableDocNo;
    private @Column(name = "Date") Date date;
    private @Column(name = "Bill_No") String billNo;
    private @Column(name = "Bill_Date") Date billDt;
    private @Column(name = "Item_Code") Integer itemCode;
    private @Column(name = "Item_Name") String itemName;
    private @Column(name = "Batch_Number") String batchNo;
    private @Column(name = "Expiry_Date") Date expiryDate;
    private @Column(name = "Cat_Code") String catCode;
    private @Column(name = "Cat_Name") String catName;
    private @Column(name = "Mfac_Code") String mfacCode;
    private @Column(name = "Mfac_Name") String mfacName;
    private @Column(name = "Brand_Name") String brandName;
    private @Column(name = "Packing") String packing;
    private @Column(name = "DC_Year") String dcYear;
    private @Column(name = "DC_Prefix") String dcPrefix;
    private @Column(name = "DC_Srno") Integer dcSrno;
    private @Column(name = "Qty_Box") Double qty;
    private @Column(name = "Pack_Qty") Double packQty;
    private @Column(name = "Loose_Qty") Double looseQty;
    private @Column(name = "Sch_Pack_Qty") Double schPackQty;
    private  @Column(name = "Sch_Loose_Qty") Double schLooseQty;
    private @Column(name = "Sch_Disc") Double schDisc;
    private @Column(name = "Sal_Rate") Double SaleRate;
    private @Column(name = "Pur_Rate") Double purRate;
    private @Column(name = "Mrp") Double mRP;
    private  @Column(name = "Purchase_Value") Double purValue;
    private @Column(name = "Disc_Per") Double discPer;
    private @Column(name = "Margin")  Double margin;
    private @Column(name = "Supp_Code") String suppCode;
    private @Column(name = "Supp_Name") String suppName;
    private @Column(name = "Disc") Double discValue;
    private @Column(name = "Taxable_Amt") Double taxableAmt;
    private @Column(name = "Gst_Code") Integer gstCode;
    private @Column(name = "Cgst") Integer cGSTPer;
    private @Column(name = "Sgst") Integer sGSTPer;
    private @Column(name = "Cgst_Amt") Double cGSTAmt;
    private @Column(name = "Sgst_Amt") Double sGSTAmt;
    private @Column(name = "Igst") Integer iGSTPer;
    private @Column(name = "Total") Double total;
    private @Column(name = "Post") Integer post;
    private @Column(name = "Item_Category") String itemCat;
    private @Column(name = "Cess_Per") Integer cessPer;
    private @Column(name = "Cess_Amt") Double cessAmt;
    private @Column(name = "Store_ID") String storeId;
}


