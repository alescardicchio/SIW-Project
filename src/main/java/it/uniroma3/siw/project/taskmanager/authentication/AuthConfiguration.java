package it.uniroma3.siw.project.taskmanager.authentication;

import it.uniroma3.siw.project.taskmanager.model.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // --- Authorization paragraph:
                //     here it's defined who can access which pages
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/", "/index", "/login", "/users/register").permitAll() // anyone can access these pages
                .antMatchers(HttpMethod.POST, "/login", "/users/register").permitAll()  // anyone can send http post requests from these pages
                .antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(Credentials.ADMIN_ROLE) // only admins can access these pages
                .antMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(Credentials.ADMIN_ROLE) // only admins can access and send http post requests from these pages
                .anyRequest().authenticated() // only authenticated users can access all the remaining pages
                .and()
                // --- Login paragraph:
                //     here we define how users can login
                .formLogin()
                .defaultSuccessUrl("/home")
                .and()
                .oauth2Login()
                 // after login, the user'll be redirect to his homepage
                .and()
                // --- Logout paragraph:
                //     here we define how users can logout
                .logout()
                .logoutUrl("/logout") // endpoint of page where the user can logout
                .logoutSuccessUrl("/index") // after logout, the user'll be redirect to /index
                .invalidateHttpSession(true) // aborts the session when the user logs out
                .clearAuthentication(true).permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                // dataSource acts as an access point with the database
                .dataSource(this.dataSource)
                // specify how to find a user's role given his username
                .authoritiesByUsernameQuery("SELECT user_name, role FROM credentials WHERE user_name = ?")
                // specify how to find user's credentials like username, password and a boolean flag which means if the user is enabled or not
                .usersByUsernameQuery("SELECT user_name, password, 1 as enabled FROM credentials WHERE user_name = ?");
    }
}
