package com.yiban.erp;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.entities.Customer;
import com.yiban.erp.util.IDCardUtil;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.Arrays;

public class UtilToolTest {

    @Test
    public void testField() {
        Customer customer = new Customer();
        customer.setName("  xxx  ");
        customer.setCustomerNo("    ");
        System.out.println("name:" + customer.getName());
        System.out.println("custNo:" + customer.getCustomerNo());
        Customer result = UtilTool.trimString(customer);
        System.out.println("name:" + result.getName());
        System.out.println("custNo:" + result.getCustomerNo());
        System.out.println("the same:" + (customer.equals(result)));
    }


    @Test
    public void testOrderNumber() {
        System.out.println(UtilTool.makeOrderNumber(1, OrderNumberType.BUY));
        System.out.println(UtilTool.makeOrderNumber(1, OrderNumberType.BUY));
        System.out.println(UtilTool.makeOrderNumber(1, OrderNumberType.BUY));
        System.out.println(UtilTool.makeOrderNumber(1, OrderNumberType.BUY));
        System.out.println(UtilTool.makeOrderNumber(1, OrderNumberType.BUY));
    }

    @Test
    public void testIDCard() {
        String idCard = "45090219881016259X";
        boolean validate = IDCardUtil.isValid(idCard);
        System.out.println("validate result:" + validate);
        System.out.println("birthday:" + IDCardUtil.getBirthday(idCard));
        System.out.println("sex:" + IDCardUtil.getSex(idCard));

        String newIdCard = IDCardUtil.generateID();
        System.out.println(newIdCard);
        System.out.println("new IDCard validate:" + IDCardUtil.isValid(newIdCard));
        System.out.println("birthday:" + IDCardUtil.getBirthday(newIdCard));
        System.out.println("sex:" + IDCardUtil.getSex(newIdCard));
    }

    @Test
    public void testMakeIDCard() {
        String newIDCard = IDCardUtil.generateID();
        System.out.println(newIDCard);
    }


    @Test
    public void testString() {
        String token = "XXXX.12.123.xxxxxxxxxxxxxxx";
        String cacheKey = token.substring(0, token.lastIndexOf("."));
        System.out.println(cacheKey);
    }

    @Test
    public void testArraysSort() {

        long a = 10L;
        long b = -1L;
        long c = 5L;
        long[] arr = {a, b, c};
        Arrays.sort(arr);
        System.out.println(JSON.toJSONString(arr));

    }
}
