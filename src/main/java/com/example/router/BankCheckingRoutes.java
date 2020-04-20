package com.example.router;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.http.javadsl.server.Route;
import com.example.actor.BankChecking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.*;

/**
 * This class defined the routes for re-check information operations with Bank (Check balance, check status transaction)
 */
public class BankCheckingRoutes {
	
	private final static Logger log = LoggerFactory.getLogger(BankCheckingRoutes.class);
	private final ActorRef<BankChecking.Command> bankCheckingActor;
	private final Duration askTimeout;
	private final Scheduler scheduler;
	
	public BankCheckingRoutes(ActorSystem<?> system, ActorRef<BankChecking.Command> bankCheckingActor) {
		this.bankCheckingActor = bankCheckingActor;
		scheduler = system.scheduler();
		askTimeout = system.settings().config().getDuration("server.routes.ask-timeout");
	}
	
	public Route bankCheckingRoutes() {
		return concat(
			path("hello", () ->
				get(() ->
					complete("<h1>Say hello to akka-http</h1>"))));
	}
	
}
