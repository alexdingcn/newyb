package com.yiban.erp.service.goods;

import com.yiban.erp.dao.GoodsAttributeMapper;
import com.yiban.erp.dto.GoodsAttForm;
import com.yiban.erp.entities.GoodsAttribute;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GoodsAttrService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsAttrService.class);

    @Autowired
    private GoodsAttributeMapper goodsAttributeMapper;

    public List<GoodsAttribute> getGoodsAttList(Integer companyId) {
        List<GoodsAttribute> attributes = goodsAttributeMapper.getByCompanyId(companyId);
        return attributes;
    }

    public void save(GoodsAttForm goodsAttForm, User user) throws BizException {
        List<GoodsAttribute> attributes = goodsAttForm.getAttributes();
        if (attributes == null || attributes.isEmpty()) {
            return;
        }
        if (attributes.size() > 6) {
            logger.warn("goods attribute max length is 6.");
            throw new BizException(ErrorCode.GOODS_ATTRIBUTE_SIZE_ERROR);
        }
        for (GoodsAttribute attribute : attributes) {
            if (attribute.getId() != null) {
                //已经存在的，进行修改
                attribute.setUpdatedBy(user.getNickname());
                attribute.setUpdatedTime(new Date());
                goodsAttributeMapper.updateByPrimaryKeySelective(attribute);
            }else {
                attribute.setCompanyId(user.getCompanyId());
                attribute.setCreatedBy(user.getNickname());
                attribute.setCreatedTime(new Date());
                goodsAttributeMapper.insert(attribute);
            }
        }
        return;

    }
}
