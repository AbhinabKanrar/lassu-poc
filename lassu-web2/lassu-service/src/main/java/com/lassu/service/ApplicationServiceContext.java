/**
 * 
 */
package com.lassu.service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.lassu.dao.ApplicationDaoContext;

/**
 * @author abhinab
 *
 */
@Configuration
@ComponentScan(basePackages="com.lassu.service")
@Import(ApplicationDaoContext.class)
public class ApplicationServiceContext {

}
