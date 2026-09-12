//package auth_api.config;
//
//import auth_api.filter.JwtAuthenticationFilter;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FilterConfig {
//
//    @Bean
//    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
//            JwtAuthenticationFilter jwtAuthenticationFilter) {
//
//        FilterRegistrationBean<JwtAuthenticationFilter> registration =
//                new FilterRegistrationBean<>(jwtAuthenticationFilter);
//
//        registration.setEnabled(false);
//
//        return registration;
//    }
//}
//
