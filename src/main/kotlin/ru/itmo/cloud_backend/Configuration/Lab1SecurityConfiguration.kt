package ru.itmo.cloud_backend.Configuration


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import ru.itmo.cloud_backend.Services.MyUserDetailsService
import ru.itmo.cloud_backend.filters.JwtFilter

@EnableWebSecurity
open class Lab1SecurityConfiguration : WebSecurityConfigurerAdapter() {
    @Autowired
    lateinit var myUserDetailsService: MyUserDetailsService

    @Autowired
    lateinit var jwtFilter: JwtFilter

    override fun configure(http: HttpSecurity) {

        http
            .csrf().disable()
            .authorizeRequests()
//            .antMatchers("/receipts").hasAnyAuthority()
//            .antMatchers("/receipts/*").hasAnyAuthority()
//            .antMatchers("/api/*").hasAnyAuthority("ROLE_USER","ROLE_MODERATOR", "ROLE_ADMIN")
            .antMatchers("/api/**").authenticated()
            .antMatchers("/**").permitAll()
            .and().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    open fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }

    @Autowired
    @Throws(Exception::class)
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService<UserDetailsService>(myUserDetailsService)
    }

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }
}