package tr.com.ogedik.config.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.ogedik.commons.expection.ErrorException;
import tr.com.ogedik.commons.expection.constants.CommonErrorType;
import tr.com.ogedik.commons.rest.request.model.JiraConfigurationProperties;
import tr.com.ogedik.commons.rest.request.model.MailServerProperties;
import tr.com.ogedik.config.constants.GenericProperty;
import tr.com.ogedik.config.constants.JiraProperty;
import tr.com.ogedik.config.model.ConfigurationProperty;
import tr.com.ogedik.config.persistence.manager.ConfigurationRepositoryManager;
import tr.com.ogedik.config.service.ConfigurationService;
import tr.com.ogedik.config.validator.ConfigurationValidationFacade;

import java.util.List;

/** @author orkun.gedik */
@Service
public class ConfigurationServiceImpl implements ConfigurationService {

  private static final Logger logger = LogManager.getLogger(ConfigurationServiceImpl.class);

  @Autowired private ConfigurationRepositoryManager repositoryManager;
  @Autowired private ConfigurationValidationFacade validationFacade;

  @Override
  public JiraConfigurationProperties getJiraConfiguration() {
    JiraConfigurationProperties jiraConfigurationProperties = new JiraConfigurationProperties();
    jiraConfigurationProperties.setBaseURL(
        retrieveConfig(JiraProperty.JIRA_BASE_URL).getPropertyValue());
    jiraConfigurationProperties.setUsername(
        retrieveConfig(JiraProperty.JIRA_USERNAME).getPropertyValue());
    jiraConfigurationProperties.setPassword(
        retrieveConfig(JiraProperty.JIRA_PASSWORD).getPropertyValue());
    jiraConfigurationProperties.setApiVersion(
        retrieveConfig(JiraProperty.JIRA_REST_API_VERSION).getPropertyValue());

    return jiraConfigurationProperties;
  }

  @Override
  public MailServerProperties getMailServerConfiguration(){
    MailServerProperties mailServerProperties = new MailServerProperties();
    mailServerProperties.setHost(
            retrieveConfig(JiraProperty.SMTP_HOST).getPropertyValue());
    mailServerProperties.setPort(
            retrieveConfig(JiraProperty.SMTP_PORT).getPropertyValue());
    mailServerProperties.setUsername(
            retrieveConfig(JiraProperty.SMTP_USERNAME).getPropertyValue());
    mailServerProperties.setPassword(
            retrieveConfig(JiraProperty.SMTP_PASSWORD).getPropertyValue());
    return mailServerProperties;
  }

  @Override
  public ConfigurationProperty configure(ConfigurationProperty property) {
    validationFacade.validateCreate(property);

    return repositoryManager.insertProperty(property);
  }

  @Override
  @Transactional
  public Boolean setUp(List<ConfigurationProperty> configurationProperties) {
    //TODO: fix validation
    //validationFacade.validateSetup(configurationProperties);
    //TODO: mail server validation
    ConfigurationProperty insertedProperty;
    for (ConfigurationProperty property : configurationProperties) {
      insertedProperty = repositoryManager.insertProperty(property);

      if (insertedProperty == null) {
        throw new ErrorException(
            CommonErrorType.DATA_ACCESS_EXCEPTION,
            "Configuration Property cannot be inserted. Configuration Property Key:"
                + property.getPropertyKey());
      }
    }

    return Boolean.TRUE;
  }

  private ConfigurationProperty retrieveConfig(GenericProperty genericProperty) {
    ConfigurationProperty config = repositoryManager.find(genericProperty.name());

    if (config == null) {
      logger.info("Configuration property {} is not found.", genericProperty.name());
      config = new ConfigurationProperty();
      config.setPropertyKey(genericProperty.name());
      config.setPropertyValue(genericProperty.getDefaultValue());
    } else { // config != null
      if (config.getPropertyValue() == null) {
        config.setPropertyValue(genericProperty.getDefaultValue());
      }
    }
    return config;
  }
}
