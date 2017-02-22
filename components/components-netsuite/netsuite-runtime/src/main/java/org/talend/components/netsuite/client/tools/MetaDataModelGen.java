package org.talend.components.netsuite.client.tools;

import static org.talend.components.netsuite.client.model.BeanUtils.toInitialLower;
import static org.talend.components.netsuite.client.model.BeanUtils.toInitialUpper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.netsuite.beans.EnumAccessor;
import org.talend.components.netsuite.client.model.BeanUtils;
import org.talend.components.netsuite.client.model.RecordTypeEx;
import org.talend.components.netsuite.client.model.SearchRecordTypeEx;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 *
 */
public class MetaDataModelGen {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Class<?> recordBaseClass;
    protected Class<? extends Enum> recordTypeEnumClass;
    protected EnumAccessor recordTypeEnumAccessor;

    protected Set<Class<?>> searchRecordBaseClasses = new HashSet<>();
    protected Class<? extends Enum> searchRecordTypeEnumClass;
    protected EnumAccessor searchRecordTypeEnumAccessor;

    protected Class<?> recordRefClass;

    protected Set<String> standardEntityTypes = new HashSet<>();
    protected Set<String> standardTransactionTypes = new HashSet<>();
    protected Set<String> standardItemTypes = new HashSet<>();

    protected Map<String, String> additionalRecordTypes = new HashMap<>();
    protected Map<String, RecordTypeSpec> recordTypeMap = new HashMap<>();

    protected Map<String, String> additionalSearchRecordTypes = new HashMap<>();
    protected Map<String, SearchRecordTypeSpec> searchRecordTypeMap = new HashMap<>();

    protected ClassName recordTypeEnumClassName = ClassName.get(
            "org.talend.components.netsuite.client.v2016_2", "RecordTypeEnum");
    protected ClassName searchRecordTypeEnumClassName = ClassName.get(
            "org.talend.components.netsuite.client.v2016_2", "SearchRecordTypeEnum");

    protected File outputFolder;

    public MetaDataModelGen() {
        outputFolder = new File("./components/components-netsuite/netsuite-runtime/src/main/gen");
    }

    public void setRecordBaseClass(Class<?> recordBaseClass) {
        this.recordBaseClass = recordBaseClass;
    }

    public void setRecordTypeEnumClass(Class<? extends Enum> recordTypeEnumClass) {
        this.recordTypeEnumClass = recordTypeEnumClass;
        this.recordTypeEnumAccessor = BeanUtils.getEnumAccessor(recordTypeEnumClass);
    }

    public void setSearchRecordBaseClasses(Collection<Class<?>> searchRecordBaseClasses) {
        this.searchRecordBaseClasses.addAll(searchRecordBaseClasses);
    }

    public void setSearchRecordTypeEnumClass(Class<? extends Enum> searchRecordTypeEnumClass) {
        this.searchRecordTypeEnumClass = searchRecordTypeEnumClass;
        this.searchRecordTypeEnumAccessor = BeanUtils.getEnumAccessor(searchRecordTypeEnumClass);
    }

    public void setRecordRefClass(Class<?> recordRefClass) {
        this.recordRefClass = recordRefClass;
    }

    protected static void traverseXmlTypes(Class<?> rootClass, Class<?> clazz, Set<Class<?>> classes) {
        if (classes.contains(clazz)) {
            return;
        }

        if (clazz != rootClass && rootClass.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
            classes.add(clazz);
        }

        XmlSeeAlso xmlSeeAlso = clazz.getAnnotation(XmlSeeAlso.class);
        if (xmlSeeAlso != null) {
            Collection<Class<?>> referencedClasses = new HashSet<>(Arrays.<Class<?>>asList(xmlSeeAlso.value()));
            for (Class<?> referencedClass : referencedClasses) {
                traverseXmlTypes(rootClass, referencedClass, classes);
            }
        }
    }

