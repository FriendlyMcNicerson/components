package org.talend.components.filedelimited;

import org.talend.components.api.AbstractComponentFamilyDefinition;
import org.talend.components.api.ComponentInstaller;
import org.talend.components.api.Constants;
import org.talend.components.filedelimited.tFileInputDelimited.TFileInputDelimitedDefinition;
import org.talend.components.filedelimited.tFileOutputDelimited.TFileOutputDelimitedDefinition;
import org.talend.components.filedelimited.wizard.FileDelimitedWizardDefinition;

import aQute.bnd.annotation.component.Component;

/**
 * Install all of the definitions provided for the FileDelimited family of components.
 */
@Component(name = Constants.COMPONENT_INSTALLER_PREFIX + FileDelimitedFamilyDefinition.NAME, provide = ComponentInstaller.class)
public class FileDelimitedFamilyDefinition extends AbstractComponentFamilyDefinition implements ComponentInstaller {

    public static final String NAME = "FileDelimited";

    public FileDelimitedFamilyDefinition() {
        super(NAME,
                // Components
                new TFileInputDelimitedDefinition(), new TFileOutputDelimitedDefinition(),
                // Component wizards
                new FileDelimitedWizardDefinition());

    }

    @Override
    public void install(ComponentFrameworkContext ctx) {
        ctx.registerComponentFamilyDefinition(this);
    }

}
