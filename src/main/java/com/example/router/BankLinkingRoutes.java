package com.example.router;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import com.example.message.BankLinkingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

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
	
	private CompletionStage<BankLinkingMessage.BankLinkingResponse> linkBank(BankLinkingMessage.LinkBankInfo info) {
		return AskPattern.ask(bankLinkingActor, ref -> new BankLinkingMessage.BankLinkingRequest(info, ref), askTimeout, scheduler);
	}
	
	public Route bankLinkingRoutes() {
		return pathPrefix("link-bank", () ->
			concat(
				pathEnd(() ->
					concat(
						get(() ->
							complete("<h1>Say hello to akka-http</h1>")
						),
						post(() ->
							entity(
								Jackson.unmarshaller(BankLinkingMessage.LinkBankInfo.class),
								info ->
									onSuccess(linkBank(info), performed -> {
										log.info("Link bank success: {}", performed.customerCode);
										return complete(StatusCodes.CREATED, performed, Jackson.marshaller());
									})
							)
						)
					)
				)
			)
		);
	}
	
}
