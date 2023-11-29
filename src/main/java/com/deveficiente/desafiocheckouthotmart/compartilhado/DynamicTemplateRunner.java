package com.deveficiente.desafiocheckouthotmart.compartilhado;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class DynamicTemplateRunner {

	private TemplateEngine templateEngine;

	public DynamicTemplateRunner(TemplateEngine templateEngine) {
		super();
		this.templateEngine = templateEngine;
	}
	
	/**
	 * 
	 * @param templatePath path relative to src/main/resources/templates. Ex: folder/template.html or template.html
	 * @param templateVariables map with template variables
	 * @return template processed
	 */
    public String buildTemplate(String templatePath, Map<String, Object> templateVariables) {
        Context context = new Context();
        
        templateVariables.forEach((key,value) -> {
        	context.setVariable(key, value);        	
        });

        return templateEngine.process(templatePath, context);
    }

}
