package de.upb.pcm.interpreter.launcher.jobs;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

import de.uka.ipd.sdq.codegen.simucontroller.runconfig.SimuComWorkflowConfiguration;
import de.uka.ipd.sdq.probespec.framework.ISampleBlackboard;
import de.uka.ipd.sdq.probespec.framework.ProbeSpecContext;
import de.uka.ipd.sdq.probespec.framework.SampleBlackboard;
import de.uka.ipd.sdq.simucomframework.DiscardInvalidMeasurementsBlackboardDecorator;
import de.uka.ipd.sdq.simucomframework.ExperimentRunner;
import de.uka.ipd.sdq.simucomframework.SimuComConfig;
import de.uka.ipd.sdq.simucomframework.SimuComGarbageCollector;
import de.uka.ipd.sdq.simucomframework.calculator.CalculatorFactory;
import de.uka.ipd.sdq.simucomframework.calculator.SetupPipesAndFiltersStrategy;
import de.uka.ipd.sdq.simucomframework.model.SimuComModel;
import de.uka.ipd.sdq.simucomframework.probes.SimuComProbeStrategyRegistry;
import de.uka.ipd.sdq.simucomframework.simucomstatus.SimuComStatus;
import de.uka.ipd.sdq.simucomframework.simucomstatus.SimucomstatusFactory;
import de.uka.ipd.sdq.simulation.AbstractSimulationConfig;
import de.uka.ipd.sdq.simulation.abstractsimengine.ISimEngineFactory;
import de.uka.ipd.sdq.simulation.preferences.SimulationPreferencesHelper;
import de.uka.ipd.sdq.workflow.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.exceptions.JobFailedException;
import de.uka.ipd.sdq.workflow.exceptions.RollbackFailedException;
import de.uka.ipd.sdq.workflow.exceptions.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;
import de.upb.pcm.interpreter.access.AccessFactory;
import de.upb.pcm.interpreter.access.IModelAccessFactory;
import de.upb.pcm.interpreter.access.UsageModelAccess;
import de.upb.pcm.interpreter.interpreter.InterpreterDefaultContext;
import de.upb.pcm.interpreter.sdinterpreter.IReconfigurator;
import de.upb.pcm.interpreter.sdinterpreter.ReconfigurationListener;
import de.upb.pcm.interpreter.sdinterpreter.SDReconfigurator;
import de.upb.pcm.interpreter.utils.InterpreterLogger;
import de.upb.pcm.interpreter.utils.ResourceSyncer;

/**
 * Job starting the pcm interpretation.
 * 
 * @author Joachim Meyer
 * 
 */
