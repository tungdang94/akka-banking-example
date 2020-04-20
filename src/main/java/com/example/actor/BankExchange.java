package com.example.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class BankExchange extends AbstractBehavior<BankExchange.Command> {
	
	// actor protocol
	public interface Command {
	}
	
	public static Behavior<BankExchange.Command> create() {
		return Behaviors.setup(BankExchange::new);
	}
	
	private BankExchange(ActorContext<BankExchange.Command> context) {
		super(context);
	}
	
	@Override
	public Receive<BankExchange.Command> createReceive() {
		return newReceiveBuilder()
			.onSignal(PostStop.class, signal -> onPostStop())
			.build();
	}
	
	private BankExchange onPostStop() {
		getContext().getLog().info("BankExchange {} stopped");
		return this;
	}
	
}