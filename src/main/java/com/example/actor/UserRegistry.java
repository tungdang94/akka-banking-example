package com.example.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserRegistry extends AbstractBehavior<UserRegistry.Command> {
	
	// actor protocol
	public interface Command {
	}
	
	@AllArgsConstructor
	public static final class GetUsers implements Command {
		public final ActorRef<Users> replyTo;
	}
	
	@AllArgsConstructor
	public static final class GetUser implements Command {
		public final String name;
		public final ActorRef<GetUserResponse> replyTo;
	}
	
	@AllArgsConstructor
	public static final class GetUserResponse {
		public final Optional<User> maybeUser;
	}
	
	@AllArgsConstructor
	public static final class CreateUser implements Command {
		public final User user;
		public final ActorRef<ActionPerformed> replyTo;
	}
	
	@AllArgsConstructor
	public static final class DeleteUser implements Command {
		public final String name;
		public final ActorRef<ActionPerformed> replyTo;
	}
	
	@AllArgsConstructor
	public static final class ActionPerformed implements Command {
		public final String description;
	}
	
	// #user-case-classes
	public static final class User {
		public final String name;
		public final int age;
		public final String countryOfResidence;
		
		@JsonCreator
		public User(@JsonProperty("name") String name, @JsonProperty("age") int age, @JsonProperty("countryOfRecidence") String countryOfResidence) {
			this.name = name;
			this.age = age;
			this.countryOfResidence = countryOfResidence;
		}
	}
	
	@AllArgsConstructor
	public final static class Users {
		public final List<User> users;
	}
	// #user-case-classes
	
	private final List<User> users = new ArrayList<>();
	
	private UserRegistry(ActorContext<Command> context) {
		super(context);
	}
	
	public static Behavior<Command> create() {
		return Behaviors.setup(UserRegistry::new);
	}
	
	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder()
			.onMessage(CreateUser.class, this::onCreateUser)
			.onMessage(GetUser.class, this::onGetUser)
			.onMessage(GetUsers.class, this::onGetUsers)
			.onMessage(DeleteUser.class, this::onDeleteUser)
			.build();
	}
	
	private Behavior<Command> onCreateUser(CreateUser command) {
		users.add(command.user);
		command.replyTo.tell(new ActionPerformed(String.format("User %s created.", command.user.name)));
		return this;
	}
	
	private Behavior<Command> onGetUser(GetUser command) {
		Optional<User> maybeUser = users.stream()
			.filter(user -> user.name.equals(command.name))
			.findFirst();
		command.replyTo.tell(new GetUserResponse(maybeUser));
		return this;
	}
	
	private Behavior<Command> onGetUsers(GetUsers command) {
		// We must be careful not to send out users since it is mutable
		// so for this response we need to make a defensive copy
		command.replyTo.tell(new Users(Collections.unmodifiableList(new ArrayList<>(users))));
		return this;
	}
	
	private Behavior<Command> onDeleteUser(DeleteUser command) {
		users.removeIf(user -> user.name.equals(command.name));
		command.replyTo.tell(new ActionPerformed(String.format("User %s deleted.", command.name)));
		return this;
	}
	
}
