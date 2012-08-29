package de.upb.mdse.simulizar.loadbalancer.analyser.filter;

import java.rmi.RemoteException;

import de.uka.ipd.sdq.prototype.framework.registry.RmiRegistry;
import defaultrepository.ILastverteiler;
import defaultrepository.IServer;
import kieker.analysis.plugin.annotation.InputPort;
import kieker.analysis.plugin.annotation.Plugin;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.common.configuration.Configuration;
import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;

@Plugin()
public class ReconfigurationExecutorFilter extends AbstractFilterPlugin {

	public static final String INPUT_VIOLATED_IDS = "violatedIDs";
	private static final Log LOG = LogFactory.getLog(ReconfigurationExecutorFilter.class);
	private ILastverteiler server_port;

	public ReconfigurationExecutorFilter(Configuration configuration) {
		super(configuration);
		RmiRegistry.setRemoteAddress("localhost");
		server_port = (ILastverteiler) RmiRegistry.lookup("ILastverteiler_lastverteiler");
	}

	@Override
	public Configuration getCurrentConfiguration() {
		final Configuration configuration = new Configuration();
		return configuration;
	}

	@Override
	protected Configuration getDefaultConfiguration() {
		final Configuration configuration = new Configuration();
		return configuration;
	}

	@InputPort(name = INPUT_VIOLATED_IDS, description = "Receives incoming IDs of methods violating their SLA", eventTypes = { Identifiable.class })
	public final void inputEvent(final Object inEvent) {
		LOG.info("Should reconfigure now!");
		try {
			server_port.reconfigure(0.1);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
