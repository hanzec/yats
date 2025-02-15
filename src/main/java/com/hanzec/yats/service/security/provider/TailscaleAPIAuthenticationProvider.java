package com.hanzec.yats.service.security.provider;

import com.hanzec.yats.model.data.management.User;
import com.hanzec.yats.service.AccountService;
import com.hanzec.yats.service.security.token.APIAuthenticationToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.ParseException;

@Component
public class TailscaleAPIAuthenticationProvider
        implements AuthenticationProvider, InitializingBean, MessageSourceAware {

    protected final Log logger = LogFactory.getLog(getClass());

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private UserCache userCache = new NullUserCache();

    private final AccountService userDetailsService;

    @Autowired
    public TailscaleAPIAuthenticationProvider(AccountService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public final void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userCache, "A user cache must be set");
        Assert.notNull(this.messages, "A message source must be set");
    }

    private UserDetails retrieveUser(APIAuthenticationToken token) throws AuthenticationException {
        UserDetails loadedUser;
        try {
            loadedUser = this.userDetailsService.loadUserByUsername((String) token.getPrincipal());
        } catch (UsernameNotFoundException notFound) {
            throw notFound;
        } catch (Exception repositoryProblem) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }
        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }

    private void verifyJWTSignature(User user,
                                    APIAuthenticationToken authentication) {
        try {
            // jwt object cannot be null
            if (authentication.getCredentials() == null) {
                this.logger.debug("Failed to authenticate since no credentials provided");
                throw new BadCredentialsException(this.messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }

            // verity jwt signature
            JWSVerifier jwsVerifier = new MACVerifier(user.getJwtKey());
            if (!authentication.getCredentials().verify(jwsVerifier)) {
                this.logger.debug("Failed to authenticate since password does not match stored value");
                throw new BadCredentialsException(this.messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        } catch (JOSEException e) {
            throw new BadCredentialsException("Failed to parse JWT object:" + e.getMessage());
        }
    }

    public void preAuthenticationCheck(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            TailscaleAPIAuthenticationProvider.this.logger
                    .debug("Failed to authenticate since user account is locked");
            throw new LockedException(TailscaleAPIAuthenticationProvider.this.messages
                    .getMessage("TailscaleAPIAuthenticationProvide.locked", "User account is locked"));
        }
        if (!user.isEnabled()) {
            TailscaleAPIAuthenticationProvider.this.logger
                    .debug("Failed to authenticate since user account is disabled");
            throw new DisabledException(TailscaleAPIAuthenticationProvider.this.messages
                    .getMessage("TailscaleAPIAuthenticationProvide.disabled", "User is disabled"));
        }
        if (!user.isAccountNonExpired()) {
            TailscaleAPIAuthenticationProvider.this.logger
                    .debug("Failed to authenticate since user account has expired");
            throw new AccountExpiredException(TailscaleAPIAuthenticationProvider.this.messages
                    .getMessage("TailscaleAPIAuthenticationProvide.expired", "User account has expired"));
        }
    }

    public void postAuthenticationCheck(UserDetails user) {
        if (!user.isCredentialsNonExpired()) {
            TailscaleAPIAuthenticationProvider.this.logger
                    .debug("Failed to authenticate since user account credentials have expired");
            throw new CredentialsExpiredException(TailscaleAPIAuthenticationProvider.this.messages
                    .getMessage("TailscaleAPIAuthenticationProvide.credentialsExpired",
                            "User credentials have expired"));
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        boolean cacheWasUsed = true;

        // try fetching user from cache
        UserDetails user = this.userCache.getUserFromCache((String) authentication.getPrincipal());
        if (user == null) {
            cacheWasUsed = false;
            try {
                user = retrieveUser((APIAuthenticationToken) authentication);
            } catch (UsernameNotFoundException ex) {
                this.logger.debug("Failed to find user '" + (String) authentication.getPrincipal() + "'");
                throw new BadCredentialsException(this.messages
                        .getMessage("TailscaleAPIAuthenticationProvide.badCredentials", "Bad credentials"));
            }
            Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
        }

        // authenticate user by verifying jwt signature
        try {
            preAuthenticationCheck(user);
            verifyJWTSignature((User) user, (APIAuthenticationToken) authentication);
        } catch (AuthenticationException ex) {
            if (!cacheWasUsed) {
                throw ex;
            }
            // There was a problem, so try again after checking
            // we're using latest data (i.e. not from the cache)
            cacheWasUsed = false;
            user = retrieveUser((APIAuthenticationToken) authentication);
            preAuthenticationCheck(user);
            verifyJWTSignature((User) user, (APIAuthenticationToken) authentication);
        }

        // to post check
        postAuthenticationCheck(user);

        // update cache
        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user);
        }
        Object principalToReturn = user;
        try {
            return createSuccessAuthentication(principalToReturn, authentication, user);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a successful {@link Authentication} object.
     * <p>
     * Protected so subclasses can override.
     * </p>
     * <p>
     * Subclasses will usually store the original credentials the user supplied (not
     * salted or encoded passwords) in the returned <code>Authentication</code> object.
     * </p>
     *
     * @param principal      that should be the principal in the returned object
     * @param authentication that was presented to the provider for validation
     * @param user           that was loaded by the implementation
     * @return the successful authentication token
     */
    protected Authentication createSuccessAuthentication(Object principal,
                                                         Authentication authentication,
                                                         UserDetails user) throws ParseException {
        // Ensure we return the original credentials the user supplied,
        // so subsequent attempts are successful even with encoded passwords.
        // Also ensure we return the original getDetails(), so that future
        // authentication events after cache expiry contain the details
        APIAuthenticationToken result = APIAuthenticationToken.authenticated(principal,
                (String) authentication.getCredentials(), user.getAuthorities());
        result.setDetails(authentication.getDetails());
        this.logger.debug("Authenticated user");
        return result;
    }


    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
