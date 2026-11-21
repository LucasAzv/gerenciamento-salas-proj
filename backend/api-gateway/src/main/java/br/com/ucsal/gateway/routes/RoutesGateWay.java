package br.com.ucsal.gateway.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesGateWay {
	
	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("salas_route", r -> r.path("/salas/**").uri("lb://sala-service"))
				.route("gerenciamento_salas_route", r -> r.path("/api/**").uri("lb://gerenciamento-salas"))
				
				.build();
	}

}
