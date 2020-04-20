package com.example.actor.agribank;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.*;
import com.example.message.BankLinkingMessage;
import com.example.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

public class AgribankLinking extends AbstractBehavior<BankLinkingMessage.Command> {
	
	@AllArgsConstructor
	public static final class AgribankLinkingRequest implements BankLinkingMessage.Command, AgribankConnector.Command {
		public final String requestId;
		public final String requestTime;
		public final String fullName;
		public final String phone;
		public final String supplierCode;
		public final ActorRef<AgribankLinkingResponse> replyTo;
	}
	
	@AllArgsConstructor
	@Builder
	public static final class AgribankLinkingResponse implements BankLinkingMessage.Command {
		public final String requestId;
		public final String responseTime;
		public final String customerCode;
		public final String respCode;
		public final String respDescription;
	}
	
	public static Behavior<BankLinkingMessage.Command> create() {
		return Behaviors.setup(AgribankLinking::new);
	}
	
	private final Duration askTimeout;
	private final Scheduler scheduler;
	
	private AgribankLinking(ActorContext<BankLinkingMessage.Command> context) {
		super(context);
		scheduler = getContext().getSystem().scheduler();
		askTimeout = getContext().getSystem().settings().config().getDuration("server.routes.ask-timeout");
	}
	
	@Override
	public Receive<BankLinkingMessage.Command> createReceive() {
		return newReceiveBuilder()
			.onMessage(BankLinkingMessage.BankLinkingRequest.class, this::handleLinkBankRequest)
			.onSignal(PostStop.class, signal -> onPostStop())
			.build();
	}
	
	private AgribankLinking handleLinkBankRequest(BankLinkingMessage.BankLinkingRequest r) throws Exception {
		getContext().getLog().info("Request to Agribank {} at {}", r.requestId, r.requestTime);
		
		ActorRef<AgribankConnector.Command> connectorActor = getContext().spawn(AgribankConnector.create(), "agribank-connector-linking-" + r.phone);
		
		CompletionStage<AgribankLinkingResponse> stage = AskPattern.ask(
			connectorActor,
			ref -> new AgribankLinking.AgribankLinkingRequest(UUID.randomUUID().toString(), DateUtil.getCurrentDateString(), "Dang Tung", "0969696969", "TMV", ref),
			askTimeout,
			scheduler
		);
		AgribankLinkingResponse agribankResp = stage.toCompletableFuture().get();
		
		// Return Actor level 2
		BankLinkingMessage.BankLinkingResponse bankResp = new BankLinkingMessage.BankLinkingResponse(agribankResp.requestId, agribankResp.responseTime,
			agribankResp.customerCode, agribankResp.respCode, agribankResp.respDescription);
		r.replyTo.tell(bankResp);
		
		return this;
	}
	
	private AgribankLinking onPostStop() {
		getContext().getLog().info("AgribankLinking {} stopped");
		return this;
	}
	
}