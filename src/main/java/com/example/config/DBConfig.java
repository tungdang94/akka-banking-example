package com.example.config;

import akka.stream.alpakka.slick.javadsl.SlickSession;

public class DBConfig {
	
	// #init-session
	public static final SlickSession DB_SESSION = SlickSession.forConfig("slick-mysql");
	// #init-session
	
}