    public void genRecordTypeMetaDataModel() {

        Set<Class<?>> recordClasses = new HashSet<>();
        traverseXmlTypes(recordBaseClass, recordBaseClass, recordClasses);

        Set<String> unresolvedTypeNames = new HashSet<>();

        for (Class<?> clazz : recordClasses) {
            if (clazz == recordBaseClass
                    || !recordBaseClass.isAssignableFrom(clazz)
                    || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            RecordTypeSpec spec = null;

            String recordTypeClassSimpleName = clazz.getSimpleName();
            String recordTypeName = null;
            Enum<?> recordTypeEnumValue = null;
            String recordTypeEnumConstantName = null;

            if (additionalRecordTypes.containsKey(recordTypeClassSimpleName)) {
                recordTypeName = toInitialLower(recordTypeClassSimpleName);
                recordTypeEnumConstantName = additionalRecordTypes.get(recordTypeClassSimpleName);
                spec = new RecordTypeSpec();

            } else {
                try {
                    recordTypeEnumValue = recordTypeEnumAccessor.mapFromString(
                            toInitialLower(recordTypeClassSimpleName));
                    recordTypeEnumConstantName = recordTypeEnumValue.name();
                    recordTypeName = recordTypeEnumAccessor.mapToString(recordTypeEnumValue);

                    spec = new RecordTypeSpec();
                } catch (IllegalArgumentException e) {
                    unresolvedTypeNames.add(recordTypeClassSimpleName);
                }
            }

            if (spec != null) {
                if (recordTypeMap.containsKey(recordTypeName)) {
                    throw new IllegalArgumentException("Record type already registered: " + recordTypeClassSimpleName);
                }

                String searchRecordTypeName = null;
                String recordTypeNameCapitalized = toInitialUpper(recordTypeName);
                if (standardEntityTypes.contains(recordTypeNameCapitalized)) {
                    try {
                        Enum<?> searchRecordType = searchRecordTypeEnumAccessor.mapFromString(recordTypeName);
                        searchRecordTypeName = searchRecordTypeEnumAccessor.mapToString(searchRecordType);
                    } catch (IllegalArgumentException e) {
                        logger.error("Invalid entity record type: '" + recordTypeName + "'");
                    }
                } else if (standardTransactionTypes.contains(recordTypeNameCapitalized)) {
                    searchRecordTypeName = "transaction";
                } else if (standardItemTypes.contains(recordTypeNameCapitalized)) {
                    searchRecordTypeName = "item";
                }

                if (additionalSearchRecordTypes.containsKey(recordTypeName)) {
                    searchRecordTypeName = recordTypeName;
                }

                spec.name = recordTypeName;
                spec.typeName = recordTypeClassSimpleName;
                spec.enumValue = recordTypeEnumValue;
                spec.enumConstantName = recordTypeEnumConstantName;
                spec.recordClass = clazz;
                spec.searchRecordTypeName = searchRecordTypeName;

                recordTypeMap.put(recordTypeName, spec);
            }
        }

        if (!unresolvedTypeNames.isEmpty()) {
            logger.warn("Unresolved record types detected: {}", unresolvedTypeNames);
        }
    }

    public void genSearchRecordTypeMetaDataModel() {
        Collection<Class<?>> searchRecordClasses = new HashSet<>();

        for (Class<?> searchRecordBaseClass : searchRecordBaseClasses) {
            XmlSeeAlso xmlSeeAlso = searchRecordBaseClass.getAnnotation(XmlSeeAlso.class);
            for (Class<?> clazz : xmlSeeAlso.value()) {
                if (clazz == searchRecordBaseClass
                        || !searchRecordBaseClass.isAssignableFrom(clazz)
                        || Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }
                searchRecordClasses.add(clazz);
            }
        }

        Set<String> searchRecordTypeSet = new HashSet<>();
        for (Enum value : searchRecordTypeEnumClass.getEnumConstants()) {
            String searchRecordTypeName = searchRecordTypeEnumAccessor.mapToString(value);
            searchRecordTypeSet.add(searchRecordTypeName);
        }
        searchRecordTypeSet.addAll(additionalSearchRecordTypes.keySet());

        Map<String, Class<?>> searchRecordClassMap = new HashMap<>();
        for (Class<?> clazz : searchRecordClasses) {
            String searchRecordTypeName = clazz.getSimpleName();
            if (searchRecordClassMap.containsKey(searchRecordTypeName)) {
                throw new IllegalStateException("Search record class already registered: " + searchRecordTypeName + ", " + clazz);
            }
            searchRecordClassMap.put(searchRecordTypeName, clazz);
        }

        Set<Class<?>> unresolvedSearchRecords = new HashSet<>(searchRecordClassMap.values());

        for (String searchRecordType : searchRecordTypeSet) {
            String searchRecordTypeName = toInitialUpper(searchRecordType);

            Class<?> searchClass;
            Class<?> searchBasicClass;
            Class<?> searchAdvancedClass;

            searchClass = searchRecordClassMap.get(searchRecordTypeName + "Search");
            unresolvedSearchRecords.remove(searchClass);

            searchBasicClass = searchRecordClassMap.get(searchRecordTypeName + "SearchBasic");
            if (searchBasicClass == null) {
                throw new IllegalStateException("Search basic class not found: " + searchRecordType + ", " + searchRecordTypeName);
            } else {
                unresolvedSearchRecords.remove(searchBasicClass);
            }

            searchAdvancedClass = searchRecordClassMap.get(searchRecordTypeName + "SearchAdvanced");
            unresolvedSearchRecords.remove(searchAdvancedClass);

            Enum<?> searchRecordEnumValue = null;
            String searchRecordTypeEnumConstantName = null;
            try {
                searchRecordEnumValue = searchRecordTypeEnumAccessor.mapFromString(searchRecordType);
                searchRecordTypeEnumConstantName = searchRecordEnumValue.name();
            } catch (IllegalArgumentException e) {
                searchRecordTypeEnumConstantName = additionalSearchRecordTypes.get(searchRecordType);
            }

            SearchRecordTypeSpec spec = new SearchRecordTypeSpec();
            spec.name = searchRecordType;
            spec.typeName = searchRecordTypeName;
            spec.enumConstantName = searchRecordTypeEnumConstantName;
            spec.searchClass = searchClass;
            spec.searchBasicClass = searchBasicClass;
            spec.searchAdvancedClass = searchAdvancedClass;

            searchRecordTypeMap.put(spec.name, spec);
        }

        if (!unresolvedSearchRecords.isEmpty()) {
            logger.warn("Unresolved search record types detected: {}", unresolvedSearchRecords);
        }
    }

    public void genRecordTypeEnumClass() throws IOException {
        List<RecordTypeSpec> specs = new ArrayList<>(recordTypeMap.values());
        Collections.sort(specs, new Comparator<RecordTypeSpec>() {
            @Override public int compare(RecordTypeSpec o1, RecordTypeSpec o2) {
                return o1.enumConstantName.compareTo(o2.enumConstantName);
            }
        });

        TypeSpec.Builder builder = TypeSpec.enumBuilder(recordTypeEnumClassName.simpleName())
                .addSuperinterface(RecordTypeEx.class)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .addField(String.class, "type",
                        javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL)
                .addField(String.class, "typeName",
                        javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL)
                .addField(Class.class, "recordClass",
                        javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL)
                .addField(String.class, "searchRecordType",
                        javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL);

        for (RecordTypeSpec spec : specs) {
            builder.addEnumConstant(spec.enumConstantName, TypeSpec.anonymousClassBuilder(
                    "$S, $S, $T.class, $S",
                    spec.name, spec.typeName, spec.recordClass, spec.searchRecordTypeName).build());
        }

        builder.addMethod(MethodSpec.constructorBuilder()
                .addParameter(String.class, "type")
                .addParameter(String.class, "typeName")
                .addParameter(Class.class, "recordClass")
                .addParameter(String.class, "searchRecordType")
                .addStatement("this.$N = $N", "type", "type")
                .addStatement("this.$N = $N", "typeName", "typeName")
                .addStatement("this.$N = $N", "recordClass", "recordClass")
                .addStatement("this.$N = $N", "searchRecordType", "searchRecordType")
                .build());

        builder.addMethod(MethodSpec.methodBuilder("getType")
                .addAnnotation(Override.class)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return this.$N", "type")
                .build());
        builder.addMethod(MethodSpec.methodBuilder("getTypeName")
                .addAnnotation(Override.class)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return this.$N", "typeName")
                .build());
        builder.addMethod(MethodSpec.methodBuilder("getRecordClass")
                .addAnnotation(Override.class)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .returns(Class.class)
                .addStatement("return this.$N", "recordClass")
                .build());
        builder.addMethod(MethodSpec.methodBuilder("getSearchRecordType")
                .addAnnotation(Override.class)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return this.$N", "searchRecordType")
                .build());

        builder.addMethod(MethodSpec.methodBuilder("getByTypeName")
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC)
                .returns(recordTypeEnumClassName)
                .addParameter(String.class, "typeName")
                .addCode("for ($T value : values()) {\n" +
                        "    if (value.typeName.equals($N)) {\n" +
                        "      return value;\n" +
                        "    }\n" +
                        "}\n" +
                        "return null;\n",
                        recordTypeEnumClassName, "typeName"
                )
                .build());

        TypeSpec typeSpec = builder.build();
//        System.out.println(typeSpec.toString());

        JavaFile jfile = JavaFile.builder(recordTypeEnumClassName.packageName(), typeSpec).build();

        jfile.writeTo(outputFolder);
    }

