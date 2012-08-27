package de.upb.mdse.simulizar.loadbalancer.analyser.filter;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.jscience.physics.amount.Amount;

import de.upb.mdse.simulizar.loadbalancer.analyser.helper.MeasureHelper;

import kieker.analysis.plugin.annotation.InputPort;
import kieker.analysis.plugin.annotation.OutputPort;
import kieker.analysis.plugin.annotation.Plugin;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.analysis.plugin.filter.forward.TeeFilter;
import kieker.common.configuration.Configuration;
import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.common.record.controlflow.OperationExecutionRecord;

@Plugin(outputPorts = @OutputPort(name = ComputeResponseTimeFilter.OUTPUT_PORT_RESPONSE_TIMES, 
	description = "Provides response times of operation execution times as stream", 
	eventTypes = { Measure.class }))
public class ComputeResponseTimeFilter extends AbstractFilterPlugin {

	public static final String OUTPUT_PORT_RESPONSE_TIMES = "outputResponseTimes";
	public static final String INPUT_PORT_NAME_EVENTS = "inputExecutionRecords";
	
	private static final Log LOG = LogFactory.getLog(ComputeResponseTimeFilter.class);

	public ComputeResponseTimeFilter(Configuration configuration) {
		super(configuration);
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

	@InputPort(name = INPUT_PORT_NAME_EVENTS, description = "Receives incoming execution records", eventTypes = { OperationExecutionRecord.class })
	public final void inputEvent(final Object inEvent) {
		OperationExecutionRecord record = (OperationExecutionRecord) inEvent;
		long diff = record.getTout() - record.getTin();
		Measure<Long, Duration> result = Measure.valueOf(diff, SI.NANO(SI.SECOND));
		LOG.info(MeasureHelper.formatDuration(result));
		super.deliver(OUTPUT_PORT_RESPONSE_TIMES, result);
	}

}
