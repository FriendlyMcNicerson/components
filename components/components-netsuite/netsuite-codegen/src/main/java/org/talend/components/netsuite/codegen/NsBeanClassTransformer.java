package org.talend.components.netsuite.codegen;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

import java.util.Set;

import org.talend.components.netsuite.PrimitiveInfo;

/**
 * Created by ihor.istomin on 1/25/2017.
 */
public class NsBeanClassTransformer {

    public void transform(CtClass classToTransform) throws Exception {

        //        System.out.println("Transform: " + classToTransform.getName());
        Set<JavassistPropertyInfo> propertyInfoSet = JavassistBeanIntrospector.getInstance()
                .getBeanProperties(classToTransform);
        //        for (JavassistPropertyInfo info : propertyInfoSet) {
        //            if (info.getSetter() == null) {
        //                System.out.println("    No setter: " + info.getType().getName() + " " + info.getName());
        //            }
        ////            System.out.println("    Property: " + info.getType().getName() + " " + info.getName());
        //        }

        if (classToTransform.isFrozen()) {
            classToTransform.defrost();
        }

        CtClass nsObjectInterface = ClassPool.getDefault().get("org.talend.components.netsuite.PropertyAccessor");
        classToTransform.addInterface(nsObjectInterface);

        genGetPropMethod(classToTransform, propertyInfoSet);
        genSetPropMethod(classToTransform, propertyInfoSet);
        genGetMetaDataMethod(classToTransform, propertyInfoSet);
    }

    private void genSetPropMethod(CtClass classToTransform,
            Set<JavassistPropertyInfo> propertyInfoSet) throws Exception {

        StringBuilder body = new StringBuilder("public void set(String name, Object value) {\n");
        //        body.append("throw new UnsupportedOperationException(\"setNsProperty\");");

        int count = 0;
        for (JavassistPropertyInfo info : propertyInfoSet) {
            if (info.getSetter() != null) {
                CtMethod setter = info.getSetter();
                CtClass paramType = setter.getParameterTypes()[0];
                if (count > 0) {
                    body.append("\nelse ");
                }
                body.append("if (name.equals(\"" + info.getName() + "\")) { ");
                body.append("");
                if (paramType.isPrimitive()) {
                    PrimitiveInfo primitiveInfo = PrimitiveInfo.valueOf(paramType.getName());
                    if (primitiveInfo.equals(PrimitiveInfo.BOOLEAN)) {
                        body.append(setter.getName() + "(((java.lang.Boolean) value).booleanValue());");
                    } else if (primitiveInfo.equals(PrimitiveInfo.BYTE)) {
                        body.append(setter.getName() + "(((java.lang.Byte) value).byteValue());");
                    } else if (primitiveInfo.equals(PrimitiveInfo.SHORT)) {
                        body.append(setter.getName() + "(((java.lang.Short) value).shortValue());");
                    } else if (primitiveInfo.equals(PrimitiveInfo.INT)) {
                        body.append(setter.getName() + "(((java.lang.Integer) value).intValue());");
                    } else if (primitiveInfo.equals(PrimitiveInfo.LONG)) {
                        body.append(setter.getName() + "(((java.lang.Long) value).longValue());");
                    } else if (primitiveInfo.equals(PrimitiveInfo.FLOAT)) {
                        body.append(setter.getName() + "(((java.lang.Float) value).floatValue());");
                    } else if (primitiveInfo.equals(PrimitiveInfo.DOUBLE)) {
                        body.append(setter.getName() + "(((java.lang.Double) value).doubleValue());");
                    } else if (primitiveInfo.equals(PrimitiveInfo.CHAR)) {
                        body.append(setter.getName() + "(((java.lang.Character) value).charValue());");
                    } else {
                        throw new Exception("Failed to determine primitive wrapper type: " + paramType.getName());
                    }
                } else {
                    body.append(setter.getName() + "((" + paramType.getName() + ") value);");
                }
                body.append(" }");

                count++;
            }
        }
        // Last else lock
        body.append("\n");
        if (count != 0) {
            body.append("else {");
        }
        body.append("throw new IllegalArgumentException(\"Invalid property: \" + name);\n");
        if (count != 0) {
            body.append("}");
        }
        // End of method body
        body.append("\n");
        body.append("}");

        try {
            //            System.out.println(body);
            CtMethod method = CtNewMethod.make(body.toString(), classToTransform);
            classToTransform.addMethod(method);
        } catch (CannotCompileException e) {
            System.out.println(body);
            throw e;
        }
    }

