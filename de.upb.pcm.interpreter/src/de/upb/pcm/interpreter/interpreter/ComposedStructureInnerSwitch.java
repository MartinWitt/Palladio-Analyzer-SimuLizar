package de.upb.pcm.interpreter.interpreter;

import org.apache.log4j.Logger;

import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.core.composition.Connector;
import de.uka.ipd.sdq.pcm.core.composition.RequiredDelegationConnector;
import de.uka.ipd.sdq.pcm.core.composition.util.CompositionSwitch;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;
import de.uka.ipd.sdq.simucomframework.variables.stackframe.SimulatedStackframe;
import de.upb.pcm.interpreter.access.IModelAccessFactory;
import de.upb.pcm.interpreter.exceptions.PCMModelInterpreterException;

class ComposedStructureInnerSwitch extends CompositionSwitch<SimulatedStackframe<Object>> {
	protected static final Logger logger = Logger
			.getLogger(ProvidedDelegationSwitch.class.getName());

	private final InterpreterDefaultContext context;
	private final IModelAccessFactory modelAccessFactory;
	private final OperationSignature operationSignature;
	private final RequiredRole requiredRole;

	/**
	 * Constructor
	 * 
	 * @param modelInterpreter
	 *            the corresponding pcm model interpreter holding this switch..
	 */
	public ComposedStructureInnerSwitch(
			final InterpreterDefaultContext context,
			final IModelAccessFactory interpreterFactory,
			final OperationSignature operationSignature, 
			final RequiredRole requiredRole) {
		super();
		this.context = context;
		this.modelAccessFactory = interpreterFactory;
		this.operationSignature = operationSignature;
		this.requiredRole = requiredRole;
	}

	@Override
	public SimulatedStackframe<Object> caseAssemblyConnector(AssemblyConnector assemblyConnector) {
		RepositoryComponentSwitch repositoryComponentSwitch = 
				new RepositoryComponentSwitch(
						context, 
						modelAccessFactory, 
						assemblyConnector.getProvidingAssemblyContext_AssemblyConnector(), 
						this.operationSignature, 
						assemblyConnector.getProvidedRole_AssemblyConnector());
		return repositoryComponentSwitch.doSwitch(assemblyConnector.getProvidedRole_AssemblyConnector());
	}
	
	@Override
	public SimulatedStackframe<Object> caseRequiredDelegationConnector(RequiredDelegationConnector requiredDelegationConnector) {
		AssemblyContext parentContext = this.context.getAssemblyContextStack().pop();
		ComposedStructureInnerSwitch composedStructureInnerSwitch = 
				new ComposedStructureInnerSwitch(
						context, 
						modelAccessFactory, 
						operationSignature, 
						requiredDelegationConnector.getOuterRequiredRole_RequiredDelegationConnector());
		SimulatedStackframe<Object> result = composedStructureInnerSwitch.doSwitch(parentContext);
		this.context.getAssemblyContextStack().push(parentContext);
		return result;
	}

	@Override
	public SimulatedStackframe<Object> caseAssemblyContext(AssemblyContext assemblyContext) {
		Connector connector = getConnectedConnector(assemblyContext, requiredRole);
		this.doSwitch(connector);
		return super.caseAssemblyContext(assemblyContext);
	}

	/**
	 * Determines the assembly connector which is connected with the required
	 * role.
	 * 
	 * @param requiredRole
	 *            the required role.
	 * @return the determined assembly connector, null otherwise.
	 */
	private static Connector getConnectedConnector(
			final AssemblyContext myContext,
			final RequiredRole requiredRole) {
		if (myContext == null)
			throw new IllegalArgumentException("Assembly context must not be null");
		if (requiredRole == null)
			throw new IllegalArgumentException("Required role must not be null");
		final CompositionSwitch<Connector> connectorSelector = new CompositionSwitch<Connector>(){

			@Override
			public Connector caseRequiredDelegationConnector(RequiredDelegationConnector delegationConnector) {
				if (delegationConnector.getAssemblyContext_RequiredDelegationConnector() == myContext &&
					delegationConnector.getInnerRequiredRole_RequiredDelegationConnector() == requiredRole)
					return delegationConnector;
				return null;
			}

			@Override
			public Connector caseAssemblyConnector(AssemblyConnector assemblyConnector) {
				if (assemblyConnector.getRequiringAssemblyContext_AssemblyConnector() == myContext && 
					assemblyConnector.getRequiredRole_AssemblyConnector() == requiredRole)
					return assemblyConnector;
				return null;
			} 
			
		};
		for (final Connector connector : myContext.getParentStructure__AssemblyContext().getConnectors__ComposedStructure()) {
			Connector result = connectorSelector.doSwitch(connector);
			if (result != null)
				return result;
		}
		throw new PCMModelInterpreterException("Found unbound provided role. PCM model is invalid.");
	}	
}
