package org.ovirt.engine.ui.webadmin.section.main.view.popup.host;

import java.util.Comparator;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.ovirt.engine.core.common.action.VdsOperationActionParameters.AuthenticationMethod;
import org.ovirt.engine.core.common.businessentities.Cluster;
import org.ovirt.engine.core.common.businessentities.ExternalEntityBase;
import org.ovirt.engine.core.common.businessentities.ExternalHostGroup;
import org.ovirt.engine.core.common.businessentities.HostedEngineDeployConfiguration;
import org.ovirt.engine.core.common.businessentities.Provider;
import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.core.common.mode.ApplicationMode;
import org.ovirt.engine.ui.common.CommonApplicationMessages;
import org.ovirt.engine.ui.common.css.OvirtCss;
import org.ovirt.engine.ui.common.editor.UiCommonEditorDriver;
import org.ovirt.engine.ui.common.idhandler.ElementIdHandler;
import org.ovirt.engine.ui.common.idhandler.WithElementId;
import org.ovirt.engine.ui.common.view.popup.AbstractTabbedModelBoundPopupView;
import org.ovirt.engine.ui.common.widget.AffinityLabelSelectionWithListWidget;
import org.ovirt.engine.ui.common.widget.Align;
import org.ovirt.engine.ui.common.widget.dialog.AdvancedParametersExpander;
import org.ovirt.engine.ui.common.widget.dialog.InfoIcon;
import org.ovirt.engine.ui.common.widget.dialog.SimpleDialogPanel;
import org.ovirt.engine.ui.common.widget.dialog.tab.DialogTab;
import org.ovirt.engine.ui.common.widget.dialog.tab.DialogTabPanel;
import org.ovirt.engine.ui.common.widget.editor.GroupedListModelListBox;
import org.ovirt.engine.ui.common.widget.editor.GroupedListModelListBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.ListModelListBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.ListModelTypeAheadListBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.generic.EntityModelCheckBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.generic.EntityModelRadioButtonEditor;
import org.ovirt.engine.ui.common.widget.editor.generic.IntegerEntityModelTextBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.generic.StringEntityModelPasswordBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.generic.StringEntityModelTextAreaLabelEditor;
import org.ovirt.engine.ui.common.widget.editor.generic.StringEntityModelTextBoxEditor;
import org.ovirt.engine.ui.common.widget.editor.generic.StringEntityModelTextBoxOnlyEditor;
import org.ovirt.engine.ui.common.widget.label.EnableableFormLabel;
import org.ovirt.engine.ui.common.widget.renderer.EnumRenderer;
import org.ovirt.engine.ui.common.widget.renderer.NameRenderer;
import org.ovirt.engine.ui.uicommonweb.UICommand;
import org.ovirt.engine.ui.uicommonweb.models.ApplicationModeHelper;
import org.ovirt.engine.ui.uicommonweb.models.EntityModel;
import org.ovirt.engine.ui.uicommonweb.models.TabName;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostModel;
import org.ovirt.engine.ui.uicompat.ConstantsManager;
import org.ovirt.engine.ui.uicompat.EventArgs;
import org.ovirt.engine.ui.uicompat.IEventListener;
import org.ovirt.engine.ui.webadmin.ApplicationConstants;
import org.ovirt.engine.ui.webadmin.ApplicationResources;
import org.ovirt.engine.ui.webadmin.ApplicationTemplates;
import org.ovirt.engine.ui.webadmin.gin.AssetProvider;
import org.ovirt.engine.ui.webadmin.section.main.presenter.popup.host.HostPopupPresenterWidget;
import org.ovirt.engine.ui.webadmin.widget.host.FenceAgentsEditor;
import org.ovirt.engine.ui.webadmin.widget.host.HostProxySourceEditor;
import org.ovirt.engine.ui.webadmin.widget.provider.HostNetworkProviderWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

public class HostPopupView extends AbstractTabbedModelBoundPopupView<HostModel> implements HostPopupPresenterWidget.ViewDef {

    interface Driver extends UiCommonEditorDriver<HostModel, HostPopupView> {
    }

