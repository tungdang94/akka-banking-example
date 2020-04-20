package com.example.actor;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class BankReconciliation extends AbstractBehavior<BankReconciliation.Command> {
	
	// actor protocol
	public interface Command {
	}
	
	public static Behavior<BankReconciliation.Command> create() {
		return Behaviors.setup(BankReconciliation::new);
	}
	
	private BankReconciliation(ActorContext<BankReconciliation.Command> context) {
		super(context);
	}
	
	@Override
	public Receive<BankReconciliation.Command> createReceive() {
		return newReceiveBuilder()
			.onSignal(PostStop.class, signal -> onPostStop())
			.build();
	}
	
	private BankReconciliation onPostStop() {
		getContext().getLog().info("BankReconciliation {} stopped");
		return this;
	}
	
}