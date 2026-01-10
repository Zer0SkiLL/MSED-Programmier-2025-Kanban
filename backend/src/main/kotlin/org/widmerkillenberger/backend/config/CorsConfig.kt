package org.widmerkillenberger.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        
        // Allow credentials (cookies, authorization headers)
        config.allowCredentials = true
        
        // Allow all headers
        config.addAllowedHeader("*")
        
        // Allow all HTTP methods
        config.addAllowedMethod("*")
        
        // Allow requests from frontend (localhost:3000 for development)
        config.addAllowedOrigin("http://localhost:3000")
        config.addAllowedOrigin("http://127.0.0.1:3000")
        
        // Expose headers that the frontend needs to access
        config.addExposedHeader("Content-Type")
        config.addExposedHeader("Authorization")
        
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}
