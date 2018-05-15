package com.yiban.erp.controller.supplier;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.SupplierContactMapper;
import com.yiban.erp.entities.SupplierContact;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


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
    public ResponseEntity<String> remove(@PathVariable Integer contactId,
                                         @AuthenticationPrincipal User user) {
        logger.info("user:{} request to remove supplier contact:{}", user.getId(), contactId);
        int result = supplierContactMapper.deleteByPrimaryKey(contactId);
        if (result <= 0) {
            logger.error("Failed to delete supplier contactId=" + contactId);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody SupplierContact supplierContact,
                                      @AuthenticationPrincipal User user) throws Exception{
        logger.info("user: {} ADD/UPDATE supplier contact:{}", user.getId(), supplierContact);
        if (supplierContact.getSupplierId() == null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        int result = 0;
        if (supplierContact.getId() == null) {
            supplierContact.setCreatedBy(user.getNickname());
            supplierContact.setCreatedTime(new Date());
            result = supplierContactMapper.insert(supplierContact);
        } else {
            supplierContact.setUpdatedBy(user.getNickname());
            supplierContact.setUpdatedTime(new Date());
            result = supplierContactMapper.updateByPrimaryKeySelective(supplierContact);
        }
        if (result > 0) {
            return ResponseEntity.ok().body(supplierContact.getId().toString());
        }
        throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
    }
}