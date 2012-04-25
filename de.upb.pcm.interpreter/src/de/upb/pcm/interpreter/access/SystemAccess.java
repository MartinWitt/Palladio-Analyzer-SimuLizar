package de.upb.pcm.interpreter.access;

import de.uka.ipd.sdq.pcm.system.System;
import de.upb.pcm.interpreter.interpreter.InterpreterDefaultContext;
import de.upb.pcm.interpreter.utils.PCMModels;

/**
 * Access class for system model.
 * 
 * @author Joachim Meyer
 */
public class SystemAccess extends AbstractPCMModelAccess<System> {
	/**
	 * Constructor
	 * 
	 * @param context
	 *            the interpreter default context.
	 * @param modelHelper
	 *            the model helper.
	 */
	public SystemAccess(final InterpreterDefaultContext context,
			final ModelHelper modelHelper) {
		super(context, modelHelper);
	}

	@Override
	protected System getSpecificModel(PCMModels models) {
		return models.getSystem();
	}
}
