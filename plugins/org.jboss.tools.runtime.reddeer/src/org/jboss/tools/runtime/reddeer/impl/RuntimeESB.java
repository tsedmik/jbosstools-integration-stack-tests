package org.jboss.tools.runtime.reddeer.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.tools.esb.core.runtime.JBossESBRuntime;
import org.jboss.tools.esb.core.runtime.JBossRuntimeManager;
import org.jboss.tools.runtime.reddeer.Namespaces;
import org.jboss.tools.runtime.reddeer.RuntimeBase;
import org.jboss.tools.runtime.reddeer.wizard.ESBRuntimePreferencePage;
import org.jboss.tools.runtime.reddeer.wizard.ESBRuntimeWizard;

/**
 * ESB Runtime
 * 
 * @author apodhrad
 * 
 */
@XmlRootElement(name = "esb", namespace = Namespaces.SOA_REQ)
@XmlAccessorType(XmlAccessType.FIELD)
public class RuntimeESB extends RuntimeBase {

	@Override
	public void create() {
		// Add runtime
		ESBRuntimePreferencePage esbRuntimePreferencePage = new ESBRuntimePreferencePage();
		esbRuntimePreferencePage.open();
		ESBRuntimeWizard esbRuntimeWizard = esbRuntimePreferencePage.addESBRuntime();
		esbRuntimeWizard.setName(getName());
		esbRuntimeWizard.setHomeFolder(getHome());
		esbRuntimeWizard.finish();
		esbRuntimePreferencePage.ok();
	}
	
	@Override
	public boolean exists() {
		JBossESBRuntime esbRuntime = JBossRuntimeManager.getInstance().findRuntimeByName(name);
		return esbRuntime != null;
	}
}
