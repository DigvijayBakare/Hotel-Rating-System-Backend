package com.userservice.configuration;

import com.userservice.configuration.interceptor.RestTemplateInterceptor;
import feign.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ConfigurationClass {
    @Autowired
    private ClientRegistrationRepository registrationRepository;

    @Autowired
    OAuth2AuthorizedClientRepository clientRepository;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new RestTemplateInterceptor(clientManager(
               registrationRepository, clientRepository
        )));
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }


    @Bean
    public OAuth2AuthorizedClientManager clientManager(
            ClientRegistrationRepository registrationRepository,
            OAuth2AuthorizedClientRepository clientRepository
    ) {
        // Create an OAuth2AuthorizedClientProvider using the builder
        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode().refreshToken().clientCredentials()
                        .password().build();

        // Construct the DefaultOAuth2AuthorizedClientManager with both repositories
        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(registrationRepository, clientRepository);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

}
