package com.yiban.erp.controller.util;

import com.yiban.erp.dto.SellOrderAllAction;
import com.yiban.erp.entities.SellOrder;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.sell.SellOrderService;
import com.yiban.erp.service.util.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/export")
public class ExcelController {
    @Autowired
    private ExcelService excelService;

    @Autowired
    private SellOrderService sellOrderService;

    @RequestMapping(value = "/sell/order", method = RequestMethod.POST)
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
