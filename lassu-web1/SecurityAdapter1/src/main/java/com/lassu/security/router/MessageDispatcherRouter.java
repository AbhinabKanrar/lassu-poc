package com.lassu.security.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lassu.security.common.model.AckType;
import com.lassu.security.common.model.HeadType;
import com.lassu.security.common.model.RespChkAuthType;
import com.lassu.security.common.util.CommonUtil;
import com.lassu.security.service.user.UserService;

@RestController
@RequestMapping("/inter-process-communication")
public class MessageDispatcherRouter {

	@Autowired
	private UserService userService;
	
	@PostMapping(value = "/respChkAuth", headers = "Accept=application/xml", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody ResponseEntity<AckType> respChkAuth(final @RequestBody RespChkAuthType respChkAuthType) {
		AckType ackType = new AckType();
		if (!isValid(respChkAuthType)) {
			ackType = createErrorAck(respChkAuthType, ackType);
			return new ResponseEntity<AckType>(ackType, HttpStatus.OK);
		}
		userService.initiateRespChkAuthAudit(respChkAuthType);
		return new ResponseEntity<AckType>(HttpStatus.OK);
	}
	
	public AckType createErrorAck(RespChkAuthType respChkAuthType, AckType ackType) {
		ackType.setApi("respChkAuth");
		if (respChkAuthType != null && respChkAuthType.getHead() != null) {
			ackType.setReqMsgId(respChkAuthType.getHead().getMsgId());
		} else {
			ackType.setReqMsgId("XXX");
		}
		ackType.setStatus("FAILURE");
		ackType.setTs(CommonUtil.currentTimeStamp());
		ackType.setErrorCd("ZA");
		return ackType;
	}
	
	public AckType createSuccessAck(RespChkAuthType respChkAuthType, AckType ackType) {
		ackType.setApi("respChkAuth");
		ackType.setReqMsgId(respChkAuthType.getHead().getMsgId());
		ackType.setStatus("SUCCESS");
		ackType.setTs(CommonUtil.currentTimeStamp());
		return ackType;
	}
	
	private boolean isValid(RespChkAuthType respChkAuthType) {
		if (respChkAuthType != null) {
			if (isHeadValid(respChkAuthType.getHead())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isHeadValid(HeadType headType) {
		if (headType != null && headType.getMsgId() != null && headType.getOrg() != null) {
			return true;
		}
		return false;
	}
	
}
