package com.yiban.erp.service.financial;

import com.yiban.erp.entities.BuyOrder;
import com.yiban.erp.entities.SellOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 财务
 */
@Service
public class FinancialService {

    private static final Logger logger = LoggerFactory.getLogger(FinancialService.class);

    /**
     * 根据销售单创建一笔往来账流水记录
     * @param sellOrder
     */
    public void createFlowBySellOrder(SellOrder sellOrder) {



    }

    /**
     * 根据采购单创建一笔往来账流水记录
     * @param buyOrder
     */
    public void createFlowByBuyOrder(BuyOrder buyOrder) {

    }

}
