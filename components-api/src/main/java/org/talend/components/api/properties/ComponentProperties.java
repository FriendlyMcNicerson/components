package org.talend.components.api.properties;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.talend.components.api.ComponentDesigner;
import org.talend.components.api.exception.ComponentException;
import org.talend.components.api.i18n.I18nMessageProvider;
import org.talend.components.api.i18n.TranslatableImpl;
import org.talend.components.api.properties.internal.ComponentPropertiesInternal;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.properties.presentation.Widget;
import org.talend.components.api.schema.SchemaElement;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.i18n.I18nMessages;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;

/**
 * The {@code ComponentProperties} class contains the definitions of the properties associated with a component. These
 * definitions contain enough information to automatically construct a nice looking user interface (UI) to populate and
 * validate the properties. The objective is that no actual (graphical) UI code is included in the component's
 * definition and as well no custom graphical UI is required for most components. The types of UIs that can be defined
 * include those for desktop (Eclipse), web, and scripting. All of these will use the code defined here for their
 * construction and validation.
 * <p/>
 * All aspects of the properties are defined in a subclass of this class using the {@link SchemaElement},
 * {@Link PresentationItem}, {@link Widget}, and {@link Form} classes. In addition in cases where user interface
 * decisions are made in code, methods can be added to the subclass to influence the flow of the user interface and help
 * with validation.
 * <p/>
 * Each property can be a Java type, both simple types and collections are permitted. In addition,
 * {@code ComponentProperties} classes can be composed allowing hierarchies of properties and collections of properties
 * to be reused.
 * <p/>
 * Properties are be grouped into {@link Form} objects which can be presented in various ways by the user interface (for
 * example, a wizard page, a tab in a property sheet, or a dialog). The same property can appear in multiple forms.
 * <p/>
 * Methods can be added in subclasses according to the conventions below to help direct the UI. These methods will be
 * automatically called by the UI code.
 * <ul>
 * <li>{@code before&lt;PropertyName&gt;} - Called before the property is presented in the UI. This can be used to
 * compute anything required to display the property.</li>
 * <li>{@code after&lt;PropertyName&gt;} - Called after the property is presented and validated in the UI. This can be
 * used to update the properties state to consider the changed in this property.</li>
 * <li>{@code validate&lt;PropertyName&gt;} - Called to validate the property value that has been entered in the UI.
 * This will return a {@link ValidationResult} object with any error information.</li>
 * <li>{@code beforeForm&lt;FormName&gt;} - Called before the form is displayed.</li>
 * </ul>
 * 
 * WARNING : property shall be created as instance field before the constructor is called so that this abstract
 * constructor can attach i18n translator to the properties. If you want to create the property later you'll have to
 * call {@link SchemaElement#setI18nMessageFormater(I18nMessages)} manually.
 */

// @JsonSerialize(using = ComponentPropertiesSerializer.class)
public abstract class ComponentProperties extends TranslatableImpl {

    static final String METHOD_BEFORE = "before";

    static final String METHOD_AFTER = "after";

    static final String METHOD_VALIDATE = "validate";

    // Not a component property
    protected ComponentPropertiesInternal internal;

    /**
     * Holder class for the results of a deserialization.
     */
    public static class Deserialized {

        public ComponentProperties properties;

        public MigrationInformation migration;
    }

    // FIXME - will be moved
    public static class MigrationInformationImpl implements MigrationInformation {

        @Override
        public boolean isMigrated() {
            return false;
        }

        @Override
        public String getVersion() {
            return null;
        }
    }

    /**
     * Returns the ComponentProperties object previously serialized.
     * 
     * @param serialized created by {@link #toSerialized()}.
     * @return a {@code ComponentProperties} object represented by the {@code serialized} value.
     */
    public static Deserialized fromSerialized(String serialized) {
        Deserialized d = new Deserialized();
        d.migration = new MigrationInformationImpl();
        d.properties = (ComponentProperties) JsonReader.jsonToJava(serialized);
        return d;
    }

    /**
     * inheriting class must call i18nMessagesProvider at the end of the constructor and every time they create and new
     * direct property
     * 
     * @param messageProvider, used to find the I18nMessage according to the current LocalProvider
     * @param baseName, used to find the resource file for I18N
     */
    public ComponentProperties(I18nMessageProvider messageProvider, String baseName) {
        internal = new ComponentPropertiesInternal();
        if (messageProvider != null)
            setI18nMessageFormater(messageProvider.getI18nMessages(this.getClass().getClassLoader(), baseName));
    }

