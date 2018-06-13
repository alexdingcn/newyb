package com.yiban.erp.dto;

import com.yiban.erp.entities.FinancialFlow;

import java.util.List;

public class FinancialDetailResult {

    //当前流水
    FinancialFlow selfFlow;

    //相同关联流水号的订单列表
    List<FinancialFlow> sameRefNoFlows;

    //拥有当前交易流水号为关联流水号的列表
    List<FinancialFlow> sameBizNoFlows;

    public FinancialFlow getSelfFlow() {
        return selfFlow;
    }

    public void setSelfFlow(FinancialFlow selfFlow) {
        this.selfFlow = selfFlow;
    }

    public List<FinancialFlow> getSameRefNoFlows() {
        return sameRefNoFlows;
    }

    public void setSameRefNoFlows(List<FinancialFlow> sameRefNoFlows) {
        this.sameRefNoFlows = sameRefNoFlows;
    }

    public List<FinancialFlow> getSameBizNoFlows() {
        return sameBizNoFlows;
    }

    public void setSameBizNoFlows(List<FinancialFlow> sameBizNoFlows) {
        this.sameBizNoFlows = sameBizNoFlows;
    }
}
