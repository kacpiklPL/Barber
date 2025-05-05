package pl.kacpik.barber.services;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;

public class CustomOAuth2UserService implements Customizer<OAuth2LoginConfigurer<HttpSecurity>> {

    @Override
    public void customize(OAuth2LoginConfigurer<HttpSecurity> oauth2) {
        // TODO INIT
    }

}
