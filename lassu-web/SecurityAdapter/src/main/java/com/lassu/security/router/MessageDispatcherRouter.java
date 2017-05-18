package com.lassu.security.router;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lassu.security.common.model.AckType;
import com.lassu.security.common.model.ReqChkAuthType;

@RestController
@RequestMapping("/inter-process-communication")
public class MessageDispatcherRouter {

	@PostMapping(value = "/reqChkAuth", headers = "Accept=application/xml", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody ResponseEntity<AckType> reqChkAuth(final @RequestBody ReqChkAuthType reqChkAuthType) {
		System.out.println(reqChkAuthType.toString());
		return new ResponseEntity<AckType>(HttpStatus.OK);
	}
	
	
}
