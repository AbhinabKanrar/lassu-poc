package com.lassu.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.lassu.common.model.AckType;
import com.lassu.common.model.ReqChkAuthType;
import com.lassu.common.util.CommonConstant;
import com.lassu.common.util.CommonUtil;
import com.lassu.common.util.InternalRestTemplate;

import reactor.bus.Event;
import reactor.fn.Consumer;

@Component
public class AuthDispatcher implements Consumer<Event<ReqChkAuthType>> {

	private Logger _log = LoggerFactory.getLogger(AuthDispatcher.class);
	private static InternalRestTemplate restTemplate = InternalRestTemplate.getInstance();
	private static ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
	
	@Override
	public void accept(Event<ReqChkAuthType> event) {
		ReqChkAuthType reqChkAuthType = event.getData();
		if (reqChkAuthType != null) {
			AckType ackType;
			try {
				System.out.println("target url : " + CommonConstant.TARGET_SITE_URL);
				ackType = restTemplate.postForEntity(CommonConstant.TARGET_SITE_URL, reqChkAuthType, AckType.class).getBody();
				_log.info(writer.writeValueAsString(ackType));
			} catch(Exception e) {
				e.printStackTrace();
				ackType = generateAck(reqChkAuthType);
				try {
					_log.error(writer.writeValueAsString(ackType));
				} catch (JsonProcessingException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
	
	private AckType generateAck(ReqChkAuthType reqChkAuthType) {
		AckType ackType  = new AckType();
		ackType.setApi("ReqChkAuth");
		ackType.setTs(CommonUtil.currentTimeStamp());
		ackType.setReqMsgId(reqChkAuthType.getHead().getMsgId());
		ackType.setStatus("FAILURE");
		ackType.setErrorCd("01");
		return ackType;
	}

}
