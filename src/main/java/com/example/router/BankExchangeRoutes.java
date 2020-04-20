package com.example.router;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.http.javadsl.server.Route;
import com.example.actor.BankExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.*;

/**
 * This class defined the routes for money exchange operations with Bank (CashIn, CashOut)
 */
public class BankExchangeRoutes {
	
	private final static Logger log = LoggerFactory.getLogger(BankExchangeRoutes.class);
	private final ActorRef<BankExchange.Command> bankExchangeActor;
	private final Duration askTimeout;
	private final Scheduler scheduler;
	
	public BankExchangeRoutes(ActorSystem<?> system, ActorRef<BankExchange.Command> bankExchangeActor) {
		this.bankExchangeActor = bankExchangeActor;
		scheduler = system.scheduler();
		askTimeout = system.settings().config().getDuration("server.routes.ask-timeout");
	}
	
	public Route bankLinkingRoutes() {
		return concat(
			path("hello", () ->
				get(() ->
					complete("<h1>Say hello to akka-http</h1>"))));
	}
	
}
