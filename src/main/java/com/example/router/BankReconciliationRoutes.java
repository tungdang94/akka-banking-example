package com.example.router;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.http.javadsl.server.Route;
import com.example.actor.BankReconciliation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static akka.http.javadsl.server.Directives.*;

/**
 * This class defined the routes for reconciliation operations with Bank
 */
public class BankReconciliationRoutes {
	
	private final static Logger log = LoggerFactory.getLogger(BankReconciliationRoutes.class);
	private final ActorRef<BankReconciliation.Command> bankReconciliationActor;
	private final Duration askTimeout;
	private final Scheduler scheduler;
	
	public BankReconciliationRoutes(ActorSystem<?> system, ActorRef<BankReconciliation.Command> bankReconciliationActor) {
		this.bankReconciliationActor = bankReconciliationActor;
		scheduler = system.scheduler();
		askTimeout = system.settings().config().getDuration("server.routes.ask-timeout");
	}
	
	public Route bankReconciliationRoutes() {
		return concat(
			path("hello", () ->
				get(() ->
					complete("<h1>Say hello to akka-http</h1>"))));
	}
	
}
