package org.jboss.tools.teiid.ui.bot.test;

import static org.junit.Assert.*;

import java.util.List;

import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.eclipse.condition.ConsoleHasText;
import org.jboss.reddeer.eclipse.wst.server.ui.view.Server;
import org.jboss.reddeer.junit.requirement.inject.InjectRequirement;
import org.jboss.reddeer.junit.runner.RedDeerSuite;
import org.jboss.reddeer.requirements.server.ServerReqState;
import org.jboss.reddeer.swt.impl.ctab.DefaultCTabItem;
import org.jboss.reddeer.swt.impl.menu.ShellMenu;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.uiforms.impl.hyperlink.DefaultHyperlink;
import org.jboss.reddeer.workbench.handler.EditorHandler;
import org.jboss.reddeer.workbench.impl.shell.WorkbenchShell;
import org.jboss.tools.teiid.reddeer.connection.ConnectionProfileConstants;
import org.jboss.tools.teiid.reddeer.manager.ImportManager;
import org.jboss.tools.teiid.reddeer.manager.ModelExplorerManager;
import org.jboss.tools.teiid.reddeer.manager.VDBManager;
import org.jboss.tools.teiid.reddeer.requirement.TeiidServerRequirement;
import org.jboss.tools.teiid.reddeer.requirement.TeiidServerRequirement.TeiidServer;
import org.jboss.tools.teiid.reddeer.view.ModelExplorer;
import org.jboss.tools.teiid.reddeer.view.ServersViewExt;
import org.jboss.tools.teiid.reddeer.wizard.ServerWizard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author mkralik
 */

@RunWith(RedDeerSuite.class)
@TeiidServer(state = ServerReqState.RUNNING,connectionProfiles={
		ConnectionProfileConstants.ORACLE_11G_PARTS_SUPPLIER
})
public class ServerManipulationTest {
	@InjectRequirement
	private static TeiidServerRequirement teiidServer;
	
	private static final String LOCAL_SERVER_NAME = "localServer";
	private static final String REMOTE_SERVER_NAME = "remoteServer";
	private static final String PROJECT_NAME = "ServerProject";
	private static final String PROJECT_LOCATION = "resources/projects/"+ PROJECT_NAME;
	private static final String NAME_SOURCE_MODEL = "partsSourceOracle";
	private static final String VDB_NAME = "serverVDB";

	private static final ServersViewExt SView= new ServersViewExt();
	
	@Before
	public void stopAndDeleteAllServers(){
		ServersViewExt SView= new ServersViewExt();
		List<Server> servers = SView.getServers();
		for (int i = 0; i < servers.size(); i++) {
			Server tmpServer = servers.get(i);
			try{
				tmpServer.stop();
				AbstractWait.sleep(TimePeriod.SHORT);
				if(!(tmpServer.getLabel().getName().equals(teiidServer.getName()))){
					tmpServer.delete();
				}
			}catch(Exception ex){
				//if server is already stop
				if(!(tmpServer.getLabel().getName().equals(teiidServer.getName()))){
					tmpServer.delete();
				}
			}
		}
	}

	@Test
	public void noRefreshTest(){
		SView.open();
		SView.getServer(teiidServer.getName()).start();
		AbstractWait.sleep(TimePeriod.NORMAL);
		assertTrue(testDeployVDB()); 
	}

	@Test
	public void localServer(){
		ServerWizard wizard = new ServerWizard();
		String[] type = {"Red Hat JBoss Middleware","JBoss Enterprise Application Platform 6.1+"};
		wizard.open();
		wizard
			.setType(type)
			.setName(LOCAL_SERVER_NAME)
			.next();
		wizard
			.setTypeServer(ServerWizard.LOCAL_SERVER)
		    .finish();
		
		SView.open();
		Server newServer = SView.getServer(LOCAL_SERVER_NAME);
		SView.startServer(LOCAL_SERVER_NAME);
		AbstractWait.sleep(TimePeriod.NORMAL);
		assertTrue(testPingToServer(newServer)); //check ping
		
		SView.setDefaultTeiidInstance(LOCAL_SERVER_NAME);
		assertTrue(checkDefaultTeiidName(LOCAL_SERVER_NAME));
		assertTrue(testDeployVDB());

		newServer.restart();
		AbstractWait.sleep(TimePeriod.NORMAL); 
		assertTrue(testPingToServer(newServer)); //check ping after restart
	}
	
