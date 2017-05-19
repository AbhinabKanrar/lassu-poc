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
import com.lassu.security.common.model.ReqChkAuthType;
import com.lassu.security.common.util.CommonUtil;
import com.lassu.security.service.user.UserService;

@RestController
@RequestMapping("/inter-process-communication")
public class MessageDispatcherRouter {

	@Autowired
	private UserService userService;
	
	@PostMapping(value = "/reqChkAuth", headers = "Accept=application/xml", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody ResponseEntity<AckType> reqChkAuth(final @RequestBody ReqChkAuthType reqChkAuthType) {
		AckType ackType = new AckType();
		if (!isValid(reqChkAuthType)) {
			ackType = createErrorAck(reqChkAuthType, ackType);
			return new ResponseEntity<AckType>(ackType, HttpStatus.OK);
		}
		userService.initiateReqChkAuthAudit(reqChkAuthType);
		return new ResponseEntity<AckType>(HttpStatus.OK);
	}
	
	public AckType createErrorAck(ReqChkAuthType reqChkAuthType, AckType ackType) {
		ackType.setApi("reqChkAuth");
		if (reqChkAuthType != null && reqChkAuthType.getHead() != null) {
			ackType.setReqMsgId(reqChkAuthType.getHead().getMsgId());
		} else {
			ackType.setReqMsgId("XXX");
		}
		ackType.setStatus("FAILURE");
		ackType.setTs(CommonUtil.currentTimeStamp());
		ackType.setErrorCd("ZA");
		return ackType;
	}
	
	public AckType createSuccessAck(ReqChkAuthType reqChkAuthType, AckType ackType) {
		ackType.setApi("reqChkAuth");
		ackType.setReqMsgId(reqChkAuthType.getHead().getMsgId());
		ackType.setStatus("SUCCESS");
		ackType.setTs(CommonUtil.currentTimeStamp());
		return ackType;
	}
	
	private boolean isValid(ReqChkAuthType reqChkAuthType) {
		if (reqChkAuthType != null) {
			if (isHeadValid(reqChkAuthType.getHead())) {
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
