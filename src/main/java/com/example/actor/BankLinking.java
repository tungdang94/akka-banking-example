package com.example.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.example.message.BankLinkingMessage;

public class BankLinking extends AbstractBehavior<BankLinkingMessage.Command> {
	
	public static Behavior<BankLinkingMessage.Command> create() {
		return Behaviors.setup(BankLinking::new);
	}
	
	private BankLinking(ActorContext<BankLinkingMessage.Command> context) {
		super(context);
	}
	
	@Override
	public Receive<BankLinkingMessage.Command> createReceive() {
		return newReceiveBuilder()
			.onSignal(PostStop.class, signal -> onPostStop())
			.build();
	}
	
	private BankLinking onPostStop() {
		getContext().getLog().info("BankLinking {} stopped");
		return this;
	}
	
}