    interface ViewUiBinder extends UiBinder<SimpleDialogPanel, HostPopupView> {

        ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);
    }

    interface ViewIdHandler extends ElementIdHandler<HostPopupView> {

        ViewIdHandler idHandler = GWT.create(ViewIdHandler.class);
    }

    @UiField
    DialogTabPanel tabPanel;

    @UiField
    Style style;

    @UiField
    @WithElementId
    DialogTab generalTab;

    @UiField
    @WithElementId
    DialogTab affinityLabelsTab;

    @UiField
    @WithElementId
    DialogTab powerManagementTab;

    @UiField(provided = true)
    @Path(value = "cluster.selectedItem")
    @WithElementId("cluster")
    GroupedListModelListBoxEditor<Cluster> clusterEditor;

    @UiField
    @Path(value = "name.entity")
    @WithElementId("name")
    StringEntityModelTextBoxEditor nameEditor;

    @UiField
    @Path(value = "userName.entity")
    @WithElementId("userName")
    StringEntityModelTextBoxEditor userNameEditor;

    @UiField
    @Path(value = "fetchSshFingerprint.entity")
    @WithElementId("fetchSshFingerprint")
    StringEntityModelTextBoxEditor fetchSshFingerprint;

    @UiField
    @Ignore
    @WithElementId("fetchResult")
    Label fetchResult;

    @UiField
    @Path(value = "comment.entity")
    @WithElementId("comment")
    StringEntityModelTextBoxEditor commentEditor;

    @UiField
    @Path(value = "providerSearchFilter.entity")
    @WithElementId("providerSearchFilter")
    StringEntityModelTextBoxEditor providerSearchFilterEditor;

    @UiField(provided = true)
    @Path(value = "externalHostName.selectedItem")
    @WithElementId("externalHostName")
    ListModelListBoxEditor<VDS> externalHostNameEditor;

    @UiField(provided = true)
    @Path(value = "providers.selectedItem")
    @WithElementId("providers")
    ListModelListBoxEditor<Provider<Provider.AdditionalProperties>> providersEditor;

    @UiField
    Row searchProviderRow;

    @UiField
    FlowPanel discoveredHostPanel;

    @UiField(provided = true)
    @Path(value = "externalDiscoveredHosts.selectedItem")
    @WithElementId("externalDiscoveredHosts")
    ListModelTypeAheadListBoxEditor<ExternalEntityBase> externalDiscoveredHostsEditor;

    @UiField(provided = true)
    @Path(value = "externalHostGroups.selectedItem")
    @WithElementId("externalHostGroups")
    ListModelTypeAheadListBoxEditor<ExternalEntityBase> externalHostGroupsEditor;

    @UiField(provided = true)
    @Path(value = "externalComputeResource.selectedItem")
    @WithElementId("externalComputeResource")
    ListModelTypeAheadListBoxEditor<ExternalEntityBase> externalComputeResourceEditor;

    @UiField
    @Path(value = "host.entity")
    @WithElementId("host")
    StringEntityModelTextBoxOnlyEditor hostAddressEditor;

    @UiField(provided = true)
    @Ignore
    InfoIcon hostAddressInfoIcon;

    @UiField
    @Ignore
    Label hostAddressLabel;

    @UiField
    @Path(value = "authSshPort.entity")
    @WithElementId("authSshPort")
    IntegerEntityModelTextBoxEditor authSshPortEditor;

    @UiField
    @Path(value = "userPassword.entity")
    @WithElementId("userPassword")
    StringEntityModelPasswordBoxEditor passwordEditor;

    @UiField(provided = true)
    @Path(value = "publicKey.entity")
    @WithElementId("publicKey")
    StringEntityModelTextAreaLabelEditor publicKeyEditor;

    @UiField(provided = true)
    @Path(value = "overrideIpTables.entity")
    @WithElementId("overrideIpTables")
    EntityModelCheckBoxEditor overrideIpTablesEditor;

    @UiField(provided = true)
    @Path(value = "isPm.entity")
    @WithElementId("isPm")
    EntityModelCheckBoxEditor pmEnabledEditor;

    @Path(value = "fenceAgentListModel")
    @UiField(provided = true)
    final FenceAgentsEditor fenceAgentsEditor;

    @Path(value = "pmProxyPreferencesList")
    @UiField(provided = true)
    HostProxySourceEditor proxySourceEditor;

    @UiField
    @Ignore
    AdvancedParametersExpander pmExpander;

    @UiField
    @Ignore
    FlowPanel pmExpanderContent;

    @UiField(provided = true)
    @Path(value = "externalHostProviderEnabled.entity")
    @WithElementId("externalHostProviderEnabled")
    EntityModelCheckBoxEditor externalHostProviderEnabledEditor;

    @UiField(provided = true)
    @Path(value = "disableAutomaticPowerManagement.entity")
    @WithElementId("disableAutomaticPowerManagementEditor")
    EntityModelCheckBoxEditor disableAutomaticPowerManagementEditor;

    @UiField(provided = true)
    @Path(value = "pmKdumpDetection.entity")
    @WithElementId("pmKdumpDetection")
    EntityModelCheckBoxEditor pmKdumpDetectionEditor;

    @UiField
    @Ignore
    SimplePanel fetchPanel;

    @UiField
    Image updateHostsButton;

    @UiField
    @Ignore
    DialogTab spmTab;

    @UiField
    @Ignore
    DialogTab consoleTab;

    @UiField
    @Ignore
    DialogTab networkProviderTab;

    @UiField
    @Ignore
    Container spmContainer;

    @UiField
    @Ignore
    DialogTab hostedEngineTab;

    @UiField
    @Path(value = "hostedEngineWarning.entity")
    Label hostedEngineWarningLabel;

    @UiField(provided=true)
    @Path(value = "hostedEngineHostModel.selectedItem")
    ListModelListBoxEditor<HostedEngineDeployConfiguration.Action> hostedEngineDeployActionsEditor;

    @UiField
    @Path(value = "pkSection.entity")
    @WithElementId("pkSection")
    Row pkSection;

    @UiField
    @Path(value = "passwordSection.entity")
    @WithElementId("passwordSection")
    Row passwordSection;

    @UiField
    @Path(value = "provisionedHostSection.entity")
    @WithElementId
    Column provisionedHostSection;

    @UiField
    @Path(value = "discoveredHostSection.entity")
    @WithElementId
    Column discoveredHostSection;

    @UiField(provided = true)
    @Ignore
    @WithElementId("rbPublicKey")
    public RadioButton rbPublicKey;

    @UiField
    @Ignore
    public Label rbPublicKeyLabel;

    @UiField(provided = true)
    @Ignore
    @WithElementId("rbPassword")
    public RadioButton rbPassword;

    @UiField
    @Ignore
    public Label rbPasswordLabel;

    @UiField(provided = true)
    @Ignore
    @WithElementId
    public EntityModelRadioButtonEditor rbProvisionedHost;

    @UiField(provided = true)
    @Ignore
    @WithElementId
    public EntityModelRadioButtonEditor rbDiscoveredHost;

    @UiField
    @Ignore
    Label authLabel;

    @UiField
    @Ignore
    Label rootPasswordLabel;

    @UiField
    @Ignore
    Label fingerprintLabel;

    @UiField(provided = true)
    @Ignore
    InfoIcon consoleAddressInfoIcon;

    @UiField(provided = true)
    @Ignore
    InfoIcon discoveredHostInfoIcon;

    @UiField(provided = true)
    @Ignore
    InfoIcon provisionedHostInfoIcon;

    @UiField
    @Path(value = "consoleAddress.entity")
    @WithElementId
    StringEntityModelTextBoxEditor consoleAddress;

    @UiField
    @Path(value = "providerSearchFilterLabel.entity")
    EnableableFormLabel providerSearchFilterLabel;

    @UiField(provided = true)
    @Path(value = "consoleAddressEnabled.entity")
    EntityModelCheckBoxEditor consoleAddressEnabled;

    @UiField(provided = true)
    InfoIcon providerSearchInfoIcon;

    @UiField
    @Ignore
    HostNetworkProviderWidget networkProviderWidget;

    @UiField
    @Ignore
    AdvancedParametersExpander expander;

    @UiField
    @Ignore
    Column expanderContent;

    @UiField
    @Ignore
    DialogTab kernelTab;

    @UiField
    @Path("currentKernelCmdLine.entity")
    Label currentKernelCmdLine;

    @UiField(provided = true)
    @Path("kernelCmdlineBlacklistNouveau.entity")
    EntityModelCheckBoxEditor kernelCmdlineBlacklistNouveau;

    @UiField
    InfoIcon kernelCmdlineBlacklistNouveauInfoIcon;

    @UiField(provided = true)
    @Path("kernelCmdlineIommu.entity")
    EntityModelCheckBoxEditor kernelCmdlineIommu;

    @UiField
    InfoIcon kernelCmdlineIommuInfoIcon;

    @UiField(provided = true)
    @Path("kernelCmdlineKvmNested.entity")
    EntityModelCheckBoxEditor kernelCmdlineKvmNested;

    @UiField
    InfoIcon kernelCmdlineKvmNestedInfoIcon;

    @UiField(provided = true)
    @Path("kernelCmdlineUnsafeInterrupts.entity")
    EntityModelCheckBoxEditor kernelCmdlineUnsafeInterrupts;

    @UiField
    InfoIcon kernelCmdlineUnsafeInterruptsInfoIcon;

    @UiField(provided = true)
    @Path("kernelCmdlinePciRealloc.entity")
    EntityModelCheckBoxEditor kernelCmdlinePciRealloc;

    @UiField
    InfoIcon kernelCmdlinePciReallocInfoIcon;

    @UiField
    @Path("kernelCmdline.entity")
    StringEntityModelTextBoxEditor kernelCmdlineText;

    @UiField
    InfoIcon kernelCmdlineInfoIcon;

    @UiField
    @Ignore
    Button kernelCmdlineResetButton;

    @UiField
    @Path(value = "labelList.selectedItem")
    @WithElementId("labelList")
    AffinityLabelSelectionWithListWidget affinityLabelSelectionWidget;

    private final Driver driver = GWT.create(Driver.class);

    private static final ApplicationTemplates templates = AssetProvider.getTemplates();
    private static final ApplicationResources resources = AssetProvider.getResources();
    private static final ApplicationConstants constants = AssetProvider.getConstants();
    private static final CommonApplicationMessages messages = AssetProvider.getMessages();

    @Inject
    public HostPopupView(EventBus eventBus, FenceAgentsEditor fenceAgentEditor,
            HostProxySourceEditor proxySourceEditor) {
        super(eventBus);

        this.fenceAgentsEditor = fenceAgentEditor;
        this.proxySourceEditor = proxySourceEditor;
        initEditors();
        initInfoIcon();
        initWidget(ViewUiBinder.uiBinder.createAndBindUi(this));
        hideEditorLabels();
        initExpander();
        ViewIdHandler.idHandler.generateAndSetIds(this);
        localize();
        addStyles();
        driver.initialize(this);
        applyModeCustomizations();
    }

    private void hideEditorLabels() {
        providersEditor.hideLabel();
        passwordEditor.hideLabel();
        publicKeyEditor.hideLabel();
        consoleAddress.hideLabel();
        kernelCmdlineText.hideLabel();
    }

    private void initInfoIcon() {
        consoleAddressInfoIcon =
                new InfoIcon(templates.italicText(constants.enableConsoleAddressOverrideHelpMessage()));
        providerSearchInfoIcon =
                new InfoIcon(templates.italicText(constants.providerSearchInfo()));
        provisionedHostInfoIcon =
                new InfoIcon(templates.italicText(constants.provisionedHostInfo()));
        discoveredHostInfoIcon =
                new InfoIcon(templates.italicText(constants.discoveredHostInfoIcon()));
        hostAddressInfoIcon =
                new InfoIcon(templates.italicText(constants.hostPopupHostAddressLabelHelpMessage()));
    }

    private void addStyles() {
        providerSearchFilterEditor.addContentWidgetContainerStyleName(style.searchFilter());
        providerSearchFilterEditor.hideLabel();
    }

    private void initEditors() {
        publicKeyEditor = new StringEntityModelTextAreaLabelEditor();

        // List boxes
        clusterEditor = new GroupedListModelListBoxEditor<>(new GroupedListModelListBox<Cluster>(new NameRenderer<Cluster>()) {

            @Override
            public String getModelLabel(Cluster model) {
                return model.getName();
            }

            @Override
            public String getGroupLabel(Cluster model) {
                return messages.hostDataCenter(model.getStoragePoolName());
            }

            @Override
            public Comparator<Cluster> getComparator() {
                return new DataCenterClusterComparator();
            }

            /**
             * Comparator that sorts on data center name first, and then cluster name. Ignoring case.
             */
            final class DataCenterClusterComparator implements Comparator<Cluster> {

                @Override
                public int compare(Cluster cluster1, Cluster cluster2) {
                    if (cluster1.getStoragePoolName() != null && cluster2.getStoragePoolName() == null) {
                        return -1;
                    } else if (cluster2.getStoragePoolName() != null && cluster1.getStoragePoolName() == null) {
                        return 1;
                    } else if (cluster1.getStoragePoolName() == null && cluster2.getStoragePoolName() == null) {
                        return 0;
                    }
                    if (cluster1.getStoragePoolName().equals(cluster2.getStoragePoolName())) {
                        return cluster1.getName().compareToIgnoreCase(cluster2.getName());
                    } else {
                        return cluster1.getStoragePoolName().compareToIgnoreCase(cluster2.getStoragePoolName());
                    }
                }
            }
        });

        externalHostNameEditor = new ListModelListBoxEditor<>(new NameRenderer<VDS>());

        providersEditor = new ListModelListBoxEditor<>(new NameRenderer<Provider<Provider.AdditionalProperties>>());

        externalDiscoveredHostsEditor = getListModelTypeAheadListBoxEditor();
        externalHostGroupsEditor = getListModelTypeAheadListBoxEditor();
        externalComputeResourceEditor = getListModelTypeAheadListBoxEditor();

        // Check boxes
        pmEnabledEditor = new EntityModelCheckBoxEditor(Align.RIGHT);
        pmEnabledEditor.setUsePatternFly(true);
        pmKdumpDetectionEditor = new EntityModelCheckBoxEditor(Align.RIGHT);
        pmKdumpDetectionEditor.setUsePatternFly(true);
        disableAutomaticPowerManagementEditor = new EntityModelCheckBoxEditor(Align.RIGHT);
        disableAutomaticPowerManagementEditor.setUsePatternFly(true);
        externalHostProviderEnabledEditor = new EntityModelCheckBoxEditor(Align.RIGHT);
        overrideIpTablesEditor = new EntityModelCheckBoxEditor(Align.RIGHT);

        rbPassword = new RadioButton("1"); //$NON-NLS-1$
        rbPublicKey = new RadioButton("1"); //$NON-NLS-1$
        rbDiscoveredHost = new EntityModelRadioButtonEditor("2"); //$NON-NLS-1$
        rbProvisionedHost = new EntityModelRadioButtonEditor("2"); //$NON-NLS-1$

        kernelCmdlineBlacklistNouveau = new EntityModelCheckBoxEditor(Align.RIGHT);
        kernelCmdlineIommu = new EntityModelCheckBoxEditor(Align.RIGHT);
        kernelCmdlineKvmNested = new EntityModelCheckBoxEditor(Align.RIGHT);
        kernelCmdlineUnsafeInterrupts = new EntityModelCheckBoxEditor(Align.RIGHT);
        kernelCmdlinePciRealloc = new EntityModelCheckBoxEditor(Align.RIGHT);
        consoleAddressEnabled = new EntityModelCheckBoxEditor(Align.RIGHT);
        hostedEngineDeployActionsEditor = new ListModelListBoxEditor<>(new EnumRenderer<HostedEngineDeployConfiguration.Action>());
    }

    private ListModelTypeAheadListBoxEditor<ExternalEntityBase> getListModelTypeAheadListBoxEditor() {
        return new ListModelTypeAheadListBoxEditor<>(
                new ListModelTypeAheadListBoxEditor.NullSafeSuggestBoxRenderer<ExternalEntityBase>() {
                    @Override
                    public String getReplacementStringNullSafe(ExternalEntityBase data) {
                        return data.getName();
                    }

                    @Override
                    public String getDisplayStringNullSafe(ExternalEntityBase data) {
                        return typeAheadNameDescriptionTemplateNullSafe(
                                data.getViewableName(),
                                data.getDescription()
                        );
                    }
                });
    }

    private String typeAheadNameDescriptionTemplateNullSafe(String name, String description) {
        return templates.typeAheadNameDescription(
                name != null ? name : constants.empty(),
                description != null ? description : constants.empty())
                .asString();
    }

    void localize() {
        // General tab
        generalTab.setLabel(constants.hostPopupGeneralTabLabel());
        clusterEditor.setLabel(constants.hostPopupClusterLabel());
        nameEditor.setLabel(constants.hostPopupNameLabel());
        userNameEditor.setLabel(constants.hostPopupUsernameLabel());
        commentEditor.setLabel(constants.commentLabel());
        hostAddressLabel.setText(constants.hostPopupHostAddressLabel());
        authSshPortEditor.setLabel(constants.hostPopupPortLabel());
        authLabel.setText(constants.hostPopupAuthLabel());
        rootPasswordLabel.setText(constants.hostPopupAuthLabelForExternalHost());
        rbPasswordLabel.setText(constants.hostPopupPasswordLabel());
        rbPublicKeyLabel.setText(constants.hostPopupPublicKeyLabel());
        fingerprintLabel.setText(constants.hostPopupHostFingerprintLabel());
        overrideIpTablesEditor.setLabel(constants.hostPopupOverrideIpTablesLabel());
        externalHostProviderEnabledEditor.setLabel(constants.hostPopupEnableExternalHostProvider());
        externalHostNameEditor.setLabel(constants.hostPopupExternalHostName());
        publicKeyEditor.setTitle(constants.publicKeyUsage());

        // Power Management tab
        powerManagementTab.setLabel(constants.hostPopupPowerManagementTabLabel());
        pmEnabledEditor.setLabel(constants.hostPopupPmEnabledLabel());

        // Auto PM
        disableAutomaticPowerManagementEditor.setLabel(constants.hostPopupPmDisableAutoPM());
        pmKdumpDetectionEditor.setLabel(constants.hostPopupPmKdumpDetection());

        // SPM tab
        spmTab.setLabel(constants.spmTestButtonLabel());
        consoleTab.setLabel(constants.consoleButtonLabel());

        // Network Provider Tab
        networkProviderTab.setLabel(constants.networkProviderButtonLabel());

        externalDiscoveredHostsEditor.setLabel(constants.discoveredHostsLabel());
        externalHostGroupsEditor.setLabel(constants.hostGroupsLabel());
        externalComputeResourceEditor.setLabel(constants.computeResourceLabel());

        // Info icons
        kernelCmdlineUnsafeInterruptsInfoIcon.setText(
                SafeHtmlUtils.fromString(constants.kernelCmdlineUnsafeInterruptsInfoIcon()));
        kernelCmdlineBlacklistNouveauInfoIcon.setText(SafeHtmlUtils.fromString(constants.kernelCmdlineBlacklistNouveauInfoIcon()));
        kernelCmdlineIommuInfoIcon.setText(SafeHtmlUtils.fromString(constants.kernelCmdlineIommuInfoIcon()));
        kernelCmdlineKvmNestedInfoIcon.setText(SafeHtmlUtils.fromString(constants.kernelCmdlineKvmNestedInfoIcon()));
        kernelCmdlinePciReallocInfoIcon.setText(SafeHtmlUtils.fromString(constants.kernelCmdlinePciReallocInfoIcon()));
        kernelCmdlineInfoIcon.setText(SafeHtmlUtils.fromString(constants.kernelCmdlineInfoIcon()));

        // Affinity Labels Tab
        affinityLabelsTab.setLabel(constants.affinityLabels());
    }

    private void applyModeCustomizations() {
        if (ApplicationModeHelper.getUiMode() == ApplicationMode.GlusterOnly) {
            spmTab.setVisible(false);
            powerManagementTab.setVisible(false);
            consoleTab.setVisible(false);
            networkProviderTab.setVisible(false);
        }

    }

    @Override
    public void edit(final HostModel object) {
        driver.edit(object);
        setTabIndexes(0);

        object.getFetchResult().getEntityChangedEvent().addListener((ev, sender, args) -> {
            String fetchResultText = object.getFetchResult().getEntity();
            if (ConstantsManager.getInstance().getConstants().errorLoadingFingerprint().equals(fetchResultText)) {
                fetchResult.addStyleName(style.fetchResultErrorLabel());
            } else {
                fetchResult.removeStyleName(style.fetchResultErrorLabel());
            }
            fetchResult.setText(fetchResultText);
        });

        object.getPkSection().getPropertyChangedEvent().addListener((ev, sender, args) -> {
            if ("IsAvailable".equals(args.propertyName)) { //$NON-NLS-1$
                setPkPasswordSectionVisiblity(false);
            }
        });

        object.getProviders().getSelectedItemChangedEvent().addListener((ev, sender, args) -> object.updateHosts());

        object.getExternalHostProviderEnabled().getEntityChangedEvent().addListener((ev, sender, args) -> {
            boolean showForemanProviders = object.getExternalHostProviderEnabled().getEntity();
            boolean doProvisioning = object.externalProvisionEnabled();
            boolean isProvisioned = showForemanProviders && doProvisioning;

            providersEditor.setVisible(showForemanProviders);

            // showing or hiding radio buttons
            provisionedHostSection.setVisible(isProvisioned);
            discoveredHostSection.setVisible(isProvisioned);

            // disabling ip and name textbox when using provisioned hosts
            hostAddressEditor.setEnabled(!isProvisioned);
            hostAddressLabel.setStyleName(OvirtCss.LABEL_DISABLED, isProvisioned);

            if (isProvisioned) {
                object.updateHosts();
                object.getIsDiscoveredHosts().setEntity(true);
            } else {
                if (doProvisioning) {
                    object.cleanHostParametersFields();
                }
                hideProviderWidgets(object);
                object.getIsDiscoveredHosts().setEntity(null);
            }
        });

        object.getIsDiscoveredHosts().getEntityChangedEvent().addListener((ev, sender, args) -> {
            if (object.getIsDiscoveredHosts().getEntity() != null) {
                if (object.getIsDiscoveredHosts().getEntity()) {
                    rbDiscoveredHost.asRadioButton().setValue(true);
                    showDiscoveredHostsWidgets(true);
                } else if (!object.getIsDiscoveredHosts().getEntity()) {
                    rbProvisionedHost.asRadioButton().setValue(true);
                    showProvisionedHostsWidgets(true);
                }
            }
        });

        nameEditor.asValueBox().addKeyDownHandler(event -> Scheduler.get().scheduleDeferred(() -> {
            if (object.getExternalHostProviderEnabled().getEntity() &&
                    Boolean.TRUE.equals(object.getIsDiscoveredHosts().getEntity())) {
                ExternalHostGroup dhg =
                        (ExternalHostGroup) object.getExternalHostGroups().getSelectedItem();
                if (dhg != null) {
                    String base = nameEditor.asEditor().getSubEditor().getValue();
                    if (base == null) {
                        base = constants.empty();
                    }
                    String generatedHostName = base + "." + //$NON-NLS-1$
                            (dhg.getDomainName() != null ? dhg.getDomainName() : constants.empty());
                    object.getHost().setEntity(generatedHostName);
                }
            }
        }));

        if (object.isPasswordSectionViewable()) {
            rbPassword.setValue(true);
            rbPassword.setFocus(true);

            fetchSshFingerprint.hideLabel();
            object.setAuthenticationMethod(AuthenticationMethod.Password);
            displayPassPkWindow(true);

            rbPassword.addClickHandler(event -> {
                object.setAuthenticationMethod(AuthenticationMethod.Password);
                displayPassPkWindow(true);
            });

            rbPublicKey.addClickHandler(event -> {
                object.setAuthenticationMethod(AuthenticationMethod.PublicKey);
                displayPassPkWindow(false);
            });
        } else {
            passwordSection.getElement().getStyle().setDisplay(Display.NONE);
            rbPublicKey.getElement().getStyle().setDisplay(Display.NONE);
            rbPublicKeyLabel.setStyleName(OvirtCss.LABEL_DISABLED);

            object.setAuthenticationMethod(AuthenticationMethod.PublicKey);
        }

        updateHostsButton.setResource(resources.searchButtonImage());

        // Create SPM related controls.
        IEventListener<EventArgs> spmListener = (ev, sender, args) -> createSpmControls(object);

        object.getSpmPriority().getItemsChangedEvent().addListener(spmListener);
        object.getSpmPriority().getSelectedItemChangedEvent().addListener(spmListener);

        createSpmControls(object);

        initExternalHostProviderWidgets(object.showExternalProviderPanel());
        // TODO: remove setIsChangeable when configured ssh username is enabled
        userNameEditor.setEnabled(false);

        networkProviderTab.setVisible(object.showNetworkProviderTab());
        networkProviderWidget.edit(object.getNetworkProviderModel());

        this.fenceAgentsEditor.edit(object.getFenceAgentListModel());
        this.proxySourceEditor.edit(object.getPmProxyPreferencesList());
        addTextAndLinkAlert(fetchPanel, constants.fetchingHostFingerprint(), object.getSSHFingerPrint());
        providerSearchFilterLabel.setText(constants.hostPopupProviderSearchFilter());
        nameEditor.setFocus(true);

        hostedEngineTab.setVisible(object.getIsHeSystem() && object.getIsNew());

        object.getHostedEngineWarning().getPropertyChangedEvent().addListener((ev, sender, args) -> {
            EntityModel entity = (EntityModel) sender;

            if ("IsAvailable".equals(args.propertyName)) { //$NON-NLS-1$
                hostedEngineWarningLabel.setVisible(entity.getIsAvailable());
            }
        });

        affinityLabelSelectionWidget.init(object.getLabelList());
    }

    private void showDiscoveredHostsWidgets(boolean enabled) {
        usualFormToDiscover(enabled);
        showExternalDiscoveredHost(enabled);
        setHostProviderVisibility(!enabled);
    }

    private void showProvisionedHostsWidgets(boolean enabled) {
        usualFormToDiscover(!enabled);
        showExternalDiscoveredHost(!enabled);
        setHostProviderVisibility(enabled);
    }

    private void hideProviderWidgets(final HostModel object) {
        rbProvisionedHost.asRadioButton().setValue(false);
        rbDiscoveredHost.asRadioButton().setValue(false);
        usualFormToDiscover(false);
        showExternalDiscoveredHost(false);
        setHostProviderVisibility(false);
    }

    private void initExternalHostProviderWidgets(boolean isAvailable) {
        // When the widgets should be enabled, only the "enable/disable" one should appear.
        // All the rest shouldn't be visible
        externalHostProviderEnabledEditor.setVisible(isAvailable);
        provisionedHostSection.setVisible(false);
        discoveredHostSection.setVisible(false);
        providersEditor.setVisible(false);
        showExternalDiscoveredHost(false);
        setHostProviderVisibility(false);
    }

    private void displayPassPkWindow(boolean isPasswordVisible) {
        passwordEditor.setVisible(isPasswordVisible);
        publicKeyEditor.setVisible(!isPasswordVisible);
    }

    private void initExpander() {
        expander.initWithContent(expanderContent.getElement());
        pmExpander.initWithContent(pmExpanderContent.getElement());
    }

    private void showExternalDiscoveredHost(boolean enabled) {
        discoveredHostPanel.setVisible(enabled);
    }

    private void usualFormToDiscover(boolean isDiscovered) {
        if (isDiscovered) {
            authLabel.setVisible(false);
            rootPasswordLabel.setVisible(true);
        } else {
            rootPasswordLabel.setVisible(false);
            authLabel.setVisible(true);
            displayPassPkWindow(true);
        }
        rbPublicKey.setVisible(!isDiscovered);
        rbPublicKeyLabel.setVisible(!isDiscovered);
        rbPassword.setVisible(!isDiscovered);
        rbPasswordLabel.setVisible(!isDiscovered);
        publicKeyEditor.setVisible(!isDiscovered);
        authSshPortEditor.setVisible(!isDiscovered);
        userNameEditor.setVisible(!isDiscovered);
    }

    private void createSpmControls(final HostModel object) {

        Row labelRow = (Row) spmContainer.getWidget(0);
        spmContainer.clear();
        spmContainer.add(labelRow);
        Iterable<?> items = object.getSpmPriority().getItems();
        if (items == null) {
            return;
        }

        // Recreate SPM related controls.
        for (Object item : items) {

            @SuppressWarnings("unchecked")
            final EntityModel<Integer> model = (EntityModel<Integer>) item;

            RadioButton rb = new RadioButton("spm"); // $//$NON-NLS-1$
            rb.setText(model.getTitle());
            Element labelElement = (Element)rb.getElement().getChild(1);
            labelElement.addClassName(style.patternFlyRadio());
            rb.setValue(object.getSpmPriority().getSelectedItem() == model);

            rb.addValueChangeHandler(e -> object.getSpmPriority().setSelectedItem(model));

            Row row = new Row();
            Column column = new Column(ColumnSize.SM_12, rb);
            row.add(column);
            spmContainer.add(row);
        }
    }

    @Override
    public HostModel flush() {
        networkProviderWidget.flush();
        fenceAgentsEditor.flush();
        proxySourceEditor.flush();
        return driver.flush();
    }

    @Override
    public void cleanup() {
        driver.cleanup();
    }

    @Override
    public void focusInput() {
        nameEditor.setFocus(true);
    }

    @Override
    public HasClickHandlers getUpdateHostsButton() {
        return updateHostsButton;
    }

    @Override
    public void showPowerManagement() {
        getTabPanel().switchTab(powerManagementTab);
    }

    /**
     * Create a widget containing text and a link that triggers the execution of a command.
     *
     * @param view
     *            the view where the alert should be added
     * @param text
     *            the text content of the alert
     * @param command
     *            the command that should be executed when the link is clicked
     */
    private void addTextAndLinkAlert(SimplePanel view, final String text, final UICommand command) {
        // Find the open and close positions of the link within the message:
        final int openIndex = text.indexOf("<a>"); //$NON-NLS-1$
        final int closeIndex = text.indexOf("</a>"); //$NON-NLS-1$
        if (openIndex == -1 || closeIndex == -1 || closeIndex < openIndex) {
            return;
        }

        // Extract the text before, inside and after the tags:
        final String beforeText = text.substring(0, openIndex);
        final String betweenText = text.substring(openIndex + 3, closeIndex);
        final String afterText = text.substring(closeIndex + 4);

        // Create a flow panel containing the text and the link:
        final FlowPanel alertPanel = new FlowPanel();

        // Create the label for the text before the tag:
        final Label beforeLabel = new Label(beforeText);
        beforeLabel.getElement().getStyle().setProperty("display", "inline"); //$NON-NLS-1$ //$NON-NLS-2$
        alertPanel.add(beforeLabel);

        // Create the anchor:
        final Anchor betweenAnchor = new Anchor(betweenText);
        betweenAnchor.getElement().getStyle().setProperty("display", "inline"); //$NON-NLS-1$ //$NON-NLS-2$
        betweenAnchor.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
        alertPanel.add(betweenAnchor);

        // Add a listener to the anchor so that the command is executed when
        // it is clicked:
        betweenAnchor.addClickHandler(event -> command.execute());

        // Create the label for the text after the tag:
        final Label afterLabel = new Label(afterText);
        afterLabel.getElement().getStyle().setProperty("display", "inline"); //$NON-NLS-1$ //$NON-NLS-2$
        alertPanel.add(afterLabel);

        // Add the alert to the view:
        view.add(alertPanel);
    }

    interface Style extends CssResource {

        String searchFilter();

        String fetchResultErrorLabel();

        String topElement();

        String patternFlyRadio();
    }

    public void setPkPasswordSectionVisiblity(boolean visible) {
        pkSection.setVisible(visible);
        passwordSection.setVisible(visible);
    }

    @Override
    public int setTabIndexes(int nextTabIndex) {
        // ==General Tab==
        nextTabIndex = generalTab.setTabIndexes(nextTabIndex);
        clusterEditor.setTabIndex(nextTabIndex++);
        externalHostProviderEnabledEditor.setTabIndex(nextTabIndex++);
        providersEditor.setTabIndex(nextTabIndex++);
        rbProvisionedHost.setTabIndex(nextTabIndex++);
        rbDiscoveredHost.setTabIndex(nextTabIndex++);
        externalDiscoveredHostsEditor.setTabIndex(nextTabIndex++);
        externalHostGroupsEditor.setTabIndex(nextTabIndex++);
        externalComputeResourceEditor.setTabIndex(nextTabIndex++);
        nameEditor.setTabIndex(nextTabIndex++);
        commentEditor.setTabIndex(nextTabIndex++);
        hostAddressEditor.setTabIndex(nextTabIndex++);
        authSshPortEditor.setTabIndex(nextTabIndex++);
        userNameEditor.setTabIndex(nextTabIndex++);
        rbPassword.setTabIndex(nextTabIndex++);
        passwordEditor.setTabIndex(nextTabIndex++);

        // ==Power Management Tab==
        nextTabIndex = powerManagementTab.setTabIndexes(nextTabIndex);
        pmEnabledEditor.setTabIndex(nextTabIndex++);
        pmKdumpDetectionEditor.setTabIndex(nextTabIndex++);
        disableAutomaticPowerManagementEditor.setTabIndex(nextTabIndex++);
        fenceAgentsEditor.setTabIndexes(nextTabIndex++);
        pmExpander.setTabIndexes(nextTabIndex);
        proxySourceEditor.setTabIndexes(nextTabIndex++);

        // ==SPM Tab==
        nextTabIndex = spmTab.setTabIndexes(nextTabIndex);

        // ==Console Tab==
        nextTabIndex = consoleTab.setTabIndexes(nextTabIndex);
        consoleAddressEnabled.setTabIndex(nextTabIndex++);
        consoleAddress.setTabIndex(nextTabIndex++);

        // ==Kernel Tab==
        nextTabIndex = kernelTab.setTabIndexes(nextTabIndex);
        kernelCmdlineBlacklistNouveau.setTabIndex(nextTabIndex++);
        kernelCmdlineIommu.setTabIndex(nextTabIndex++);
        kernelCmdlineKvmNested.setTabIndex(nextTabIndex++);
        kernelCmdlineUnsafeInterrupts.setTabIndex(nextTabIndex++);
        kernelCmdlinePciRealloc.setTabIndex(nextTabIndex++);
        kernelCmdlineText.setTabIndex(nextTabIndex++);

        // ==Hosted Engine Tab==
        nextTabIndex = hostedEngineTab.setTabIndexes(nextTabIndex);
        hostedEngineDeployActionsEditor.setTabIndex(nextTabIndex++);

        // ==Affinity Labels Tab==
        nextTabIndex = affinityLabelsTab.setTabIndexes(nextTabIndex);

        return nextTabIndex;
    }

    @Override
    public void setHostProviderVisibility(boolean visible) {
        searchProviderRow.setVisible(visible);
    }

    @Override
    protected void populateTabMap() {
        getTabNameMapping().put(TabName.GENERAL_TAB, this.generalTab.getTabListItem());
        getTabNameMapping().put(TabName.POWER_MANAGEMENT_TAB, this.powerManagementTab.getTabListItem());
        getTabNameMapping().put(TabName.NETWORK_PROVIDER_TAB, this.networkProviderTab.getTabListItem());
        getTabNameMapping().put(TabName.CONSOLE_TAB, this.consoleTab.getTabListItem());
        getTabNameMapping().put(TabName.SPM_TAB, this.spmTab.getTabListItem());
        getTabNameMapping().put(TabName.KERNEL_TAB, this.kernelTab.getTabListItem());
    }

    @Override
    public DialogTabPanel getTabPanel() {
        return tabPanel;
    }


    @Override
    public HasClickHandlers getKernelCmdlineResetButton() {
        return kernelCmdlineResetButton;
    }

    @Override
    public HasClickHandlers getAddAffinityLabelButton() {
        return affinityLabelSelectionWidget.getSelectionWidget().getAddSelectedItemButton();
    }
}
