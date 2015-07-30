package org.jboss.tools.fuse.ui.bot.test;

import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.tools.fuse.reddeer.view.JBossCentral;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A Fuse Integration Project can be created from JBoss Central.
 * In case of Fuse Tooling is not installed, it installs the tooling.
 * 
 * <b>Note:</b> This test should be run in environment with and without installed JBoss Fuse Tooling.
 * 
 * @author tsedmik
 */
@RunWith(RedDeerSuite.class)
public class JBossCentralTest {

	/**
	 * Tests the following:
	 * <ul>
	 * <li>Check whether JBoss Fuse Tooling is installed</li>
	 * <li>Select JBoss Integration Project</li>
	 * <li>Check whether the open wizard is the right one (installation/project creation)</li>
	 * <li>Try to cancel the open wizard</li>
	 * <li>Check completeness of plugins to install</li>
	 * <li>Check installed plugins (Need to deal with JBDS restart?)</li>
	 * <li>Check error log</li>
	 * <li>Try to create a Fuse project via JBoss Integration Project link in JBoss Central</li>
	 * <li>Check error log</li>
	 * </ul>
	 */
	@Test
	public void testBasic() {

		JBossCentral central = new JBossCentral();
		central.open();
		central.startFromScratch("Fuse Integration Project");
	}
}
