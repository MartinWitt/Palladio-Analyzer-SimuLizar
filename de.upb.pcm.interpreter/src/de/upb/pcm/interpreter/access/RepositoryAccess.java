/**
 * 
 */
package de.upb.pcm.interpreter.access;

import de.uka.ipd.sdq.pcm.repository.Repository;
import de.upb.pcm.interpreter.interpreter.InterpreterDefaultContext;
import de.upb.pcm.interpreter.utils.PCMModels;

/**
 * Abstract interpreter base class for repository and rdseff interpreter.
 * 
 * @author Joachim Meyer
 */
public class RepositoryAccess extends
		AbstractPCMModelAccess<Repository> 
{
	/**
	 * Constructor
	 * 
	 * @param contex
	 *            the interpreter default context for the pcm model interpreter,
	 *            may be null.
	 * @param modelHelper
	 *            the model helper.
	 */
	public RepositoryAccess(final InterpreterDefaultContext context,
			final ModelHelper modelHelper) {
		super(context, modelHelper);
	}

	@Override
	protected Repository getSpecificModel(PCMModels models) {
		return models.getRepository();
	}
}
