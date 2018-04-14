package com.yiban.erp.service.message;

import com.yiban.erp.constant.MessageOptionStatus;
import com.yiban.erp.constant.MessageStatus;
import com.yiban.erp.constant.UserRoleType;
import com.yiban.erp.dao.MessageInfoMapper;
import com.yiban.erp.dao.MessageOptionMapper;
import com.yiban.erp.dao.UserRoleMapper;
import com.yiban.erp.dto.MessageResp;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService {

    private static final String ALL_ROLE = "ALL"; //如果消息没有定义有具体的用户角色，则针对所有用户可见
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageInfoMapper messageInfoMapper;
    @Autowired
    private MessageOptionMapper messageOptionMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 创建一个消息
     * @param title 消息标题（必须）
     * @param content 消息内容
     * @param roleType 可以看到该消息的用户角色
     * @param user 创建消息的用户，
     * @throws BizException
     */
    public void create(String title, String content, UserRoleType roleType, User user) {
        if (StringUtils.isBlank(title)) {
            logger.warn("message title can not null");
            throw new BizRuntimeException(ErrorCode.PARAMETER_MISSING);
        }
        MessageInfo message = new MessageInfo();
        message.setCompanyId(user.getCompanyId());
        message.setRoleType(roleType == null ? ALL_ROLE : roleType.name());
        message.setTitle(title);
        message.setContent(content);
        message.setStatus(MessageStatus.NORMAL.name());
        message.setCreateBy(user.getNickname());
        message.setCreateTime(new Date());
        messageInfoMapper.insert(message);
    }


    /**
     * 处理一个消息
     * @param messageId 消息编号
     * @param optionStatus 处理状态
     * @param optionResult 处理意见
     * @param user 提交人
     * @return
     * @throws BizException
     */
    public MessageInfo process(Long messageId, MessageOptionStatus optionStatus, String optionResult, User user) throws BizException {
        MessageInfo messageInfo = messageInfoMapper.selectByPrimaryKey(messageId);
        if (messageInfo == null) {
            logger.warn("get message by id result is null, messageId:{}", messageId);
            throw new BizException(ErrorCode.MESSAGE_GET_FAIL);
        }
        //直接加一条处理意见信息
        MessageOption option = new MessageOption();
        option.setMessageId(messageId);
        option.setUserId(user.getId());
        option.setOptionStatus(optionStatus == null ? MessageOptionStatus.UNPROCESS.name() : optionStatus.name());
        option.setOptionResult(optionResult);
        option.setCreateBy(user.getNickname());
        option.setCreateTime(new Date());
        messageOptionMapper.insert(option);

        List<MessageOption> options = messageOptionMapper.getByMessageId(messageId);
        messageInfo.setOptions(options);
        return messageInfo;
    }

    /**
     * 获取消息列表
     * @param user
     * @param startTime 查询消息的时间范围,默认取当前最近一个月
     * @param endTime
     * @return
     */
    public MessageResp getMessageList(User user, Date startTime, Date endTime) {
        Date beginTime = startTime;
        Date lastTime = endTime;
        if (startTime == null || endTime == null) {
            logger.debug("startTime or endTime is null, get last month data");
            lastTime = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastTime);
            calendar.add(Calendar.MONTH, -1);
            beginTime = calendar.getTime();
        }
        List<UserRole> userRoles = userRoleMapper.findByUserId(user.getId());
        List<String> roleTypeList = new ArrayList<>();
        roleTypeList.add(ALL_ROLE); //所有人都能看到的
        if (userRoles != null && !userRoles.isEmpty()) {
            userRoles.stream().forEach(item -> roleTypeList.add(item.getName()));
        }
        List<MessageInfo> messageInfos = messageInfoMapper.query(user.getCompanyId(), roleTypeList, beginTime, lastTime);
        MessageResp resp = new MessageResp();
        if (messageInfos == null || messageInfos.isEmpty()) {
            resp.setUnProcessCount(0);
            resp.setProcessedCount(0);
            resp.setProcessedList(Collections.emptyList());
            resp.setUnProcessList(Collections.emptyList());
            return resp;
        }

        List<MessageInfo> unprocessList = new ArrayList<>();
        List<MessageInfo> processedList = new ArrayList<>();
        for (MessageInfo info : messageInfos) {
            if (MessageOptionStatus.PROCESSED.equals(info.userMessageStatus(user.getId()))) {
                processedList.add(info);
            }
            else {
                unprocessList.add(info);
            }
        }
        resp.setUnProcessList(unprocessList);
        resp.setUnProcessCount(unprocessList.size());
        resp.setProcessedList(processedList);
        resp.setProcessedCount(processedList.size());

        return resp;
    }



}
