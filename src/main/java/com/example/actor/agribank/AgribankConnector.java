package com.example.actor.agribank;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.example.util.DateUtil;

import java.util.UUID;

public class AgribankConnector extends AbstractBehavior<AgribankConnector.Command> {
	
	// actor protocol
	public interface Command {
	}
	
	public static Behavior<AgribankConnector.Command> create() {
		return Behaviors.setup(AgribankConnector::new);
	}
	
	private AgribankConnector(ActorContext<AgribankConnector.Command> context) {
		super(context);
	}
	
	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder()
			.onMessage(AgribankLinking.AgribankLinkingRequest.class, this::linkBank)
			.onSignal(PostStop.class, signal -> onPostStop())
			.build();
	}
	
	private Behavior<Command> linkBank(AgribankLinking.AgribankLinkingRequest r) {
		getContext().getLog().info("Request to Agribank after convert {} at {}", r.requestId, r.requestTime);
		
		// TODO using Client API of akka-http and process response...
		
		r.replyTo.tell(new AgribankLinking.AgribankLinkingResponse(UUID.randomUUID().toString(), DateUtil.getCurrentDateString(),
			"CODE", "00", "Sucesss"));
		return this;
	}
	
	private AgribankConnector onPostStop() {
		getContext().getLog().info("AgribankConnector {} stopped");
		return this;
	}
	
}