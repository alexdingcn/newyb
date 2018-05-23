package com.yiban.erp.service.company;


import com.yiban.erp.dao.CompanyMapper;
import com.yiban.erp.dao.UserMapper;
import com.yiban.erp.dao.UserRoleMapper;
import com.yiban.erp.entities.Company;
import com.yiban.erp.entities.User;
import com.yiban.erp.entities.UserRole;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.service.customer.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class companyService {
    private static final Logger logger = LoggerFactory.getLogger(companyService.class);

    @Autowired
    private CompanyMapper  companyMapper;

    @Autowired
    private UserMapper  UserMapper;

    public Company initCompany(Long id) throws Exception{
        User user = UserMapper.getDetailById(id);
        Integer companyId=user.getCompanyId();
        if (companyId==null){
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        Company company = companyMapper.selectByPrimaryKey(companyId);
        return company;
    }

    public int updateCompany(Company company){
        Integer i = companyMapper.updateByPrimaryKeySelective(company);
        return i;
    }
}
