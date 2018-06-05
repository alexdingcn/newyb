package com.yiban.erp.controller.util;

import com.yiban.erp.dto.SellOrderAllAction;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.sell.SellOrderService;
import com.yiban.erp.service.util.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/export")
public class ExcelController {
    @Autowired
    private ExcelService excelService;

    @Autowired
    private SellOrderService sellOrderService;

    @RequestMapping(value = "/sell/order", method = RequestMethod.POST, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void exportSellList(@RequestBody SellOrderAllAction allAction,
                               HttpServletResponse response,
                               @AuthenticationPrincipal User user) throws Exception {
        allAction.setCompanyId(user.getCompanyId());
        List<SellOrder> result = sellOrderService.getAllList(allAction);
        if (result != null && result.size() > 0) {
            excelService.exportSellList(result, response);
        }
    }
}
