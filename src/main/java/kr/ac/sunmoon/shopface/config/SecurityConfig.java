package kr.ac.sunmoon.shopface.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import kr.ac.sunmoon.shopface.member.MemberService;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final MemberService memberService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/img/**", "/js/**", "/templates/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//추후에 관리자 추가
		http.authorizeRequests()
			.antMatchers("/login", "/member/form", "/member/check").permitAll()
			.antMatchers(HttpMethod.POST, "/member", "employ").permitAll()
			.antMatchers(HttpMethod.PUT, "/employ", "/employ/invite/").permitAll()
			.antMatchers("/**").hasRole("MEMBER")
		.and()
			.formLogin()
			.loginPage("/login")
			.defaultSuccessUrl("/member", true)
			.usernameParameter("id")
			.failureUrl("/login?error")
			.permitAll()
		.and()
			.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/login")
			.deleteCookies("JSESSIONID")
			.invalidateHttpSession(true)
		.and()
			.exceptionHandling().accessDeniedPage("/login")
		.and()
		    .csrf().ignoringAntMatchers("/employ/**");
	}
}