	@Test
	public void remoteServer(){
		SView.startServer(teiidServer.getName());
		
		String[] type = {"Red Hat JBoss Middleware","JBoss Enterprise Application Platform 6.1+"};
		ServerWizard wizard = new ServerWizard();
		wizard.open();
		wizard
			.setType(type)
			.setName(REMOTE_SERVER_NAME)
			.next();
		wizard
			.setTypeServer(ServerWizard.REMOTE_SERVER)
			.setControlled(ServerWizard.MANAGEMENT_OPERATIONS)
			.externallyManaged(true)
			.assignRuntime(false)
			.next();
		wizard
			.setHost("Local")
			.setPathToServer(teiidServer.getServerConfig().getServerBase().getHome())
			.next();
		wizard.finish();
		
		SView.open();
		Server newServer = SView.getServer(REMOTE_SERVER_NAME);
		SView.startServer(REMOTE_SERVER_NAME);
		AbstractWait.sleep(TimePeriod.NORMAL);
		assertTrue(testPingToServer(newServer)); //check ping
		
		SView.setDefaultTeiidInstance(REMOTE_SERVER_NAME);
		assertTrue(checkDefaultTeiidName(REMOTE_SERVER_NAME));
		assertTrue(testDeployVDB());

		newServer.restart();
		AbstractWait.sleep(TimePeriod.NORMAL); 
		assertTrue(testPingToServer(newServer)); //check ping after restart
	}
	
	/**
	 * Check name of default teiid instance
	 */
	private boolean checkDefaultTeiidName(String nameServer){
		new ModelExplorer().activate();
		String defaultServerName = new DefaultHyperlink(0).getText();
		return nameServer.equals(defaultServerName);
	}
	
	/**
	 * Test of teiidName and teiidPassword is correct
	 */
	private boolean testPingToServer(Server server){
		server.open();
		new DefaultCTabItem("Teiid Instance").activate();
		new WorkbenchShell();
		new LabeledText("User name").setText(teiidServer.getServerConfig().getServerBase().getProperty("teiidUser"));
		new LabeledText("Password").setText(teiidServer.getServerConfig().getServerBase().getProperty("teiidPassword"));
		AbstractWait.sleep(TimePeriod.SHORT);
		new DefaultHyperlink("Test JDBC Connection").activate();
		new ShellMenu("File", "Save All").select();
		return !(new ConsoleHasText("payload token could not be authenticated by security domain teiid-security").test());
	}
	
	private boolean testDeployVDB(){
		new ImportManager().importProject(PROJECT_LOCATION);
		new ModelExplorerManager().changeConnectionProfile(ConnectionProfileConstants.ORACLE_11G_PARTS_SUPPLIER, PROJECT_NAME, NAME_SOURCE_MODEL);
		VDBManager vdb = new VDBManager(); 
		vdb.createVDB(PROJECT_NAME, VDB_NAME);
		vdb.addModelsToVDB(PROJECT_NAME, VDB_NAME, new String[]{NAME_SOURCE_MODEL + ".xmi"});
		try{
			new ModelExplorer().deployVdb(PROJECT_NAME, VDB_NAME);
		}catch(Error ex){
			EditorHandler.getInstance().closeAll(false);
			new ModelExplorer().deleteAllProjects(true);
			return false;
		}
		EditorHandler.getInstance().closeAll(false);
		new ModelExplorer().deleteAllProjects(true);
		return true;
	}
}
