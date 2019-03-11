package com.badgehu.demo.config;

import com.badgehu.demo.dao.PermissionDao;
import com.badgehu.demo.dao.UserDao;
import com.badgehu.demo.domain.Permission;
import com.badgehu.demo.domain.SysUser;
import com.badgehu.demo.security.MyCustomUserService;
import com.badgehu.demo.security.MyFilterSecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(LdapProperties.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LdapProperties ldapProperties;

    @Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;

    @Autowired
    private UserDao userDao;
    @Autowired
    PermissionDao permissionDao;

//    @Bean
//    UserDetailsService customUserService(){
//        return new MyCustomUserService();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/static").permitAll()
                .antMatchers("/api/private**").authenticated() //任何请求,登录后可以访问
                .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll() //登录页面用户任意访问
                .and()
                .logout().permitAll()//注销行为任意访问
                .and().csrf().disable();
        //http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class);
    }
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        String url = String.format("%s/%s", ldapProperties.getUrls()[0], ldapProperties.getBase());

        auth
                .ldapAuthentication()
                .contextSource()
                .url(url)
                .managerDn(ldapProperties.getUsername())
                .managerPassword(ldapProperties.getPassword())
                .and()
                .userDnPatterns("uid={0}","cn={0}","cn={0},ou=tech")
                .ldapAuthoritiesPopulator(new LdapAuthoritiesPopulator() {
                    @Override
                    public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {
                        SysUser user = userDao.findByUserName(username);
                        if (user != null) {
                            List<Permission> permissions = permissionDao.findByAdminUserId(user.getId());
                            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                            for (Permission permission : permissions) {
                                if (permission != null && permission.getName()!=null) {

                                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getName());
                                    //1：此处将权限信息添加到 GrantedAuthority 对象中，在后面进行全权限验证时会使用GrantedAuthority 对象。
                                    grantedAuthorities.add(grantedAuthority);
                                    System.out.println(permission.getName());
                                }
                            }
                            return grantedAuthorities;
                        } else {
                            throw new UsernameNotFoundException("admin: " + username + " do not exist!");
                        }
                    }
                });
    }

    /*@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(new MyPasswordEncoder())
                .withUser("user").password("1234").roles("ADMIN");
    }*/

}
