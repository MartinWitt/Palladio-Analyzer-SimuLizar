package org.palladiosimulator.simulizar.interpreter.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.UnsupportedDataTypeException;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringpointFactory;
import org.palladiosimulator.edp2.models.measuringpoint.StringMeasuringPoint;
import org.palladiosimulator.edp2.util.MeasuringPointUtility;
import org.palladiosimulator.pcmmeasuringpoint.ActiveResourceMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.PcmmeasuringpointFactory;
import org.palladiosimulator.pcmmeasuringpoint.SystemOperationMeasuringPoint;
import org.palladiosimulator.pcmmeasuringpoint.UsageScenarioMeasuringPoint;
import org.palladiosimulator.probeframework.calculator.Calculator;
import org.palladiosimulator.probeframework.calculator.ICalculatorFactory;
import org.palladiosimulator.probeframework.probes.Probe;
import org.palladiosimulator.probeframework.probes.TriggeredProbe;
import org.palladiosimulator.simulizar.access.IModelAccessFactory;
import org.palladiosimulator.simulizar.access.PMSAccess;
import org.palladiosimulator.simulizar.access.PRMAccess;
import org.palladiosimulator.simulizar.metrics.aggregators.ResponseTimeAggregator;
import org.palladiosimulator.simulizar.pms.MeasurementSpecification;
import org.palladiosimulator.simulizar.pms.PerformanceMetricEnum;
import org.palladiosimulator.simulizar.prm.PrmFactory;

import de.uka.ipd.sdq.pcm.core.entity.Entity;
import de.uka.ipd.sdq.pcm.core.entity.InterfaceProvidingEntity;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceContainer;
import de.uka.ipd.sdq.pcm.seff.ExternalCallAction;
import de.uka.ipd.sdq.pcm.usagemodel.EntryLevelSystemCall;
import de.uka.ipd.sdq.pcm.usagemodel.UsageScenario;
import de.uka.ipd.sdq.simucomframework.model.SimuComModel;
import de.uka.ipd.sdq.simucomframework.probes.TakeCurrentSimulationTimeProbe;

/**
 * Class for listening to interpreter events in order to store collected data using the
 * ProbeFramework
 * 
 * @author Steffen Becker, Sebastian Lehrig
 */
public class ProbeFrameworkListener extends AbstractInterpreterListener {

    private static final Logger LOG = Logger.getLogger(ProbeFrameworkListener.class);
    private static final int START_PROBE_INDEX = 0;
    private static final int STOP_PROBE_INDEX = 1;

    private final PMSAccess pmsModelAccess;
    private final PRMAccess prmAccess;
    private final SimuComModel simuComModel;
    private final ICalculatorFactory calculatorFactory;

    private final Map<EObject, List<TriggeredProbe>> currentTimeProbes = new HashMap<EObject, List<TriggeredProbe>>();
    private TriggeredProbe reconfTimeProbe;

    /** Default EMF factory for measuring points. */
    private final MeasuringpointFactory measuringpointFactory = MeasuringpointFactory.eINSTANCE;
    private final PcmmeasuringpointFactory pcmMeasuringpointFactory = PcmmeasuringpointFactory.eINSTANCE;

