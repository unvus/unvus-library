package com.unvus.domain;

import com.unvus.domain.audit.AbstractAuditingEntity;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

@Data
public class UnvusSecureUser extends AbstractAuditingEntity {

	private Long id;				// 아이디
	private String login;		// 계정 아이디(email)
	private String password;	    // 계정 패스워드
	private String name;			// 사용자명

    private boolean activated = false;

	private Set<Authority> authorities = new HashSet();

	public UnvusSecureUser() {

	}

	public UnvusSecureUser(Long id, String login, String name, Collection<? extends Authority> authorities) {
		this.id = id;
		this.login = login;
		this.name = name;
		this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
	}

	private static SortedSet<Authority> sortAuthorities(Collection<? extends Authority> authorities) {
		Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
		SortedSet<Authority> sortedAuthorities = new TreeSet(new UnvusSecureUser.AuthorityComparator());
		Iterator var2 = authorities.iterator();

		while(var2.hasNext()) {
            Authority grantedAuthority = (Authority)var2.next();
			Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
			sortedAuthorities.add(grantedAuthority);
		}

		return sortedAuthorities;
	}

	private static class AuthorityComparator implements Comparator<Authority>, Serializable {
		private static final long serialVersionUID = 420L;

		private AuthorityComparator() {
		}

		public int compare(Authority g1, Authority g2) {
			if (g2.getName() == null) {
				return -1;
			} else {
				return g1.getName() == null ? 1 : g1.getName().compareTo(g2.getName());
			}
		}
	}
}

