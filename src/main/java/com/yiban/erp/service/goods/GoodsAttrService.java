package com.yiban.erp.service.goods;

import com.yiban.erp.dao.GoodsAttributeMapper;
import com.yiban.erp.dto.GoodsAttForm;
import com.yiban.erp.entities.GoodsAttribute;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsAttrService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsAttrService.class);

    @Autowired
    private GoodsAttributeMapper goodsAttributeMapper;

    public List<GoodsAttribute> getGoodsAttList(Integer companyId) {
        List<GoodsAttribute> attributes = goodsAttributeMapper.getByCompanyId(companyId);
        return attributes;
    }

    public Map<Long, GoodsAttribute> getGoodsAttMap(Integer companyId) {
        List<GoodsAttribute> attributes = getGoodsAttList(companyId);
        Map<Long, GoodsAttribute> attributeMap = new HashMap<>();
        if (attributes == null || attributes.isEmpty()) {
            return attributeMap;
        }
        attributes.stream().forEach(item -> attributeMap.put(item.getId(), item));
        return attributeMap;
    }

    public void save(GoodsAttribute attribute, User user) throws BizException {
        if (attribute == null || StringUtils.isEmpty(attribute.getAttName())) {
            throw new BizException(ErrorCode.GOODS_ATTRIBUTE_NAME_ERROR);
        }
        if (attribute.getId() == null) {
            //新建
            attribute.setCompanyId(user.getCompanyId());
            attribute.setCreatedTime(new Date());
            attribute.setCreatedBy(user.getNickname());
            goodsAttributeMapper.insert(attribute);
        }else {
            attribute.setUpdatedTime(new Date());
            attribute.setUpdatedBy(user.getNickname());
            goodsAttributeMapper.updateByPrimaryKeySelective(attribute);
        }
    }

    public void remove(Long id, User user) throws BizException {
        GoodsAttribute attribute = goodsAttributeMapper.selectByPrimaryKey(id);
        if (attribute == null || !attribute.getCompanyId().equals(user.getCompanyId())) {
            throw new BizException(ErrorCode.GOODS_ATTRIBUTE_GET_FAIL);
        }
        //验证下是否使用了，如果使用了，不能删除
        boolean isUsed = goodsAttributeMapper.isUsedAttribute(id);
        if (isUsed) {
            throw new BizException(ErrorCode.GOODS_ATTRIBUTE_USED);
        }
        logger.info("user:{} delete goods attribute:{}", user.getId(), id);
        goodsAttributeMapper.deleteByPrimaryKey(id);

    }
}
