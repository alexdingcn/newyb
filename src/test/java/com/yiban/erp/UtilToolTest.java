package com.yiban.erp;

import com.yiban.erp.entities.Customer;
import com.yiban.erp.util.UtilTool;
import org.junit.Test;

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


}