public class PCMStartInterpretationJob 
implements IBlackboardInteractingJob<MDSDBlackboard> {
	
	private static final Logger logger = Logger
			.getLogger(PCMStartInterpretationJob.class.getName());

	private MDSDBlackboard blackboard;

	private final SimuComWorkflowConfiguration configuration;

	/**
	 * Constructor
	 * 
	 * @param configuration
	 *            the SimuCom workflow configuration.
	 */
	public PCMStartInterpretationJob(final SimuComWorkflowConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * @see de.uka.ipd.sdq.workflow.IJob#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void execute(final IProgressMonitor monitor)
			throws JobFailedException, UserCanceledException {
		
		InterpreterLogger.info(logger, "Start job: " + this);

		// 1. Initialise SimuComModel & Simulation Engine
		final SimuComModel simuComModel = initialiseSimuComModel();

		// 2. Initialise Model Access Factory
		final IModelAccessFactory modelAccessFactory = AccessFactory.createModelAccessFactory(this.blackboard);
		
		// 3. Setup interpreters for each usage scenario
		final UsageModelAccess usageModelAccess = modelAccessFactory.getUsageModelAccess(
						new InterpreterDefaultContext(simuComModel));
		simuComModel.setUsageScenarios(usageModelAccess.getWorkloadDrivers(
				modelAccessFactory));

	    /*
	     * 4. Setup Actuators that keep simulated system and model@runtime consistent
	    * Sync Resources from global pcm model with simucom model for the first time, models are
	    * already loaded into the blackboard by the workflow engine
	    */
	    final ResourceSyncer resourceSyncer = new ResourceSyncer(simuComModel, modelAccessFactory);
        resourceSyncer.syncResourceEnvironment();
		
        // 5. Setup reconfiguration rules and engines
        final ReconfigurationListener reconfigurator = 
        		new ReconfigurationListener(
        				modelAccessFactory, 
        				new IReconfigurator[]{new SDReconfigurator(modelAccessFactory)});
        reconfigurator.startListening();
        
		InterpreterLogger.debug(logger,"Start simulation");
		final double simRealTimeNano = ExperimentRunner.run(simuComModel);
		InterpreterLogger.debug(logger,
				"Finished Simulation. Simulator took "
						+ (simRealTimeNano / Math.pow(10, 9)) + " real time seconds");

		// 6. Deregister all listeners and execute cleanup code
		reconfigurator.stopListening();
		simuComModel.getProbeSpecContext().finish();
		InterpreterLogger.info(logger, "finished job: " + this);
	}

	/**
	 * @see de.uka.ipd.sdq.workflow.IJob#getName()
	 */
	@Override
	public String getName() {
		return "Run SimuLizar";
	}

	/**
	 * @see de.uka.ipd.sdq.workflow.IJob#rollback(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void rollback(final IProgressMonitor monitor)
			throws RollbackFailedException {
	}

	/**
	 * @see de.uka.ipd.sdq.workflow.IBlackboardInteractingJob#setBlackboard(de.uka.ipd.sdq.workflow.Blackboard)
	 */
	@Override
	public void setBlackboard(final MDSDBlackboard blackboard) {
		this.blackboard = blackboard;
	}

	private SimuComModel initialiseSimuComModel() {
		// Configuration options for the simulation engine
		final AbstractSimulationConfig simulationConfiguration = getConfiguration()
				.getSimulationConfiguration();

		// Status model to store the runtime state of the simulator
		final SimuComStatus simuComStatus = createSimuComStatus();

		// Factory used to create the simulation engine used in the simulation,
		// e.g., SSJ engine or Desmo-J engine
		final ISimEngineFactory simEngineFactory = getSimEngineFactory();

		// Probe spec context used to take the measurements of the simulation
		final ProbeSpecContext probeSpecContext = new ProbeSpecContext();

		final SimuComModel simuComModel = new SimuComModel(
				(SimuComConfig) simulationConfiguration, simuComStatus,
				simEngineFactory, false, probeSpecContext);

		simuComModel.getSimulationStatus().setCurrentSimulationTime(0);

		linkSimuComAndProbeSpec(simuComModel, probeSpecContext);
		
		return simuComModel;
	}

	private ISimEngineFactory getSimEngineFactory() {
		// load factory for the preferred simulation engine
		ISimEngineFactory factory = SimulationPreferencesHelper
				.getPreferredSimulationEngine();
		if (factory == null) {
			throw new RuntimeException(
					"There is no simulation engine available. Install at least one engine.");
		}
		return factory;
	}

	/**
	 * @return returns the configuration.
	 */
	private SimuComWorkflowConfiguration getConfiguration() {
		return this.configuration;
	}

	/**
	 * Gets the SimuCom status, creates one if none exists.
	 * 
	 * @return the SimuCom status.
	 */
	private SimuComStatus createSimuComStatus() {
		final SimuComStatus simuComStatus = SimucomstatusFactory.eINSTANCE
				.createSimuComStatus();

		simuComStatus.setProcessStatus(SimucomstatusFactory.eINSTANCE
				.createSimulatedProcesses());
		simuComStatus.setResourceStatus(SimucomstatusFactory.eINSTANCE
				.createSimulatedResources());

		return simuComStatus;
	}

	/**
	 * Sets SampleBlackboard instead of concurrency sample blackboard.
	 * 
	 * @param simuComModel
	 *            the SimuCom model.
	 */
	private void linkSimuComAndProbeSpec(final SimuComModel simuComModel,
			final ProbeSpecContext probeSpecContext) {
		final ISampleBlackboard discardingBlackboard = new DiscardInvalidMeasurementsBlackboardDecorator(
				new SampleBlackboard(), simuComModel.getSimulationControl());

		probeSpecContext.initialise(
				discardingBlackboard,
				new SimuComProbeStrategyRegistry(), 
				new CalculatorFactory(
						simuComModel, 
						new SetupPipesAndFiltersStrategy(simuComModel)));

		// install a garbage collector which keeps track of the samples stored
		// on the blackboard and
		// removes samples when they become obsolete
		probeSpecContext.setBlackboardGarbageCollector(
				new SimuComGarbageCollector(discardingBlackboard));
	}
}
