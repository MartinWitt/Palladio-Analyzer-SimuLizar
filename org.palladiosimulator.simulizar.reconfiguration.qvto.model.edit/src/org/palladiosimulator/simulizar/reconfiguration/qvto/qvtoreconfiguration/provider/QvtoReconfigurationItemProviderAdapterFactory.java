/**
 */
package org.palladiosimulator.simulizar.reconfiguration.qvto.qvtoreconfiguration.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IChildCreationExtender;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.ocl.ecore.EcorePackage;
import org.eclipse.ocl.ecore.util.EcoreSwitch;
import org.eclipse.ocl.expressions.ExpressionsPackage;
import org.eclipse.ocl.expressions.MessageExp;
import org.eclipse.ocl.expressions.TupleLiteralPart;
import org.eclipse.ocl.expressions.util.ExpressionsSwitch;
import org.eclipse.ocl.utilities.ExpressionInOCL;
import org.eclipse.ocl.utilities.UtilitiesPackage;
import org.eclipse.ocl.utilities.util.UtilitiesSwitch;
import org.palladiosimulator.simulizar.reconfiguration.qvto.qvtoreconfiguration.QvtoReconfigurationFactory;
import org.palladiosimulator.simulizar.reconfiguration.qvto.qvtoreconfiguration.util.QvtoReconfigurationAdapterFactory;
import org.palladiosimulator.simulizar.reconfigurationrule.ReconfigurationRule;
import org.palladiosimulator.simulizar.reconfigurationrule.reconfigurationrulePackage;
import org.palladiosimulator.simulizar.reconfigurationrule.util.reconfigurationruleSwitch;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers. The
 * adapters generated by this factory convert EMF adapter notifications into calls to
 * {@link #fireNotifyChanged fireNotifyChanged}. The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * 
 * @generated
 */
public class QvtoReconfigurationItemProviderAdapterFactory extends QvtoReconfigurationAdapterFactory implements
        ComposeableAdapterFactory, IChangeNotifier, IDisposable {
    /**
     * This keeps track of the root adapter factory that delegates to this adapter factory. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ComposedAdapterFactory parentAdapterFactory;

    /**
     * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected IChangeNotifier changeNotifier = new ChangeNotifier();

    /**
     * This keeps track of all the supported types checked by {@link #isFactoryForType
     * isFactoryForType}. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected Collection<Object> supportedTypes = new ArrayList<Object>();

    /**
     * This constructs an instance. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public QvtoReconfigurationItemProviderAdapterFactory() {
        supportedTypes.add(IEditingDomainItemProvider.class);
        supportedTypes.add(IStructuredItemContentProvider.class);
        supportedTypes.add(ITreeItemContentProvider.class);
        supportedTypes.add(IItemLabelProvider.class);
        supportedTypes.add(IItemPropertySource.class);
    }

    /**
     * This keeps track of the one adapter used for all
     * {@link org.palladiosimulator.simulizar.reconfiguration.qvto.qvtoreconfiguration.QvtoTransformation}
     * instances. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected QvtoTransformationItemProvider qvtoTransformationItemProvider;

    /**
     * This creates an adapter for a
     * {@link org.palladiosimulator.simulizar.reconfiguration.qvto.qvtoreconfiguration.QvtoTransformation}
     * . <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Adapter createQvtoTransformationAdapter() {
        if (qvtoTransformationItemProvider == null) {
            qvtoTransformationItemProvider = new QvtoTransformationItemProvider(this);
        }

        return qvtoTransformationItemProvider;
    }

    /**
     * This returns the root adapter factory that contains this factory. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ComposeableAdapterFactory getRootAdapterFactory() {
        return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
    }

    /**
     * This sets the composed adapter factory that contains this factory. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
        this.parentAdapterFactory = parentAdapterFactory;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object type) {
        return supportedTypes.contains(type) || super.isFactoryForType(type);
    }

    /**
     * This implementation substitutes the factory itself as the key for the adapter. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Adapter adapt(Notifier notifier, Object type) {
        return super.adapt(notifier, this);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object adapt(Object object, Object type) {
        if (isFactoryForType(type)) {
            Object adapter = super.adapt(object, type);
            if (!(type instanceof Class<?>) || (((Class<?>) type).isInstance(adapter))) {
                return adapter;
            }
        }

        return null;
    }

    /**
     * This adds a listener. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void addListener(INotifyChangedListener notifyChangedListener) {
        changeNotifier.addListener(notifyChangedListener);
    }

    /**
     * This removes a listener. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void removeListener(INotifyChangedListener notifyChangedListener) {
        changeNotifier.removeListener(notifyChangedListener);
    }

    /**
     * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void fireNotifyChanged(Notification notification) {
        changeNotifier.fireNotifyChanged(notification);

        if (parentAdapterFactory != null) {
            parentAdapterFactory.fireNotifyChanged(notification);
        }
    }

    /**
     * This disposes all of the item providers created by this factory. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void dispose() {
        if (qvtoTransformationItemProvider != null)
            qvtoTransformationItemProvider.dispose();
    }

    /**
     * A child creation extender for the {@link UtilitiesPackage}. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    public static class UtilitiesChildCreationExtender implements IChildCreationExtender {
        /**
         * The switch for creating child descriptors specific to each extended class. <!--
         * begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        protected static class CreationSwitch extends UtilitiesSwitch<Object> {
            /**
             * The child descriptors being populated. <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            protected List<Object> newChildDescriptors;

            /**
             * The domain in which to create the children. <!-- begin-user-doc --> <!-- end-user-doc
             * -->
             * 
             * @generated
             */
            protected EditingDomain editingDomain;

            /**
             * Creates the a switch for populating child descriptors in the given domain. <!--
             * begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            CreationSwitch(List<Object> newChildDescriptors, EditingDomain editingDomain) {
                this.newChildDescriptors = newChildDescriptors;
                this.editingDomain = editingDomain;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            @Override
            public <C, PM> Object caseExpressionInOCL(ExpressionInOCL<C, PM> object) {
                newChildDescriptors.add(createChildParameter(
                        UtilitiesPackage.Literals.EXPRESSION_IN_OCL__GENERATED_TYPE,
                        QvtoReconfigurationFactory.eINSTANCE.createQvtoTransformation()));

                return null;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            protected CommandParameter createChildParameter(Object feature, Object child) {
                return new CommandParameter(null, feature, child);
            }

        }

        /**
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        @Override
        public Collection<Object> getNewChildDescriptors(Object object, EditingDomain editingDomain) {
            ArrayList<Object> result = new ArrayList<Object>();
            new CreationSwitch(result, editingDomain).doSwitch((EObject) object);
            return result;
        }

        /**
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        @Override
        public ResourceLocator getResourceLocator() {
            return QvtoreconfigurationEditPlugin.INSTANCE;
        }
    }

    /**
     * A child creation extender for the {@link ExpressionsPackage}. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    public static class ExpressionsChildCreationExtender implements IChildCreationExtender {
        /**
         * The switch for creating child descriptors specific to each extended class. <!--
         * begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        protected static class CreationSwitch extends ExpressionsSwitch<Object> {
            /**
             * The child descriptors being populated. <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            protected List<Object> newChildDescriptors;

            /**
             * The domain in which to create the children. <!-- begin-user-doc --> <!-- end-user-doc
             * -->
             * 
             * @generated
             */
            protected EditingDomain editingDomain;

            /**
             * Creates the a switch for populating child descriptors in the given domain. <!--
             * begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            CreationSwitch(List<Object> newChildDescriptors, EditingDomain editingDomain) {
                this.newChildDescriptors = newChildDescriptors;
                this.editingDomain = editingDomain;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            @Override
            public <C, COA, SSA> Object caseMessageExp(MessageExp<C, COA, SSA> object) {
                newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MESSAGE_EXP__CALLED_OPERATION,
                        QvtoReconfigurationFactory.eINSTANCE.createQvtoTransformation()));

                newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MESSAGE_EXP__SENT_SIGNAL,
                        QvtoReconfigurationFactory.eINSTANCE.createQvtoTransformation()));

                return null;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            @Override
            public <C, P> Object caseTupleLiteralPart(TupleLiteralPart<C, P> object) {
                newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.TUPLE_LITERAL_PART__ATTRIBUTE,
                        QvtoReconfigurationFactory.eINSTANCE.createQvtoTransformation()));

                return null;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            protected CommandParameter createChildParameter(Object feature, Object child) {
                return new CommandParameter(null, feature, child);
            }

        }

        /**
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        @Override
        public Collection<Object> getNewChildDescriptors(Object object, EditingDomain editingDomain) {
            ArrayList<Object> result = new ArrayList<Object>();
            new CreationSwitch(result, editingDomain).doSwitch((EObject) object);
            return result;
        }

        /**
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        @Override
        public ResourceLocator getResourceLocator() {
            return QvtoreconfigurationEditPlugin.INSTANCE;
        }
    }

    /**
     * A child creation extender for the {@link EcorePackage}. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    public static class EcoreChildCreationExtender implements IChildCreationExtender {
        /**
         * The switch for creating child descriptors specific to each extended class. <!--
         * begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        protected static class CreationSwitch extends EcoreSwitch<Object> {
            /**
             * The child descriptors being populated. <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            protected List<Object> newChildDescriptors;

            /**
             * The domain in which to create the children. <!-- begin-user-doc --> <!-- end-user-doc
             * -->
             * 
             * @generated
             */
            protected EditingDomain editingDomain;

            /**
             * Creates the a switch for populating child descriptors in the given domain. <!--
             * begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            CreationSwitch(List<Object> newChildDescriptors, EditingDomain editingDomain) {
                this.newChildDescriptors = newChildDescriptors;
                this.editingDomain = editingDomain;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated not
             */
            public <C, COA, SSA> Object caseMessageExp(MessageExp<C, COA, SSA> object) {
                newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MESSAGE_EXP__CALLED_OPERATION,
                        QvtoReconfigurationFactory.eINSTANCE.createQvtoTransformation()));

                newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.MESSAGE_EXP__SENT_SIGNAL,
                        QvtoReconfigurationFactory.eINSTANCE.createQvtoTransformation()));

                return null;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated not
             */
            public <C, P> Object caseTupleLiteralPart(TupleLiteralPart<C, P> object) {
                newChildDescriptors.add(createChildParameter(ExpressionsPackage.Literals.TUPLE_LITERAL_PART__ATTRIBUTE,
                        QvtoReconfigurationFactory.eINSTANCE.createQvtoTransformation()));

                return null;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            protected CommandParameter createChildParameter(Object feature, Object child) {
                return new CommandParameter(null, feature, child);
            }

        }

        /**
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        @Override
        public Collection<Object> getNewChildDescriptors(Object object, EditingDomain editingDomain) {
            ArrayList<Object> result = new ArrayList<Object>();
            new CreationSwitch(result, editingDomain).doSwitch((EObject) object);
            return result;
        }

        /**
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        @Override
        public ResourceLocator getResourceLocator() {
            return QvtoreconfigurationEditPlugin.INSTANCE;
        }
    }

    /**
     * A child creation extender for the {@link reconfigurationrulePackage}. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    public static class reconfigurationruleChildCreationExtender implements IChildCreationExtender {
        /**
         * The switch for creating child descriptors specific to each extended class. <!--
         * begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        protected static class CreationSwitch extends reconfigurationruleSwitch<Object> {
            /**
             * The child descriptors being populated. <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            protected List<Object> newChildDescriptors;

            /**
             * The domain in which to create the children. <!-- begin-user-doc --> <!-- end-user-doc
             * -->
             * 
             * @generated
             */
            protected EditingDomain editingDomain;

            /**
             * Creates the a switch for populating child descriptors in the given domain. <!--
             * begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            CreationSwitch(List<Object> newChildDescriptors, EditingDomain editingDomain) {
                this.newChildDescriptors = newChildDescriptors;
                this.editingDomain = editingDomain;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            @Override
            public Object caseReconfigurationRule(ReconfigurationRule object) {
                newChildDescriptors.add(createChildParameter(
                        reconfigurationrulePackage.Literals.RECONFIGURATION_RULE__CONDITION_CHECK,
                        QvtoReconfigurationFactory.eINSTANCE.createQvtoTransformation()));

                newChildDescriptors.add(createChildParameter(
                        reconfigurationrulePackage.Literals.RECONFIGURATION_RULE__TRANSFORMATION_ACTION,
                        QvtoReconfigurationFactory.eINSTANCE.createQvtoTransformation()));

                return null;
            }

            /**
             * <!-- begin-user-doc --> <!-- end-user-doc -->
             * 
             * @generated
             */
            protected CommandParameter createChildParameter(Object feature, Object child) {
                return new CommandParameter(null, feature, child);
            }

        }

        /**
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        @Override
        public Collection<Object> getNewChildDescriptors(Object object, EditingDomain editingDomain) {
            ArrayList<Object> result = new ArrayList<Object>();
            new CreationSwitch(result, editingDomain).doSwitch((EObject) object);
            return result;
        }

        /**
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        @Override
        public ResourceLocator getResourceLocator() {
            return QvtoreconfigurationEditPlugin.INSTANCE;
        }
    }

}
