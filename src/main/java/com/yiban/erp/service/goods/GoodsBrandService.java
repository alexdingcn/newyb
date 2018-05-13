package com.yiban.erp.service.goods;

import com.yiban.erp.dao.GoodsBrandMapper;
import com.yiban.erp.dto.GoodsBrandQuery;
import com.yiban.erp.entities.GoodsBrand;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class GoodsBrandService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsBrandService.class);

    @Autowired
    private GoodsBrandMapper goodsBrandMapper;


    public int getBrandCount(GoodsBrandQuery query) {
        return goodsBrandMapper.getBrandCount(query);
    }

    public List<GoodsBrand> getBrands(GoodsBrandQuery query) {
        return goodsBrandMapper.getBrands(query);
    }


    @Transactional
    public void save(GoodsBrand goodsBrand, User user) throws BizException {
        if (goodsBrand == null || StringUtils.isEmpty(goodsBrand.getBrandName())) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        if (goodsBrand.getId() != null) {
            logger.info("user:{} update goods brand.", user.getId());
            goodsBrand.setUpdatedBy(user.getNickname());
            goodsBrand.setUpdatedTime(new Date());
            goodsBrandMapper.updateByPrimaryKeySelective(goodsBrand);
        }else {
            logger.info("user:{} created goods brand.");
            goodsBrand.setCompanyId(user.getCompanyId());
            goodsBrand.setCreatedBy(user.getNickname());
            goodsBrand.setCreatedTime(new Date());
            if (goodsBrand.getEnabled() == null){
                goodsBrand.setEnabled(true);
            }
            goodsBrandMapper.insert(goodsBrand);
        }
    }

    @Transactional
    public void remove(Long brandId) throws BizException {
        //验证是否有商品正在使用该品牌
        boolean isUsed = goodsBrandMapper.isBrandUsed(brandId);
        if (isUsed) {
            logger.warn("brand:{} is have goods used, can not remove.", brandId);
            throw new BizException(ErrorCode.GOODS_BRAND_USED_CANNOT_REMOVE);
        }
        //直接删除
        goodsBrandMapper.deleteByPrimaryKey(brandId);
    }
}
