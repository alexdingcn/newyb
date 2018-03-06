package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.UnitMapper;
import com.yiban.erp.entities.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/unit")
public class UnitController {
    private static final Logger logger = LoggerFactory.getLogger(UnitController.class);

    @Autowired
    private UnitMapper unitMapper;

    /**
     * 获取产品的目录树
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list() {
        List<Unit> unitList = unitMapper.selectAll();
        return ResponseEntity.ok().body(JSON.toJSONString(unitList));
    }

    @RequestMapping(value = "/remove/{unitId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@PathVariable Integer unitId) {
        int result = unitMapper.deleteByPrimaryKey(unitId);
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Failed to delete");
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> add(@RequestBody Unit unit) {
        logger.info("ADD new good unit:{}", unit.getName());
        int result = 0;
        if (unit.getId() == null) {
            unit.setCreatedBy("admin");
            unit.setCreatedTime(new Date());
            result = unitMapper.insertSelective(unit);
        } else {
            result = unitMapper.updateByPrimaryKey(unit);
        }
        if (result > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Failed to insert");
    }
}