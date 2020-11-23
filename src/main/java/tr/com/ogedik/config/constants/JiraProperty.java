package tr.com.ogedik.config.constants;

import lombok.Getter;

/** @author orkun.gedik */
@Getter
public enum JiraProperty implements GenericProperty {
  JIRA_BASE_URL(null),
  JIRA_USERNAME(null),
  JIRA_PASSWORD(null),
  JIRA_REST_API_VERSION(JiraApi.v2.getVersion()),
  SMTP_HOST(null),
  SMTP_PORT(null),
  SMTP_USERNAME(null),
  SMTP_PASSWORD(null);

  private String defaultValue;

  JiraProperty(String defaultValue) {
    this.defaultValue = defaultValue;
  }
}
