package com.lassu.common;

import java.sql.Timestamp;
import java.util.Date;

public class Test {

	public static void main(String[] args) {
		System.out.println((new Timestamp(new Date().getTime())).toString());
	}

}
