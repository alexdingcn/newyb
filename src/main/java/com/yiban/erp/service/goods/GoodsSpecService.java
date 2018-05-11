package com.yiban.erp.service.goods;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.GoodsSpecMapper;
import com.yiban.erp.dto.GoodsSpecForm;
import com.yiban.erp.entities.GoodsSpec;
import com.yiban.erp.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class GoodsSpecService {

    private static final int GOODS_SPEC_CACHE_DAYS = 7;

    @Autowired
    private GoodsSpecMapper specMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public List<GoodsSpecForm> getListForm(Integer companyId) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String cacheValue = operations.get(companyId.toString());
        List<GoodsSpecForm> form = new ArrayList<>();
        if (StringUtils.isNotBlank(cacheValue)) {
            form = JSON.parseArray(cacheValue, GoodsSpecForm.class);
        }
        if (form != null) {
            return form;
        }
        List<GoodsSpec> goodsSpecs = specMapper.getByCompanyId(companyId);
        if (goodsSpecs.isEmpty()) {
            return null;
        }
        //如果不为空值, 则根据数据组织出对应的结果集
        form = transferToForm(goodsSpecs);
        String cacheStr = JSON.toJSONString(form);
        operations.set(companyId.toString(), cacheStr, GOODS_SPEC_CACHE_DAYS, TimeUnit.DAYS);

        return form;
    }

    private List<GoodsSpecForm> transferToForm(List<GoodsSpec> goodsSpecs) {
        Map<Long, GoodsSpecForm> tempMap = new HashMap<>();
        for (GoodsSpec spec : goodsSpecs) {
            if (spec.getParentId() == null) {
                continue;
            }
            if (!tempMap.containsKey(spec.getParentId())) {
                GoodsSpecForm form = new GoodsSpecForm();
                form.setParentId(spec.getParentId());
                form.setParentName(spec.getParentName());
                form.setParentNo(spec.getParentNo());
                List<GoodsSpec> subSpecs = new ArrayList<>();
                subSpecs.add(spec);
                form.setSubGoodsSpecs(subSpecs);

                tempMap.put(spec.getParentId(), form);
            }else {
                GoodsSpecForm form = tempMap.get(spec.getParentId());
                List<GoodsSpec> subSpecs = form.getSubGoodsSpecs();
                subSpecs.add(spec);
            }
        }

        //把map转换换到list
        List<GoodsSpecForm> forms = new ArrayList<>();
        for (Map.Entry<Long, GoodsSpecForm> entry : tempMap.entrySet()) {
            forms.add(entry.getValue());
        }
        return forms;
    }


    public void save(GoodsSpecForm form) throws BizException {


    }
}
