package org.jboss.tools.fuse.reddeer.view;

import org.jboss.reddeer.swt.condition.JobIsRunning;
import org.jboss.reddeer.swt.impl.shell.WorkbenchShell;
import org.jboss.reddeer.swt.impl.toolbar.DefaultToolItem;
import org.jboss.reddeer.swt.wait.WaitWhile;

/**
 * Represents 'JBoss Central' view
 * 
 * @author tsedmik
 */
public class JBossCentral {

	/**
	 * Opens JBoss Central View
	 */
	public void open() {
		new DefaultToolItem(new WorkbenchShell(), "JBoss Central").click();
		new WaitWhile(new JobIsRunning());
	}

	/**
	 * Selects one of the links in 'Start from Scratch' section
	 * 
	 * @param linkName Name of the link
	 */
	public void startFromScratch(String linkName) {
		// TODO Need hyperlinks (should be in RedDeer 0.8.0)
	}
}