    public void genSearchRecordTypeEnumClass() throws IOException {
        List<SearchRecordTypeSpec> specs = new ArrayList<>(searchRecordTypeMap.values());
        Collections.sort(specs, new Comparator<SearchRecordTypeSpec>() {
            @Override public int compare(SearchRecordTypeSpec o1, SearchRecordTypeSpec o2) {
                return o1.enumConstantName.compareTo(o2.enumConstantName);
            }
        });

        TypeSpec.Builder builder = TypeSpec.enumBuilder(searchRecordTypeEnumClassName.simpleName())
                .addSuperinterface(SearchRecordTypeEx.class)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .addField(String.class, "type",
                        javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL)
                .addField(String.class, "typeName",
                        javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL)
                .addField(Class.class, "searchClass",
                        javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL)
                .addField(Class.class, "searchBasicClass",
                        javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL)
                .addField(Class.class, "searchAdvancedClass",
                        javax.lang.model.element.Modifier.PRIVATE, javax.lang.model.element.Modifier.FINAL);

        for (SearchRecordTypeSpec spec : specs) {
            TypeSpec enumTypeSpec;
            if (spec.searchClass != null && spec.searchAdvancedClass != null) {
                enumTypeSpec = TypeSpec.anonymousClassBuilder("$S, $S, $T.class, $T.class, $T.class",
                        spec.name, spec.typeName, spec.searchClass, spec.searchBasicClass, spec.searchAdvancedClass).build();
            } else {
                enumTypeSpec = TypeSpec.anonymousClassBuilder("$S, $S, null, $T.class, null",
                        spec.name, spec.typeName, spec.searchBasicClass).build();
            }
            builder.addEnumConstant(spec.enumConstantName, enumTypeSpec);
        }

        builder.addMethod(MethodSpec.constructorBuilder()
                .addParameter(String.class, "type")
                .addParameter(String.class, "typeName")
                .addParameter(Class.class, "searchClass")
                .addParameter(Class.class, "searchBasicClass")
                .addParameter(Class.class, "searchAdvancedClass")
                .addStatement("this.$N = $N", "type", "type")
                .addStatement("this.$N = $N", "typeName", "typeName")
                .addStatement("this.$N = $N", "searchClass", "searchClass")
                .addStatement("this.$N = $N", "searchBasicClass", "searchBasicClass")
                .addStatement("this.$N = $N", "searchAdvancedClass", "searchAdvancedClass")
                .build());

        builder.addMethod(MethodSpec.methodBuilder("getType")
                .addAnnotation(Override.class)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return this.type")
                .build());
        builder.addMethod(MethodSpec.methodBuilder("getTypeName")
                .addAnnotation(Override.class)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .returns(String.class)
                .addStatement("return this.typeName")
                .build());
        builder.addMethod(MethodSpec.methodBuilder("getSearchClass")
                .addAnnotation(Override.class)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .returns(Class.class)
                .addStatement("return this.searchClass")
                .build());
        builder.addMethod(MethodSpec.methodBuilder("getSearchBasicClass")
                .addAnnotation(Override.class)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .returns(Class.class)
                .addStatement("return this.searchBasicClass")
                .build());
        builder.addMethod(MethodSpec.methodBuilder("getSearchAdvancedClass")
                .addAnnotation(Override.class)
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC)
                .returns(Class.class)
                .addStatement("return this.searchAdvancedClass")
                .build());

