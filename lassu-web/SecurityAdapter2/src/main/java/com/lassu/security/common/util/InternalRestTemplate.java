/**
 * 
 */
package com.lassu.security.common.util;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author abhinab
 *
 */
public final class InternalRestTemplate {

	private final RestTemplate restTemplate;

	public InternalRestTemplate() {
		this.restTemplate = new RestTemplate(clientHttpRequestFactory());
	}

	private ClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(10000);
		factory.setConnectTimeout(10000);
		factory.setBufferRequestBody(false);
		return factory;
	}

	public static InternalRestTemplate getInstance() {
		return new InternalRestTemplate();
	}

	public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType,
			Object... uriVariables) {
		return restTemplate.postForEntity(url, request, responseType, uriVariables);
	}

	public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType) {
		return restTemplate.postForEntity(url, request, responseType);
	}

	public <T> T postForObject(String url, Object request, Class<T> responseType) {
		return restTemplate.postForObject(url, request, responseType);
	}

	public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType) {
		return restTemplate.getForEntity(url, responseType);
	}

}
