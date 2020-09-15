package pi.usage.simulizar;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.util.ComposedSwitch;
import org.palladiosimulator.simulizar.interpreter.InterpreterDefaultContext;
import org.palladiosimulator.simulizar.utils.TransitionDeterminer;

import base.Entity;
import base.NamedElement;
import de.uka.ipd.sdq.identifier.Identifier;
import de.uka.ipd.sdq.simucomframework.variables.StackContext;
import usage.AbstractUserAction;
import usage.Branch;
import usage.BranchTransition;
import usage.Delay;
import usage.Loop;
import usage.ScenarioBehaviour;
import usage.Start;
import usage.Stop;
import usage.util.UsageSwitch;

public class PiUsageSwitch<T> extends UsageSwitch<T> {

    private InterpreterDefaultContext context;
    private  Logger LOGGER = Logger.getLogger(PiUsageSwitch.class);
    private  TransitionDeterminer transitionDeterminer;
    private  ComposedSwitch<T> parentSwitch;

    public PiUsageSwitch(InterpreterDefaultContext context, ComposedSwitch<T> parentSwitch ){
        this.context = context;
        this.transitionDeterminer = new TransitionDeterminer(context);
        this.parentSwitch = parentSwitch;
        
      }
    /**
     * exists for serviceloader reasons only
     * @param context
     * @param parentSwitch
     */
    public void init(InterpreterDefaultContext context, ComposedSwitch<T> parentSwitch ) {
        this.context = context;
        this.transitionDeterminer = new TransitionDeterminer(context);
        this.parentSwitch = parentSwitch;
    }
	@Override
	public T caseScenarioBehaviour(ScenarioBehaviour object) {
        // interpret start user action
        for (final AbstractUserAction abstractUserAction : object.getActions_ScenarioBehaviour()) {
            if (abstractUserAction instanceof Start) {
                parentSwitch.doSwitch(abstractUserAction);
                break;
            }
        }
        return super.caseScenarioBehaviour(object);
	}

	@Override
	public T caseAbstractUserAction(AbstractUserAction object) {
        if (object.getSuccessor() != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Interpret " + object.getSuccessor().eClass().getName() + ": " + object);
            }
            parentSwitch.doSwitch(object.getSuccessor());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Finished Interpretation of " + object.getSuccessor().eClass().getName() + ": " + object);
            }
        }
        return super.caseAbstractUserAction(object);
	}


	@Override
	public T caseBranch(Branch object) {
        // determine branch transition
        final BranchTransition branchTransition = this.transitionDeterminer
                .determineBranchTransition(object.getBranchTransitions_Branch());

        // interpret scenario behaviour of branch transition
        parentSwitch.doSwitch(branchTransition.getBranchedBehaviour_BranchTransition());
        return super.caseBranch(object);
	}



	@Override
	public T caseLoop(Loop object) {
        // determine number of loops
        final int numberOfLoops = StackContext.evaluateStatic(object.getLoopIteration_Loop().getSpecification(),
                Integer.class);
        for (int i = 0; i < numberOfLoops; i++) {
            LOGGER.debug("Interpret loop number " + i);
            parentSwitch.doSwitch(object.getBodyBehaviour_Loop());
            LOGGER.debug("Finished loop number " + i);
        }
        return super.caseLoop(object);
	}

	@Override
	public T caseDelay(Delay object) {
        // determine delay
        final double delay = StackContext.evaluateStatic(object.getTimeSpecification_Delay().getSpecification(),
                Double.class);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Start delay " + delay + " @ simulation time "
                    + this.context.getModel().getSimulationControl().getCurrentSimulationTime());
        }
        // hold simulation process
        this.context.getThread().hold(delay);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Continue user @ simulation time "
                    + this.context.getModel().getSimulationControl().getCurrentSimulationTime());
        }
        return super.caseDelay(object);
	}



}