        builder.addMethod(MethodSpec.methodBuilder("getByTypeName")
                .addModifiers(javax.lang.model.element.Modifier.PUBLIC, javax.lang.model.element.Modifier.STATIC)
                .returns(searchRecordTypeEnumClassName)
                .addParameter(String.class, "typeName")
                .addCode("for ($T value : values()) {\n" +
                                "    if (value.typeName.equals($N)) {\n" +
                                "      return value;\n" +
                                "    }\n" +
                                "}\n" +
                                "return null;\n",
                        searchRecordTypeEnumClassName, "typeName"
                )
                .build());


        TypeSpec typeSpec = builder.build();
//        System.out.println(typeSpec.toString());

        JavaFile jfile = JavaFile.builder(searchRecordTypeEnumClassName.packageName(), typeSpec).build();

        jfile.writeTo(outputFolder);

    }

    static class RecordTypeSpec {
        String name;
        String typeName;
        Enum<?> enumValue;
        String enumConstantName;
        Class<?> recordClass;
        String searchRecordTypeName;
    }

    static class SearchRecordTypeSpec {
        String name;
        String typeName;
        Enum<?> enumValue;
        String enumConstantName;
        Class<?> searchClass;
        Class<?> searchBasicClass;
        Class<?> searchAdvancedClass;
    }

    public void run(String...args) throws Exception {
        genRecordTypeMetaDataModel();
        genSearchRecordTypeMetaDataModel();

        genRecordTypeEnumClass();
        genSearchRecordTypeEnumClass();
    }

}