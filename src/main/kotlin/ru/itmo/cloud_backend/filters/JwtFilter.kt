package ru.itmo.cloud_backend.filters

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.itmo.cloud_backend.Services.MyUserDetailsService
import ru.itmo.cloud_backend.Utils.JwtUtil
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtFilter : OncePerRequestFilter() {

    @Autowired
    lateinit var myUserDetailsService: MyUserDetailsService

    @Autowired
    lateinit var jwtUtil: JwtUtil

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authorizationHeader = request.getHeader("Authorization");
        var username: String? = null
        var jwtToken: String? = null
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7)
            username = jwtUtil.extractUsername(jwtToken)
        }
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = this.myUserDetailsService.loadUserByUsername(username)
            if (userDetails != null)
                if (jwtUtil.validateToken(jwtToken, userDetails)) {
                    val usernamePasswordAuthenticationToken =
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    usernamePasswordAuthenticationToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
                }
        }
        chain.doFilter(request, response)
    }
}