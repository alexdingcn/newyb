package com.yiban.erp.controller.supplier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.SupplierContactMapper;
import com.yiban.erp.dao.SupplierMapper;
import com.yiban.erp.entities.Supplier;
import com.yiban.erp.entities.SupplierContact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/supplier/contact")
public class SupplierContactController {
    private static final Logger logger = LoggerFactory.getLogger(SupplierContactController.class);

    @Autowired
    private SupplierContactMapper supplierContactMapper;

    /**
     * 获取供应商的目录树
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@RequestParam Long supplierId) {
        List<SupplierContact> contactList = supplierContactMapper.getSupplierContacts(supplierId);
        return ResponseEntity.ok().body(JSON.toJSONString(contactList));
    }

    @RequestMapping(value = "/{contactId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get(@PathVariable Integer contactId) {
        SupplierContact contact = supplierContactMapper.selectByPrimaryKey(contactId);
        return ResponseEntity.ok().body(JSON.toJSONString(contact));
    }

    @RequestMapping(value = "/remove/{contactId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@PathVariable Integer contactId) {
        int result = supplierContactMapper.deleteByPrimaryKey(contactId);
        if (result <= 0) {
            logger.error("Failed to delete supplier contactId=" + contactId);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody SupplierContact supplierContact) {
        logger.info("ADD new supplier contact:{}", supplierContact);

        int result = 0;
        if (supplierContact.getId() == null) {
            supplierContact.setCreatedBy("admin");
            supplierContact.setCreatedTime(new Date());
            result = supplierContactMapper.insertSelective(supplierContact);
        } else {
            result = supplierContactMapper.updateByPrimaryKey(supplierContact);
        }
        if (result > 0) {
            return ResponseEntity.ok().body(supplierContact.getId().toString());
        }
        return ResponseEntity.badRequest().body("Failed to insert/update");
    }
}