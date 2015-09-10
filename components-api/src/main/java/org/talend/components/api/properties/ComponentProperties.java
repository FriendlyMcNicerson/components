package org.talend.components.api.properties;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.talend.components.api.ComponentDesigner;
import org.talend.components.api.properties.internal.ComponentPropertiesInternal;
import org.talend.components.api.properties.presentation.Form;
import org.talend.components.api.properties.presentation.Widget;

/**
 * The {@code ComponentProperties} class contains the definitions of the properties associated with a component. These
 * definitions contain enough information to automatically construct a nice looking user interface (UI) to populate and
 * validate the properties. The objective is that no actual (graphical) UI code is included in the component's
 * definition and as well no custom graphical UI is required for most components. The types of UIs that can be defined
 * include those for desktop (Eclipse), web, and scripting. All of these will use the code defined here for their
 * construction and validation.
 * <p/>
 * All aspects of the properties are defined in a subclass of this class using the {@link Property}, {@Link
 * PresentationItem}, {@link Widget}, and {@link Form} classes. In addition in cases where user interface decisions are
 * made in code, methods can be added to the subclass to influence the flow of the user interface and help with
 * validation.
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
 */

// @JsonSerialize(using = ComponentPropertiesSerializer.class)
public abstract class ComponentProperties {

    static final String                   METHOD_BEFORE   = "before";

    static final String                   METHOD_AFTER    = "after";

    static final String                   METHOD_VALIDATE = "validate";

    // Not a component property
    protected ComponentPropertiesInternal internal;

    public ComponentProperties() {
        internal = new ComponentPropertiesInternal();
    }

    public List<Form> getForms() {
        return internal.getForms();
    }

    public Form getForm(String formName) {
        return internal.getForm(formName);
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

    public Property[] getProperties() {
        List<Property> properties = new ArrayList();
        Field[] fields = getClass().getFields();
        for (Field f : fields) {
            if (Property.class.isAssignableFrom(f.getType()))
                try {
                    properties.add((Property) f.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
        }
        return (Property[]) properties.toArray(new Property[] {});
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
        if (m != null)
            layout.setCallBefore(true);
        m = findMethod(METHOD_AFTER, property);
        if (m != null)
            layout.setCallAfter(true);
        m = findMethod(METHOD_VALIDATE, property);
        if (m != null)
            layout.setCallValidate(true);
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
        if (m == null)
            throw new IllegalStateException("before method not found for: " + propName);
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
