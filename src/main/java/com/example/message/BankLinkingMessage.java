package com.example.message;

import akka.actor.typed.ActorRef;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

public class BankLinkingMessage {
	
	// actor protocol
	public interface Command {
	}
	
	@AllArgsConstructor
	public static final class BankLinkingRequest implements Command {
		public final String requestId;
		public final String requestTime;
		public final String fullName;
		public final String phone;
		public final String bankCode;
		public final ActorRef<BankLinkingResponse> replyTo;
		
		public BankLinkingRequest(LinkBankInfo info, ActorRef<BankLinkingResponse> ref) {
			this.requestId = info.requestId;
			this.requestTime = info.requestTime;
			this.fullName = info.fullName;
			this.phone = info.phone;
			this.bankCode = info.bankCode;
			this.replyTo  = ref;
		}
	}
	
	public static final class LinkBankInfo {
		public final String requestId;
		public final String requestTime;
		public final String fullName;
		public final String phone;
		public final String bankCode;
		
		@JsonCreator
		public LinkBankInfo(@JsonProperty("requestId") String requestId, @JsonProperty("requestTime") String requestTime, @JsonProperty("fullName") String fullName,
												@JsonProperty("phone") String phone, @JsonProperty("bankCode") String bankCode) {
			this.requestId = requestId;
			this.requestTime = requestTime;
			this.fullName = fullName;
			this.phone = phone;
			this.bankCode = bankCode;
		}
	}
	
	public static final class BankLinkingResponse implements Command {
		public final String requestId;
		public final String responseTime;
		public final String customerCode;
		public final String bankCode;
		public final String bankDescription;
		
		@JsonCreator
		public BankLinkingResponse(@JsonProperty("requestId") String requestId, @JsonProperty("responseTime") String responseTime, @JsonProperty("customerCode") String customerCode,
															 @JsonProperty("bankCode") String bankCode, @JsonProperty("bankDescription") String bankDescription) {
			this.requestId = requestId;
			this.responseTime = responseTime;
			this.customerCode = customerCode;
			this.bankCode = bankCode;
			this.bankDescription = bankDescription;
		}
	}
	
}
