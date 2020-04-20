package com.example.actor.agribank;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.example.message.BankLinkingMessage;
import com.example.util.DateUtil;
import lombok.AllArgsConstructor;

import java.util.Calendar;
import java.util.UUID;

public class AgribankLinking extends AbstractBehavior<BankLinkingMessage.Command> {
	
	@AllArgsConstructor
	public static final class AgribankLinkingRequest implements BankLinkingMessage.Command, AgribankConnector.Command {
		final String requestId;
		final String requestTime;
		final String fullName;
		final String phone;
		final String supplierCode;
		final ActorRef<AgribankLinkingResponse> replyTo;
	}
	
	@AllArgsConstructor
	public static final class AgribankLinkingResponse implements BankLinkingMessage.Command {
		final String requestId;
		final String responseTime;
		final String customerCode;
		final String respCode;
		final String respDescription;
	}
	
	public static Behavior<BankLinkingMessage.Command> create() {
		return Behaviors.setup(AgribankLinking::new);
	}
	
	private AgribankLinking(ActorContext<BankLinkingMessage.Command> context) {
		super(context);
	}
	
	@Override
	public Receive<BankLinkingMessage.Command> createReceive() {
		return newReceiveBuilder()
			.onMessage(BankLinkingMessage.BankLinkingRequest.class, this::handleLinkBankRequest)
			//.onMessage(AgribankLinkingResponse.class, this::handleLinkBankResponse)
			.onSignal(PostStop.class, signal -> onPostStop())
			.build();
	}
	
	private AgribankLinking handleLinkBankRequest(BankLinkingMessage.BankLinkingRequest r) {
		getContext().getLog().info("Request to Agribank {} at {}", r.requestId, r.requestTime);
		
		ActorRef<AgribankConnector.Command> connectorActor = getContext().spawn(AgribankConnector.create(), "agribank-connector-linking-" + r.phone);
		connectorActor.tell(new AgribankLinking.AgribankLinkingRequest(UUID.randomUUID().toString(), DateUtil.toDateString(Calendar.getInstance().getTime(), DateUtil.DATE_FORMAT_1),
			"Dang Tung", "0969696969", "TMV", getContext().getSelf().unsafeUpcast()));
		
		return this;
	}
	
	/*private AgribankLinking handleLinkBankResponse(AgribankLinkingResponse resp) {
		getContext().getLog().info("Response from Agribank {} at {}", r.requestId, r.requestTime);
	}*/
	
	private AgribankLinking onPostStop() {
		getContext().getLog().info("AgribankLinking {} stopped");
		return this;
	}
	
}