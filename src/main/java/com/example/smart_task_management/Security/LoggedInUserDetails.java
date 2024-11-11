package com.example.smart_task_management.Security;

import com.example.smart_task_management.Entities.Role;
import com.example.smart_task_management.Entities.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents an authenticated user in the system with details such as username, password, roles,
 * and session token. Implements both {@link UserDetails} and {@link Principal} interfaces to
 * integrate with Spring Security and Java authentication mechanisms.
 */
@Data
public class LoggedInUserDetails implements UserDetails, Principal {

	// User object representing the authenticated user
	private final User user;

	// Token associated with the user's session
	private String sessionToken;

	/**
	 * Constructor to initialize LoggedInUserDetails with a User object.
	 *
	 * @param user the User entity representing the authenticated user
	 */
	public LoggedInUserDetails(User user) {
		this.user = user;
	}

	/**
	 * Retrieves the session token for the authenticated user.
	 *
	 * @return the session token
	 */
	public String getSessionToken() {
		return sessionToken;
	}

	/**
	 * Sets the session token for the authenticated user.
	 *
	 * @param sessionToken the session token to set
	 */
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	/**
	 * Retrieves the authorities (roles) assigned to the authenticated user.
	 *
	 * @return a collection of granted authorities for the user
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Role> roles = user.getRoles();
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();

		// Convert roles to SimpleGrantedAuthority for Spring Security
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
		}

		return authorities;
	}

	/**
	 * Retrieves the roles assigned to the authenticated user.
	 *
	 * @return a set of roles for the user
	 */
	public Set<Role> getRoles() {
		return user.getRoles();
	}

	/**
	 * Retrieves the password of the authenticated user.
	 *
	 * @return the password
	 */
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	/**
	 * Retrieves the username of the authenticated user.
	 *
	 * @return the username
	 */
	@Override
	public String getUsername() {
		return user.getUserName();
	}

	/**
	 * Checks if the authenticated user has an "ADMIN" role.
	 *
	 * @return true if the user is an admin, false otherwise
	 */
	public boolean isAdmin() {
		Set<Role> roles = user.getRoles();

		for (Role role : roles) {
			if (role.getRoleName().equals("ADMIN"))
				return true;
		}

		return false;
	}

	/**
	 * Retrieves the ID of the authenticated user.
	 *
	 * @return the user's ID
	 */
	public Long getUserId() {
		return user.getId();
	}

	/**
	 * Retrieves the roles assigned to the authenticated user.
	 *
	 * @return a set of roles
	 */
	public Set<Role> getUserRoles() {
		return user.getRoles();
	}

	/**
	 * Retrieves the User entity representing the authenticated user.
	 *
	 * @return the User entity
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Checks if the user's account has expired.
	 *
	 * @return true if the account is not expired, false otherwise
	 */
	@Override
	public boolean isAccountNonExpired() {
		return user != null;
	}

	/**
	 * Checks if the user's account is locked.
	 *
	 * @return true if the account is not locked, false otherwise
	 */
	@Override
	public boolean isAccountNonLocked() {
		return user != null;
	}

	/**
	 * Checks if the user's credentials have expired.
	 *
	 * @return true if the credentials are not expired, false otherwise
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return user != null;
	}

	/**
	 * Checks if the user's account is enabled.
	 *
	 * @return true if the account is enabled, false otherwise
	 */
	@Override
	public boolean isEnabled() {
		return user != null;
	}

	/**
	 * Retrieves the name of the authenticated user (equivalent to username).
	 *
	 * @return the username of the authenticated user
	 */
	@Override
	public String getName() {
		if (user != null)
			return user.getUserName();
		return "";
	}

	/**
	 * Checks if the provided Subject implies the current user.
	 *
	 * @param subject the Subject to check
	 * @return true if the current user is implied by the subject
	 */
	@Override
	public boolean implies(Subject subject) {
		return Principal.super.implies(subject);
	}
}
