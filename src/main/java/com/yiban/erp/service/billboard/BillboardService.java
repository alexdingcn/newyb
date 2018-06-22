package com.yiban.erp.service.billboard;

import com.yiban.erp.dao.BillboardMapper;
import com.yiban.erp.entities.Billboard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillboardService {

    @Autowired
    private BillboardMapper billboardMapper;

    public List<Billboard> getList(Integer companyId){
        return billboardMapper.getList(companyId);
    }
    public int insert(Billboard billboard){
       return billboardMapper.insert(billboard);
    }
    public int update(Billboard billboard){
        return billboardMapper.update(billboard);
    }
    public int delete(int id){
        return billboardMapper.delete(id);
    }
    public int sort(int id,int number){
            return billboardMapper.sort(id, number);
    }

    public List<Billboard> display(Integer companyId){
        return billboardMapper.display(companyId);
    }
}