    /**
     * This will use the current I18nMessage to the property handles by this class, but only for direct properties and
     * not nested ComponentProperties
     */
    protected void setupPropertiesWithI18n() {
        List<SchemaElement> properties = getProperties();
        for (SchemaElement prop : properties) {
            if (!(prop instanceof ComponentProperties)) {
                if (prop != null) {
                    prop.setI18nMessageFormater(i18nMessages);
                } // else the property has not been initialised yet, please make sure to call this after initilisation
            } // else this is handle by the constructor of this class.
        }

    }

    /**
     * Returns a serialized version of this for storage in a repository.
     * 
     * @return the serialized {@code String}, use {@link #fromSerialized(String)} to materialize the object.
     */
    public String toSerialized() {
        return JsonWriter.objectToJson(this);
    }

    public List<Form> getForms() {
        return internal.getForms();
    }

    public Form getForm(String formName) {
        return internal.getForm(setupFormName(formName));
    }

    public String getSimpleClassName() {
        return getClass().getSimpleName();
    }

    // Qualify the formName by this class for debugging/testing
    public String setupFormName(String formName) {
        return getSimpleClassName() + formName;
    }

    public void addForm(Form form) {
        internal.getForms().add(form);
    }

    public ComponentDesigner getComponentDesigner() {
        return internal.getDesigner();
    }

    public void setComponentDesigner(ComponentDesigner designer) {
        internal.setDesigner(designer);
    }

    public List<SchemaElement> getProperties() {
        List<SchemaElement> properties = new ArrayList();
        Field[] fields = getClass().getFields();
        for (Field f : fields) {
            if (SchemaElement.class.isAssignableFrom(f.getType())) {
                try {
                    SchemaElement se = (SchemaElement) f.get(this);
                    if (se != null) {
                        properties.add(se);
                    } // else element not initialised (set to null)
                } catch (IllegalAccessException e) {
                    throw new ComponentException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                }
            }
        }
        return properties;
    }

    public void setValue(SchemaElement property, Object value) {
        internal.setValue(property, value);
    }

    public Object getValue(SchemaElement property) {
        return internal.getValue(property);
    }

    public boolean getBooleanValue(SchemaElement property) {
        Boolean value = (Boolean) getValue(property);
        if (value == null || !value) {
            return false;
        }
        return true;
    }

    public String getStringValue(SchemaElement property) {
        return (String) getValue(property);
    }

    public int getIntValue(SchemaElement property) {
        Integer value = (Integer) getValue(property);
        if (value == null) {
            return 0;
        }
        return value;
    }

    /**
     * Returns the {@link ValidationResult} for the property being validated if requested.
     *
     * @return a ValidationResult
     */
    public ValidationResult getValidationResult() {
        return internal.getValidationResult();
    }

    /**
     * Declare the widget information for each of the properties
     */
    protected void setupLayout() {
    }

    /**
     * This is called every time the presentation of the components properties needs to be updated
     */
    public void refreshLayout(Form form) {
        form.setRefreshUI(true);
    }

    // Internal - not API
    public void setLayoutMethods(String property, Widget layout) {
        Method m;
        m = findMethod(METHOD_BEFORE, property);
        if (m != null) {
            layout.setCallBefore(true);
        }
        m = findMethod(METHOD_AFTER, property);
        if (m != null) {
            layout.setCallAfter(true);
        }
        m = findMethod(METHOD_VALIDATE, property);
        if (m != null) {
            layout.setCallValidate(true);
        }
    }

    Method findMethod(String type, String propName) {
        propName = propName.substring(0, 1).toUpperCase() + propName.substring(1);
        String methodName = type + propName;
        Method[] methods = getClass().getMethods();
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        return null;
    }

    public void validateProperty(String propName) throws Throwable {
        Method m = findMethod("validate", propName);
        if (m != null) {
            try {
                ValidationResult validationResult = (ValidationResult) m.invoke(this);
                internal.setValidationResult(validationResult);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

    public void beforeProperty(String propName) throws Throwable {
        Method m = findMethod("before", propName);
        if (m == null) {
            throw new IllegalStateException("before method not found for: " + propName);
        }
        try {
            m.invoke(this);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    public void afterProperty(String propName) throws Throwable {
        Method m = findMethod("after", propName);
        if (m != null) {
            try {
                m.invoke(this);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

}
