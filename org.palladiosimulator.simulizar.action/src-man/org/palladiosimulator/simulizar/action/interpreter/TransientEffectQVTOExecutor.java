package org.palladiosimulator.simulizar.action.interpreter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.m2m.qvt.oml.ExecutionDiagnostic;
import org.eclipse.m2m.qvt.oml.ModelExtent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.simulizar.action.core.EnactAdaptationAction;
import org.palladiosimulator.simulizar.action.core.GuardedAdaptationBehavior;
import org.palladiosimulator.simulizar.action.core.ResourceDemandingAction;
import org.palladiosimulator.simulizar.action.core.StateTransformingAction;
import org.palladiosimulator.simulizar.action.mapping.Mapping;
import org.palladiosimulator.simulizar.action.mapping.MappingPackage;
import org.palladiosimulator.simulizar.reconfiguration.qvto.AbstractQVTOExecutor;
import org.palladiosimulator.simulizar.reconfiguration.qvto.util.QVToModelCache;
import org.palladiosimulator.simulizar.reconfiguration.qvto.util.TransformationCache;
import org.palladiosimulator.simulizar.reconfiguration.qvto.util.TransformationData;
import org.palladiosimulator.simulizar.reconfiguration.qvto.util.TransformationParameterInformation;

/**
 * Implementation of the {@link AbstractQVTOExecutor} suitable for executing {@link AdaptationStep
 * AdaptationSteps}, i.e., transient effects.
 * 
 * @author Florian Rosenthal
 *
 */
class TransientEffectQVTOExecutor extends AbstractQVTOExecutor {

    private static final EPackage MAPPING_EPACKAGE = MappingPackage.Literals.MAPPING.getEPackage();
    private static final EPackage REPOSITORY_EPACKAGE = RepositoryPackage.Literals.REPOSITORY.getEPackage();

    private final Collection<ModelExtent> currentPureOutParams;

    protected TransientEffectQVTOExecutor(QVToModelCache availableModels) {
        super(new TransformationCache(), Objects.requireNonNull(availableModels));
        this.currentPureOutParams = new ArrayList<>();

    }

    boolean executeTransformation(String uriString) {
        return this.executeTransformation(URI.createURI(Objects.requireNonNull(uriString)));
    }

    Optional<Mapping> executeControllerCompletion(Repository controllerCompletionRepository,
            String controllerCompletionPath) {
        // cache repo, if present
        Optional<EObject> cachedRepo = this.getModelByType(REPOSITORY_EPACKAGE);
        this.storeModel(controllerCompletionRepository);
        URI controllerCompletionUri = URI.createURI(controllerCompletionPath);
        boolean result = this.executeTransformation(controllerCompletionUri);
        // restore if necessary
        cachedRepo.ifPresent(repoInCache -> this.storeModel(repoInCache));
        if (result) {
            return this.getModelByType(MAPPING_EPACKAGE).map(obj -> (Mapping) obj);
        }
        return Optional.empty();
    }

    private void storeModel(EObject model) {
        getAvailableModels().storeModel(model);
    }

    private void prepareTransformation(String transformationUri) {
        assert transformationUri != null;

        URI uri = URI.createURI(Objects.requireNonNull(transformationUri));
        if (!getAvailableTransformations().contains(uri)) {
            getAvailableTransformations().store(uri);
        }
    }

    void enableForTransformationExecution(GuardedAdaptationBehavior guardedAdaptationBehavior) {
        storeModel(Objects.requireNonNull(guardedAdaptationBehavior));
        prepareTransformation(guardedAdaptationBehavior.getPreconditionURI());

    }

    void enableForTransformationExecution(EnactAdaptationAction enactAdaptationAction) {
        storeModel(Objects.requireNonNull(enactAdaptationAction));
        prepareTransformation(enactAdaptationAction.getAdaptationStepURI());
    }

    void enableForTransformationExecution(ResourceDemandingAction resourceDemandingAction) {
        storeModel(Objects.requireNonNull(resourceDemandingAction));
        prepareTransformation(resourceDemandingAction.getControllerCompletionURI());
    }

    void enableForTransformationExecution(StateTransformingAction stateTransformingAction) {
        storeModel(Objects.requireNonNull(stateTransformingAction));
    }

    Optional<TransformationData> getTransformationByUri(URI transformationId) {
        return getAvailableTransformations().get(Objects.requireNonNull(transformationId));
    }

    Optional<EObject> getModelByType(EPackage modelType) {
        return getAvailableModels().getModelByType(Objects.requireNonNull(modelType));
    }

    @Override
    protected boolean handleExecutionResult(ExecutionDiagnostic executionResult) {
        boolean result = super.handleExecutionResult(executionResult);
        if (result) {
            this.currentPureOutParams.stream().map(ModelExtent::getContents).filter(contents -> !contents.isEmpty())
                    .map(contents -> contents.get(0)).forEach(getAvailableModels()::storeModel);
        }
        return result;
    }

    @Override
    protected ModelExtent[] setupModelExtents(TransformationData data) {
        this.currentPureOutParams.clear();
        ModelExtent[] result = super.setupModelExtents(data);
        data.getPureOutParameters().stream().mapToInt(TransformationParameterInformation::getParameterIndex)
                .mapToObj(index -> result[index]).forEach(this.currentPureOutParams::add);
        return result;
    }
}