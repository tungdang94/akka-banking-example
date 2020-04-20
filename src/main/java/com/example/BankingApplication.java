package com.example;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Adapter;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import com.example.actor.*;
import com.example.config.DBConfig;
import com.example.message.BankLinkingMessage;
import com.example.router.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.concat;

public class BankingApplication {
	
	// #start-http-server
	static void startHttpServer(Route route, ActorSystem<?> system) {
		// Akka HTTP still needs a classic ActorSystem to start
		akka.actor.ActorSystem classicSystem = Adapter.toClassic(system);
		final Http http = Http.get(classicSystem);
		final Materializer materializer = Materializer.matFromSystem(system);
		
		// binding happens asynchronously and therefore the bindAndHandle method returns a Future which completes with an object representing the binding or fails if binding the HTTP route failed.
		// for example if the port is already taken.
		Config config = ConfigFactory.load();
		final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = route.flow(classicSystem, materializer);
		CompletionStage<ServerBinding> futureBinding = http.bindAndHandle(routeFlow, ConnectHttp.toHost(config.getString("server.host"), config.getInt("server.port")), materializer);
		
		futureBinding.whenComplete((binding, exception) -> {
			if (binding != null) {
				InetSocketAddress address = binding.localAddress();
				system.log().info("Server online at http://{}:{}/", address.getHostString(), address.getPort());
			} else {
				system.log().error("Failed to bind HTTP endpoint, terminating system", exception);
				DBConfig.DB_SESSION.close();    // Slick requires you to eventually close your database session to free up connection pool resources.
				system.terminate();            // To make sure our application stops if it cannot bind we terminate the actor system if there is a failure.
			}
		});
	}
	// #start-http-server
	
	public static void main(String[] args) throws Exception {
		//#server-bootstrapping
		Behavior<NotUsed> rootBehavior = Behaviors.setup(context -> {
			
			ActorRef<UserRegistry.Command> userRegistryActor = context.spawn(UserRegistry.create(), "user-registry");
			ActorRef<BankLinkingMessage.Command> bankLinkingActor = context.spawn(BankLinking.create(), "bank-linking");
			ActorRef<BankExchange.Command> bankExchangeActor = context.spawn(BankExchange.create(), "bank-exchange");
			ActorRef<BankChecking.Command> bankCheckingActor = context.spawn(BankChecking.create(), "bank-checking");
			ActorRef<BankReconciliation.Command> bankReconciliationActor = context.spawn(BankReconciliation.create(), "bank-reconciliation");
			
			UserRoutes userRoutes = new UserRoutes(context.getSystem(), userRegistryActor);
			BankLinkingRoutes bankLinkingRoutes = new BankLinkingRoutes(context.getSystem(), bankLinkingActor);
			BankExchangeRoutes bankExchangeRoutes = new BankExchangeRoutes(context.getSystem(), bankExchangeActor);
			BankCheckingRoutes bankCheckingRoutes = new BankCheckingRoutes(context.getSystem(), bankCheckingActor);
			BankReconciliationRoutes bankReconciliationRoutes = new BankReconciliationRoutes(context.getSystem(), bankReconciliationActor);
			
			// In larger applications we’d define separate subsystems in different places
			// and then combine combine the various routes of our application into a big using the concat directive like this
			// Route route = concat(UserRoutes.userRoutes(), healthCheckRoutes, ...)
			
			Route route = concat(userRoutes.userRoutes(), bankLinkingRoutes.bankLinkingRoutes(),
				bankExchangeRoutes.bankLinkingRoutes(), bankCheckingRoutes.bankCheckingRoutes(), bankReconciliationRoutes.bankReconciliationRoutes());
			
			startHttpServer(route, context.getSystem());
			
			return Behaviors.empty();
		});
		
		// Create ActorSystem and top level supervisor
		// And boot up server using the route as defined above
		ActorSystem system = ActorSystem.create(rootBehavior, "akka-banking");
		system.getWhenTerminated().thenAccept(done -> DBConfig.DB_SESSION.close());    // Slick requires you to eventually close your database session to free up connection pool resources.
	}
	
}
