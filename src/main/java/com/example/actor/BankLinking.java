package com.example.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.*;
import com.example.actor.agribank.AgribankLinking;
import com.example.message.BankLinkingMessage;
import com.example.util.DateUtil;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class BankLinking extends AbstractBehavior<BankLinkingMessage.Command> {
	
	public static Behavior<BankLinkingMessage.Command> create() {
		return Behaviors.setup(BankLinking::new);
	}
	
	private final Duration askTimeout;
	private final Scheduler scheduler;
	
	private BankLinking(ActorContext<BankLinkingMessage.Command> context) {
		super(context);
		scheduler = getContext().getSystem().scheduler();
		askTimeout = getContext().getSystem().settings().config().getDuration("server.routes.ask-timeout");
		
		context.getLog().info("BankLinking actor started...");
	}
	
	@Override
	public Receive<BankLinkingMessage.Command> createReceive() {
		return newReceiveBuilder()
			.onMessage(BankLinkingMessage.BankLinkingRequest.class, this::forwardToBank)
			.onSignal(PostStop.class, signal -> onPostStop())
			.build();
	}
	
	private BankLinking forwardToBank(BankLinkingMessage.BankLinkingRequest r) throws ExecutionException, InterruptedException {
		ActorRef<BankLinkingMessage.Command> agribankLinkingActor = getContext().spawn(AgribankLinking.create(), "agribank-linking");
		CompletionStage<BankLinkingMessage.BankLinkingResponse> stage = AskPattern.ask(
			agribankLinkingActor,
			ref -> new BankLinkingMessage.BankLinkingRequest(UUID.randomUUID().toString(), DateUtil.getCurrentDateString(), "Dang Tung", "0969696969", "AGRIBANK", ref),
			askTimeout,
			scheduler
		);
		BankLinkingMessage.BankLinkingResponse bankResp = stage.toCompletableFuture().get();
		r.replyTo.tell(bankResp);
		
		return this;
	}
	
	private BankLinking onPostStop() {
		getContext().getLog().info("BankLinking {} stopped");
		return this;
	}
	
}
