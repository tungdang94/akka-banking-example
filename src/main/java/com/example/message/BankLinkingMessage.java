package com.example.message;

import akka.actor.typed.ActorRef;
import lombok.AllArgsConstructor;

public class BankLinkingMessage {
	
	// actor protocol
	public interface Command {
	}
	
	@AllArgsConstructor
	public static final class BankLinkingRequest implements Command {
		public final long requestId;
		public final String requestTime;
		public final String fullName;
		public final String phone;
		public final String bankCode;
		public final String type;
		public final ActorRef<BankLinkingResponse> replyTo;
	}
	
	@AllArgsConstructor
	public static final class BankLinkingResponse implements Command {
		public final long requestId;
		public final String responseTime;
		public final String customerCode;
		public final String bankCode;
		public final String bankDescription;
	}
	
}