    private void genGetPropMethod(CtClass classToTransform,
            Set<JavassistPropertyInfo> propertyInfoSet) throws Exception {

        StringBuilder body = new StringBuilder("public Object get(String name) {\n");

        //        body.append("throw new UnsupportedOperationException(\"get\");");
        int count = 0;
        for (JavassistPropertyInfo info : propertyInfoSet) {
            if (info.getGetter() != null) {
                CtMethod getter = info.getGetter();
                CtClass returnType = getter.getReturnType();
                if (count > 0) {
                    body.append("\n");
                }
                body.append("if ($1.equals(\"" + info.getName() + "\")) { ");
                body.append("return ");
                if (returnType.isPrimitive()) {
                    body.append("($w) ").append(getter.getName() + "();");
                } else {
                    body.append(getter.getName() + "();");
                }
                body.append(" }");

                count++;
            }
        }
        body.append("\n");
        body.append("throw new IllegalArgumentException(\"Invalid property: \" + name);\n");
        body.append("}");

        try {
            CtMethod method = CtNewMethod.make(body.toString(), classToTransform);
            classToTransform.addMethod(method);
        } catch (CannotCompileException e) {
            System.out.println(body);
            throw e;
        }
    }

    private void genGetMetaDataMethod(CtClass targetClass,
            Set<JavassistPropertyInfo> propertyInfoSet) throws Exception {

        CtClass beanMetaDataClass = ClassPool.getDefault().get("org.talend.components.netsuite.BeanMetaData");
        CtClass propMetaDataClass = ClassPool.getDefault().get("org.talend.components.netsuite.PropertyMetaData");

        StringBuilder body = new StringBuilder("return new " + beanMetaDataClass.getName() +"(");
        body.append("new " + propMetaDataClass.getName() + "[]{");

        int count = 0;
        for (JavassistPropertyInfo info : propertyInfoSet) {
            if (count > 0) {
                body.append(", ");
            }

            // PropertyMetaData constructor
            body.append("new " + propMetaDataClass.getName() + "(");
            // property name
            body.append("\"" + info.getName() + "\"");
            body.append(", ");
            // property read type (class)
            body.append("" + info.getReadType().getName() + ".class");
            body.append(", ");
            // property write type (class)
            if (info.getWriteType() != null) {
                body.append("" + info.getWriteType().getName() + ".class");
            } else {
                body.append("null");
            }
            body.append(", ");
            // read method name
            if (info.getGetter() != null) {
                body.append("\"" + info.getGetter().getName() + "\"");
            } else {
                body.append("null");
            }
            body.append(", ");
            // write method name
            if (info.getSetter() != null) {
                body.append("\"" + info.getSetter().getName() + "\"");
            } else {
                body.append("null");
            }
            // End of PropertyMetaData constructor
            body.append(")");

            count++;
        }
        // End of PropertyMetaData array
        body.append("}");
        // End of BodyMetaData constructor
        body.append(");");

        try {
            CtMethod method = CtNewMethod.make(beanMetaDataClass, "getBeanMetaData",
                    new CtClass[0], new CtClass[0], body.toString(), targetClass);
            method.setModifiers(Modifier.PUBLIC | Modifier.STATIC);

            targetClass.addMethod(method);
        } catch (CannotCompileException e) {
            System.out.println(body);
            throw e;
        }
    }

}