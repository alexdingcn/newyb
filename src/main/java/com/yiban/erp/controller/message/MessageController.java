package com.yiban.erp.controller.message;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.constant.MessageOptionStatus;
import com.yiban.erp.dto.MessageListReq;
import com.yiban.erp.dto.MessageProcessReq;
import com.yiban.erp.dto.MessageResp;
import com.yiban.erp.entities.MessageInfo;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getList(@RequestBody MessageListReq req,
                                          @AuthenticationPrincipal User user) throws Exception {
        MessageResp resp = messageService.getMessageList(user, req.getStartDate(), req.getEndDate());
        return ResponseEntity.ok().body(JSON.toJSONString(resp));
    }

    @RequestMapping(value = "/process", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> process(@RequestBody MessageProcessReq req,
                                          @AuthenticationPrincipal User user) throws Exception {
        MessageOptionStatus status = MessageOptionStatus.getByName(req.getStatus());
        MessageInfo messageInfo = messageService.process(req.getMessageId(), status, req.getOptionResult(), user);
        return ResponseEntity.ok().body(JSON.toJSONString(messageInfo));
    }
}