    /**
     * @param modelAccessFactory
     *            Provides access to simulated models
     * @param simuComModel
     *            Provides access to the central simulation
     */
    public ProbeFrameworkListener(final IModelAccessFactory modelAccessFactory, final SimuComModel simuComModel) {
        super();
        this.pmsModelAccess = modelAccessFactory.getPMSModelAccess();
        this.prmAccess = modelAccessFactory.getPRMModelAccess();
        this.calculatorFactory = simuComModel.getProbeFrameworkContext().getCalculatorFactory();
        this.simuComModel = simuComModel;
        this.reconfTimeProbe = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.upb.pcm.interpreter.interpreter.listener.AbstractInterpreterListener#
     * beginUsageScenarioInterpretation
     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
     */
    @Override
    public void beginUsageScenarioInterpretation(final ModelElementPassedEvent<UsageScenario> event) {
        this.startMeasurement(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.upb.pcm.interpreter.interpreter.listener.AbstractInterpreterListener#
     * endUsageScenarioInterpretation
     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
     */
    @Override
    public void endUsageScenarioInterpretation(final ModelElementPassedEvent<UsageScenario> event) {
        this.endMeasurement(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.upb.pcm.interpreter.interpreter.listener.AbstractInterpreterListener#
     * beginEntryLevelSystemCallInterpretation
     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
     */
    @Override
    public void beginEntryLevelSystemCallInterpretation(final ModelElementPassedEvent<EntryLevelSystemCall> event) {
        this.startMeasurement(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.upb.pcm.interpreter.interpreter.listener.AbstractInterpreterListener#
     * endEntryLevelSystemCallInterpretation
     * (de.upb.pcm.interpreter.interpreter.listener.ModelElementPassedEvent)
     */
    @Override
    public void endEntryLevelSystemCallInterpretation(final ModelElementPassedEvent<EntryLevelSystemCall> event) {
        this.endMeasurement(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.upb.pcm.simulizar.interpreter.listener.AbstractInterpreterListener#
     * beginExternalCallInterpretation
     * (de.upb.pcm.simulizar.interpreter.listener.ModelElementPassedEvent)
     */
    @Override
    public void beginExternalCallInterpretation(final RDSEFFElementPassedEvent<ExternalCallAction> event) {
        this.startMeasurement(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.upb.pcm.simulizar.interpreter.listener.AbstractInterpreterListener#
     * endExternalCallInterpretation
     * (de.upb.pcm.simulizar.interpreter.listener.ModelElementPassedEvent)
     */
    @Override
    public void endExternalCallInterpretation(final RDSEFFElementPassedEvent<ExternalCallAction> event) {
        this.endMeasurement(event);
    }

    /**
     * Initializes response time measurement.
     * 
     * @param event
     *            which was fired
     * @param <T>
     *            extends Entity
     */
    private <T extends Entity> void initReponseTimeMeasurement(final ModelElementPassedEvent<T> event) {
        final EObject modelElement = event.getModelElement();

        if (!entityIsAlreadyInstrumented(modelElement)) {
            final MeasuringPoint measuringPoint = createMeasuringPoint(modelElement);
            final SimuComModel simuComModel = event.getThread().getModel();
            final List<Probe> probeList = createStartAndStopProbe(modelElement, simuComModel);
            final Calculator calculator = calculatorFactory.buildResponseTimeCalculator(measuringPoint, probeList);

            final MeasurementSpecification measurementSpecification = this.pmsModelAccess.isMonitored(modelElement,
                    PerformanceMetricEnum.RESPONSE_TIME);
            if (elementShouldBeMonitored(measurementSpecification)) {
                try {
                    new ResponseTimeAggregator(this.prmAccess, measurementSpecification, calculator,
                            MeasuringPointUtility.measuringPointToString(measuringPoint), modelElement,
                            PrmFactory.eINSTANCE.createPCMModelElementMeasurement());
                } catch (final UnsupportedDataTypeException e) {
                    LOG.error(e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 
     * @param modelElement
     *            for which a MeasuringPoint shall be created
     * @param <T>
     *            extends Entity
     * @return MeasuringPoint for modelElement
     */
    private <T extends Entity> MeasuringPoint createMeasuringPoint(final EObject modelElement) {
        MeasuringPoint result;
        if (modelElement == null) {
            throw new IllegalArgumentException("ModelElementPassedEvent cannot be null");
        } else if (modelElement instanceof ResourceContainer) {
            final ResourceContainer resourceContainer = (ResourceContainer) modelElement;

            // FIXME Always takes the first active resource of a given container. That should be
            // more flexible. [Lehrig]
            final ActiveResourceMeasuringPoint mp = this.pcmMeasuringpointFactory.createActiveResourceMeasuringPoint();
            mp.setActiveResource(resourceContainer.getActiveResourceSpecifications_ResourceContainer().get(0));
            mp.setReplicaID(0);
            result = mp;
        } else if (modelElement instanceof ExternalCallAction) {
            final ExternalCallAction externalCallAction = (ExternalCallAction) modelElement;

            final StringMeasuringPoint mp = measuringpointFactory.createStringMeasuringPoint();
            mp.setMeasuringPoint("UNKOWN ASSEMBLY " + "Role: "
                    + externalCallAction.getCalledService_ExternalService().getEntityName() + "Operation: "
                    + externalCallAction.getRole_ExternalService().getEntityName());

            // FIXME Do not use StringMeasuringPoint but implement some nice solution using
            // AssemblyOperationMeasuringPoint. The current problem is that an event does not
            // provide the assembly as shown below. [Lehrig]

            // AssemblyOperationMeasuringPoint mp =
            // this.pcmMeasuringpointFactory.createAssemblyOperationMeasuringPoint();
            // mp.setAssembly(???);
            // mp.setOperationSignature(externalCallAction.getCalledService_ExternalService());
            // mp.setRole(externalCallAction.getRole_ExternalService());
            result = mp;
        } else if (modelElement instanceof EntryLevelSystemCall) {
            final EntryLevelSystemCall entryLevelSystemCall = (EntryLevelSystemCall) modelElement;

            // final StringMeasuringPoint mp = measuringpointFactory.createStringMeasuringPoint();
            // mp.setMeasuringPoint("UNKOWN SYSTEM " + "Role: "
            // + entryLevelSystemCall.getProvidedRole_EntryLevelSystemCall().getEntityName() +
            // "Operation: "
            // +
            // entryLevelSystemCall.getOperationSignature__EntryLevelSystemCall().getEntityName());

            final SystemOperationMeasuringPoint mp = this.pcmMeasuringpointFactory
                    .createSystemOperationMeasuringPoint();
            final InterfaceProvidingEntity providingEntity = entryLevelSystemCall
                    .getProvidedRole_EntryLevelSystemCall().getProvidingEntity_ProvidedRole();
            if (providingEntity instanceof de.uka.ipd.sdq.pcm.system.System) {
                de.uka.ipd.sdq.pcm.system.System system = (de.uka.ipd.sdq.pcm.system.System) providingEntity;
                mp.setSystem(system);
            } else {
                throw new IllegalArgumentException("EntryLevelSystemCall \"" + entryLevelSystemCall.getEntityName()
                        + "\" does not reference a system.");
            }
            mp.setOperationSignature(entryLevelSystemCall.getOperationSignature__EntryLevelSystemCall());
            mp.setRole(entryLevelSystemCall.getProvidedRole_EntryLevelSystemCall());
            result = mp;
        } else if (modelElement instanceof UsageScenario) {
            final UsageScenario usageScenario = (UsageScenario) modelElement;

            final UsageScenarioMeasuringPoint mp = this.pcmMeasuringpointFactory.createUsageScenarioMeasuringPoint();
            mp.setUsageScenario(usageScenario);
            result = mp;
        } else {
            throw new IllegalArgumentException("Unknown model element  (" + modelElement.toString() + ")");
        }
        return result;
    }

    /**
     * Initializes reconfiguration time measurement.
     * 
     * FIXME I would bet that too many StringMeasuringPoints are created here, potentially leading
     * to Exceptions [Lehrig]
     * 
     * TODO StringMeasuringPoint should not be used by SimuLizar. Create something better! I could
     * imagine an EDP2 extension that introduces dedicated reconfiguration measuring points.
     * [Lehrig]
     * 
     * @param event
     *            which was fired
     * @param <T>
     *            extends Entity
     */
    private <T extends Entity> void initReconfTimeMeasurement(final ReconfigurationEvent event) {

        final SimuComModel simuComModel = event.getModel();

        this.reconfTimeProbe = new TakeCurrentSimulationTimeProbe(simuComModel.getSimulationControl());

        final StringMeasuringPoint measuringPoint = measuringpointFactory.createStringMeasuringPoint();
        measuringPoint.setMeasuringPoint("Reconfiguration");
        this.calculatorFactory.buildStateOfActiveResourceCalculator(measuringPoint, this.reconfTimeProbe);
    }

    /**
     * @param modelElement
     * @param simuComModel
     * @return list with start and stop probe
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<Probe> createStartAndStopProbe(final EObject modelElement, final SimuComModel simuComModel) {
        final List probeList = new ArrayList<TriggeredProbe>(2);
        probeList.add(new TakeCurrentSimulationTimeProbe(simuComModel.getSimulationControl()));
        probeList.add(new TakeCurrentSimulationTimeProbe(simuComModel.getSimulationControl()));
        currentTimeProbes.put(modelElement, Collections.unmodifiableList(probeList));
        return probeList;
    }

    /**
     * @param measurementSpecification
     * @return
     */
    protected boolean elementShouldBeMonitored(final MeasurementSpecification measurementSpecification) {
        return measurementSpecification != null;
    }

    /**
     * @param modelElement
     * @return
     */
    protected boolean entityIsAlreadyInstrumented(final EObject modelElement) {
        return this.currentTimeProbes.containsKey(modelElement);
    }

    /**
     * @param <T>
     * @param event
     */
    private <T extends Entity> void startMeasurement(final ModelElementPassedEvent<T> event) {
        this.initReponseTimeMeasurement(event);
        if (this.currentTimeProbes.containsKey(event.getModelElement()) && simulationIsRunning()) {
            this.currentTimeProbes.get(event.getModelElement()).get(START_PROBE_INDEX)
                    .takeMeasurement(event.getThread().getRequestContext());
        }
    }

    /**
     * @param event
     */
    private <T extends Entity> void endMeasurement(final ModelElementPassedEvent<T> event) {
        if (this.currentTimeProbes.containsKey(event.getModelElement()) && simulationIsRunning()) {
            this.currentTimeProbes.get(event.getModelElement()).get(STOP_PROBE_INDEX)
                    .takeMeasurement(event.getThread().getRequestContext());
        }
    }

    @Override
    public void reconfigurationInterpretation(final ReconfigurationEvent event) {
        if (this.reconfTimeProbe == null) {
            initReconfTimeMeasurement(event);
        } else {
            this.reconfTimeProbe.takeMeasurement();
        }
    }

    private Boolean simulationIsRunning() {
        return simuComModel.getSimulationControl().isRunning();
    }
}
