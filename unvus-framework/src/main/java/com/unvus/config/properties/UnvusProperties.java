package com.unvus.config.properties;

import io.github.jhipster.config.JHipsterProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Properties specific to UnvUS.
 *
 * <p> Properties are configured in the application.yml file. </p>
 * <p> This class also load properties in the Spring Environment from the git.properties and META-INF/build-info.properties
 * files if they are found in the classpath.</p>
 */
@ConfigurationProperties(prefix = "unvus", ignoreUnknownFields = false)
@PropertySources({
		@PropertySource(value = "classpath:unvus-git.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "classpath:META-INF/unvus-build-info.properties", ignoreResourceNotFound = true)
})
public class UnvusProperties {

	private final JHipsterProperties jhipster;

	public JHipsterProperties getJhipster() {
		return jhipster;
	}

	public UnvusProperties(JHipsterProperties jhipster) {
		this.jhipster = jhipster;
	}

	private final UnvusProperties.App app = new UnvusProperties.App();

	@Getter
	@Setter
	public static class App {
		private String name;
		private String env;
	}


}
