/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.dashboard.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.rocketmq.common.Pair;
import org.apache.rocketmq.dashboard.model.MessagePage;
import org.apache.rocketmq.dashboard.model.MessageView;
import org.apache.rocketmq.dashboard.model.request.MessageQuery;
import org.apache.rocketmq.dashboard.permisssion.Permission;
import org.apache.rocketmq.dashboard.service.MessageService;
import org.apache.rocketmq.dashboard.util.JsonUtil;
import org.apache.rocketmq.remoting.protocol.body.ConsumeMessageDirectlyResult;
import org.apache.rocketmq.tools.admin.api.MessageTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/message")
@Permission
public class MessageController {
    private Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Resource
    private MessageService messageService;

    @GetMapping("/viewMessage.query")
    @ResponseBody
    public Object viewMessage(@RequestParam(required = false) String topic, @RequestParam String msgId) {
        Map<String, Object> messageViewMap = Maps.newHashMap();
        Pair<MessageView, List<MessageTrack>> messageViewListPair = messageService.viewMessage(topic, msgId);
        messageViewMap.put("messageView", messageViewListPair.getObject1());
        messageViewMap.put("messageTrackList", messageViewListPair.getObject2());
        return messageViewMap;
    }

    @PostMapping("/queryMessagePageByTopic.query")
    @ResponseBody
    public MessagePage queryMessagePageByTopic(@RequestBody MessageQuery query) {
        return messageService.queryMessageByPage(query);
    }

    @GetMapping("/queryMessageByTopicAndKey.query")
    @ResponseBody
    public Object queryMessageByTopicAndKey(@RequestParam String topic, @RequestParam String key) {
        return messageService.queryMessageByTopicAndKey(topic, key);
    }

    @GetMapping("/queryMessageByTopic.query")
    @ResponseBody
    public Object queryMessageByTopic(@RequestParam String topic, @RequestParam long begin,
                                      @RequestParam long end) {
        return messageService.queryMessageByTopic(topic, begin, end);
    }

    @PostMapping("/consumeMessageDirectly.do")
    @ResponseBody
    public Object consumeMessageDirectly(@RequestParam String topic, @RequestParam String consumerGroup,
                                         @RequestParam String msgId,
                                         @RequestParam(required = false) String clientId) {
        logger.info("msgId={} consumerGroup={} clientId={}", msgId, consumerGroup, clientId);
        ConsumeMessageDirectlyResult consumeMessageDirectlyResult = messageService.consumeMessageDirectly(topic, msgId, consumerGroup, clientId);
        logger.info("consumeMessageDirectlyResult={}", JsonUtil.obj2String(consumeMessageDirectlyResult));
        return consumeMessageDirectlyResult;
    }
}
