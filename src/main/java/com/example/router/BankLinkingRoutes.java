package com.example.router;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.http.javadsl.server.Route;
import com.example.message.BankLinkingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.*;

/**
 * This class defined the routes for integrating customer wallet operations with Bank (Link, Unlink)
 */
public class BankLinkingRoutes {
	
	private final static Logger log = LoggerFactory.getLogger(BankLinkingRoutes.class);
	private final ActorRef<BankLinkingMessage.Command> bankLinkingActor;
	private final Duration askTimeout;
	private final Scheduler scheduler;
	
	public BankLinkingRoutes(ActorSystem<?> system, ActorRef<BankLinkingMessage.Command> bankLinkingActor) {
		this.bankLinkingActor = bankLinkingActor;
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
