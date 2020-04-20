package com.example.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class BankChecking extends AbstractBehavior<BankChecking.Command> {
	
	// actor protocol
	public interface Command {
	}
	
	public static Behavior<BankChecking.Command> create() {
		return Behaviors.setup(BankChecking::new);
	}
	
	private BankChecking(ActorContext<BankChecking.Command> context) {
		super(context);
	}
	
	@Override
	public Receive<BankChecking.Command> createReceive() {
		return newReceiveBuilder()
			.onSignal(PostStop.class, signal -> onPostStop())
			.build();
	}
	
	private BankChecking onPostStop() {
		getContext().getLog().info("BankChecking {} stopped");
		return this;
	}
	
}