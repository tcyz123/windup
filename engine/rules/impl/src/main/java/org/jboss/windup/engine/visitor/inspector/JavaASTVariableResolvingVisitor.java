/*
 * Copyright (c) 2013 Red Hat, Inc. and/or its affiliates.
 *  
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Brad Davis - bradsdavis@gmail.com - Initial API and implementation
 */
package org.jboss.windup.engine.visitor.inspector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.jboss.windup.engine.visitor.inspector.decompiler.ClassCandidate;
import org.jboss.windup.graph.WindupContext;
import org.jboss.windup.graph.dao.JavaClassDao;
import org.jboss.windup.graph.model.resource.JavaClassModel;

/**
 * Runs through the source code and checks "type" uses against the blacklisted class entries.
 * 
 * @author bradsdavis
 * 
 */
public class JavaASTVariableResolvingVisitor extends ASTVisitor
{

    // TODO: Fix the Method and Constructor visitors to properly
    // handle checking the blacklist. Because they don't pass just the class through, a blacklisted constructor
    // (org.example.Sample(x, y, z)) will never match the blacklist candidate
    // present in the blacklist candidate set (org.example.Sample)

    private static final Log LOG = LogFactory.getLog(JavaASTVariableResolvingVisitor.class);

    private final CompilationUnit cu;
    private final WindupContext windupContext;
    private final JavaClassModel javaClass;
    private final JavaClassDao javaClassDao;

    private final Map<String, String> classNameToFullyQualified = new HashMap<String, String>();

    Set<String> names = new HashSet<String>();
    Map<String, String> nameInstance = new HashMap<String, String>();
    Set<String> blacklistCandidates = new HashSet<String>();

    public JavaASTVariableResolvingVisitor(CompilationUnit cu, JavaClassDao javaClassDao, WindupContext context,
                JavaClassModel javaClass)
    {
        this.cu = cu;
        this.javaClassDao = javaClassDao;
        this.javaClass = javaClass;
        this.windupContext = context;

        // creates an in-memory cache if all qualified blacklist candidates.
        // this set is leveraged to determine whether to catalog.
        for (JavaClassModel clz : javaClassDao.findLeveragedCandidateBlacklists(javaClass))
        {
            LOG.info("Blacklist w/in Class[" + javaClass.getQualifiedName() + "]: " + clz.getQualifiedName());
            blacklistCandidates.add(clz.getQualifiedName());
        }

        for (JavaClassModel javaImport : javaClass.getImports())
        {
            classNameToFullyQualified.put(StringUtils.substringAfterLast(javaImport.getQualifiedName(), "."),
                        javaImport.getQualifiedName());
        }

        this.names.add("this");
        this.nameInstance.put("this", javaClass.getQualifiedName());
        this.classNameToFullyQualified.put(StringUtils.substringAfterLast(javaClass.getQualifiedName(), "."),
                    javaClass.getQualifiedName());
    }

    private void processConstructor(ConstructorType interest, int lineStart, int startPosition, int length,
                String decoratorPrefix, JavaSourceType sourceType)
    {
        if (!blacklistCandidates.contains(interest.getQualifiedName()))
        {
            return;
        }

        ClassCandidate dr = new ClassCandidate(lineStart, startPosition, length, interest.toString());
        // results.add(dr);
        LOG.info("Prefix: " + decoratorPrefix);
        LOG.info("Candidate: " + dr);
    }

    private void processMethod(MethodType interest, int lineStart, int startPosition, int length,
                String decoratorPrefix, JavaSourceType sourceType)
    {
        if (!blacklistCandidates.contains(interest.getQualifiedName()))
        {
            return;
        }

        ClassCandidate dr = new ClassCandidate(lineStart, startPosition, length, interest.toString());
        // results.add(dr);
        LOG.info("Prefix: " + decoratorPrefix);
        LOG.info("Candidate: " + dr);
    }

    private void processInterest(String interest, int lineStart, int startPosition, int length, String decoratorPrefix,
                JavaSourceType sourceType)
    {

        String sourceString = interest;
        if (!StringUtils.contains(sourceString, "."))
        {
            if (classNameToFullyQualified.containsKey(sourceString))
            {
                sourceString = classNameToFullyQualified.get(sourceString);
            }
        }

        if (!blacklistCandidates.contains(sourceString))
        {
            return;
        }

        ClassCandidate dr = new ClassCandidate(lineStart, startPosition, length, sourceString);
        // results.add(dr);
        LOG.info("Prefix: " + decoratorPrefix);
        LOG.info("Candidate: " + dr);
    }

    private void processType(Type type, String decoratorPrefix, JavaSourceType sourceType)
    {
        if (type == null)
            return;

        int sourcePosition = cu.getLineNumber(type.getStartPosition());
        int startPosition = cu.getColumnNumber(type.getStartPosition());
        int length = type.getLength();

        String sourceString = type.toString();
        if (!StringUtils.contains(sourceString, "."))
        {
            if (classNameToFullyQualified.containsKey(sourceString))
            {
                sourceString = classNameToFullyQualified.get(sourceString);
            }
        }

        if (!blacklistCandidates.contains(sourceString))
        {
            return;
        }

        ClassCandidate dr = new ClassCandidate(sourcePosition, startPosition, length, sourceString);
        // results.add(dr);
        LOG.info("Prefix: " + decoratorPrefix);
        LOG.info("Candidate: " + dr);
    }

    private void processName(Name name, String decoratorPrefix, int lineNumber, int startPosition, int length)
    {
        if (name == null)
            return;

        String sourceString = name.toString();

        if (!StringUtils.contains(sourceString, "."))
        {
            if (classNameToFullyQualified.containsKey(sourceString))
            {
                sourceString = classNameToFullyQualified.get(sourceString);
            }
        }

        if (!blacklistCandidates.contains(sourceString))
        {
            return;
        }

        ClassCandidate dr = new ClassCandidate(lineNumber, startPosition, length, sourceString);
        // TODO: results.add(dr);
        LOG.info("Prefix: " + decoratorPrefix);
        LOG.info("Candidate: " + dr);
    }

    @Override
    public boolean visit(MethodDeclaration node)
    {
        // get a method's return type.
        Type returnType = node.getReturnType2();
        if (returnType != null)
        {
            processType(returnType, "Return type", JavaSourceType.TYPE);
        }

        List<SingleVariableDeclaration> parameters = (List<SingleVariableDeclaration>) node.parameters();
        if (parameters != null)
        {
            for (SingleVariableDeclaration type : parameters)
            {
                // make it fully qualified.
                String typeName = type.getType().toString();
                if (classNameToFullyQualified.containsKey(typeName))
                {
                    typeName = classNameToFullyQualified.get(typeName);
                }
                // now add it as a local variable.
                this.names.add(type.getName().toString());
                this.nameInstance.put(type.getName().toString(), typeName);

                processType(type.getType(), "Method parameter", JavaSourceType.TYPE);
            }
        }

        List<Name> throwsTypes = node.thrownExceptions();
        if (throwsTypes != null)
        {
            for (Name name : throwsTypes)
            {
                processName(name, "Throws", cu.getLineNumber(node.getStartPosition()),
                            cu.getColumnNumber(name.getStartPosition()), name.getLength());
            }
        }

        return super.visit(node);
    }

    @Override
    public boolean visit(InstanceofExpression node)
    {
        Type type = node.getRightOperand();
        processType(type, "Instance Of", JavaSourceType.TYPE);

        return super.visit(node);
    }

    public boolean visit(org.eclipse.jdt.core.dom.ThrowStatement node)
    {
        if (node.getExpression() instanceof ClassInstanceCreation)
        {
            ClassInstanceCreation cic = (ClassInstanceCreation) node.getExpression();
            processType(cic.getType(), "Throwing", JavaSourceType.TYPE);
        }

        return super.visit(node);
    }

    public boolean visit(org.eclipse.jdt.core.dom.CatchClause node)
    {
        Type catchType = node.getException().getType();
        processType(catchType, "Catching", JavaSourceType.TYPE);

        return super.visit(node);
    }

    @Override
    public boolean visit(ReturnStatement node)
    {
        if (node.getExpression() instanceof ClassInstanceCreation)
        {
            ClassInstanceCreation cic = (ClassInstanceCreation) node.getExpression();
            processType(cic.getType(), "Declaring", JavaSourceType.TYPE);
        }
        return super.visit(node);
    }

    @Override
    public boolean visit(FieldDeclaration node)
    {
        for (int i = 0; i < node.fragments().size(); ++i)
        {
            String nodeType = node.getType().toString();
            if (!StringUtils.contains(nodeType, "."))
            {
                if (classNameToFullyQualified.containsKey(nodeType))
                {
                    nodeType = classNameToFullyQualified.get(nodeType);
                }
            }
            VariableDeclarationFragment frag = (VariableDeclarationFragment) node.fragments().get(i);
            frag.resolveBinding();
            this.names.add(frag.getName().getIdentifier());
            this.nameInstance.put(frag.getName().toString(), nodeType.toString());

            processType(node.getType(), "Declaring type", JavaSourceType.TYPE);
        }
        return true;
    }

    @Override
    public boolean visit(MarkerAnnotation node)
    {
        processName(node.getTypeName(), "Annotation", cu.getLineNumber(node.getStartPosition()),
                    cu.getColumnNumber(cu.getStartPosition()), cu.getLength());
        return super.visit(node);
    }

    @Override
    public boolean visit(NormalAnnotation node)
    {
        processName(node.getTypeName(), "Annotation", cu.getLineNumber(node.getStartPosition()),
                    cu.getColumnNumber(node.getStartPosition()), node.getLength());
        return super.visit(node);
    }

    @Override
    public boolean visit(SingleMemberAnnotation node)
    {
        processName(node.getTypeName(), "Annotation", cu.getLineNumber(node.getStartPosition()),
                    cu.getColumnNumber(node.getStartPosition()), node.getLength());
        return super.visit(node);
    }

    public boolean visit(TypeDeclaration node)
    {
        Object clzInterfaces = node.getStructuralProperty(TypeDeclaration.SUPER_INTERFACE_TYPES_PROPERTY);
        Object clzSuperClasses = node.getStructuralProperty(TypeDeclaration.SUPERCLASS_TYPE_PROPERTY);

        if (clzInterfaces != null)
        {
            if (List.class.isAssignableFrom(clzInterfaces.getClass()))
            {
                List<?> clzInterfacesList = (List<?>) clzInterfaces;
                for (Object clzInterface : clzInterfacesList)
                {
                    if (clzInterface instanceof SimpleType)
                    {
                        processType((SimpleType) clzInterface, "Implements Type", JavaSourceType.TYPE);
                    }
                    else
                    {
                        LOG.warn(clzInterface);
                    }
                }
            }
        }
        if (clzSuperClasses != null)
        {
            if (clzSuperClasses instanceof SimpleType)
            {
                processType((SimpleType) clzSuperClasses, "Extends Type", JavaSourceType.TYPE);
            }
            else
            {
                LOG.warn(clzSuperClasses);
            }
        }

        return super.visit(node);
    }

    @Override
    public boolean visit(VariableDeclarationStatement node)
    {
        for (int i = 0; i < node.fragments().size(); ++i)
        {
            String nodeType = node.getType().toString();
            if (!StringUtils.contains(nodeType, "."))
            {
                if (classNameToFullyQualified.containsKey(nodeType))
                {
                    nodeType = classNameToFullyQualified.get(nodeType);
                }
            }

            VariableDeclarationFragment frag = (VariableDeclarationFragment) node.fragments().get(i);
            this.names.add(frag.getName().getIdentifier());
            this.nameInstance.put(frag.getName().toString(), nodeType.toString());
        }
        processType(node.getType(), "Declaring type", JavaSourceType.TYPE);

        return super.visit(node);
    }

    @Override
    public boolean visit(ImportDeclaration node)
    {
        String name = node.getName().toString();
        if (!node.isOnDemand())
        {
            String clzName = StringUtils.substringAfterLast(name, ".");
            classNameToFullyQualified.put(clzName, name);
            processInterest(node.getName().toString(), cu.getLineNumber(node.getName().getStartPosition()),
                        cu.getColumnNumber(node.getName().getStartPosition()), node.getName().getLength(), "Import of",
                        JavaSourceType.IMPORT);
        }
        else
        {
            for (String knownClz : classNameToFullyQualified.values())
            {
                if (StringUtils.startsWith(knownClz, name))
                {
                    processInterest(knownClz, cu.getLineNumber(node.getName().getStartPosition()),
                                cu.getColumnNumber(node.getName().getStartPosition()), node.getName().getLength(),
                                "Leads to import of", JavaSourceType.IMPORT);
                }
            }
        }

        return super.visit(node);
    }

    /***
     * Takes the MethodInvocation, and attempts to resolve the types of objects passed into the method invocation.
     */
    public boolean visit(MethodInvocation node)
    {
        if (!StringUtils.contains(node.toString(), "."))
        {
            // it must be a local method. ignore.
            return true;
        }

        String nodeName = StringUtils.removeStart(node.toString(), "this.");

        List arguements = node.arguments();
        List<String> resolvedParams = methodParameterGuesser(arguements);

        String objRef = StringUtils.substringBefore(nodeName, "." + node.getName().toString());

        if (nameInstance.containsKey(objRef))
        {
            objRef = nameInstance.get(objRef);
        }

        if (classNameToFullyQualified.containsKey(objRef))
        {
            objRef = classNameToFullyQualified.get(objRef);
        }

        MethodType methodCall = new MethodType(objRef, node.getName().toString(), resolvedParams);
        processMethod(methodCall, cu.getLineNumber(node.getName().getStartPosition()),
                    cu.getColumnNumber(node.getName().getStartPosition()), node.getName().getLength(), "Usage of",
                    JavaSourceType.METHOD);

        return super.visit(node);
    }

    @Override
    public boolean visit(ClassInstanceCreation node)
    {
        String nodeType = node.getType().toString();
        if (classNameToFullyQualified.containsKey(nodeType))
        {
            nodeType = classNameToFullyQualified.get(nodeType);
        }

        List<String> resolvedParams = this.methodParameterGuesser(node.arguments());

        ConstructorType resolvedConstructor = new ConstructorType(nodeType, resolvedParams);
        processConstructor(resolvedConstructor, cu.getLineNumber(node.getType().getStartPosition()),
                    cu.getColumnNumber(node.getType().getStartPosition()), node.getType().getLength(),
                    "Constructing type", JavaSourceType.CONSTRUCT);

        return super.visit(node);
    }

    private List<String> methodParameterGuesser(List arguements)
    {
        List<String> resolvedParams = new ArrayList<String>(arguements.size());
        for (Object o : arguements)
        {
            if (o instanceof SimpleName)
            {
                String name = nameInstance.get(o.toString());
                if (name != null)
                {
                    resolvedParams.add(name);
                }
                else
                {
                    resolvedParams.add("Undefined");
                }
            }
            else if (o instanceof StringLiteral)
            {
                resolvedParams.add("java.lang.String");
            }
            else if (o instanceof FieldAccess)
            {
                String field = ((FieldAccess) o).getName().toString();

                if (names.contains(field))
                {
                    resolvedParams.add(nameInstance.get(field));
                }
                else
                {
                    resolvedParams.add("Undefined");
                }
            }
            else if (o instanceof CastExpression)
            {
                String type = ((CastExpression) o).getType().toString();
                type = qualifyType(type);
                resolvedParams.add(type);
            }
            else if (o instanceof MethodInvocation)
            {
                String on = ((MethodInvocation) o).getName().toString();
                if (StringUtils.equals(on, "toString"))
                {
                    if (((MethodInvocation) o).arguments().size() == 0)
                    {
                        resolvedParams.add("java.lang.String");
                    }
                }
                else
                {
                    resolvedParams.add("Undefined");
                }
            }
            else if (o instanceof NumberLiteral)
            {
                if (StringUtils.endsWith(o.toString(), "L"))
                {
                    resolvedParams.add("long");
                }
                else if (StringUtils.endsWith(o.toString(), "f"))
                {
                    resolvedParams.add("float");
                }
                else if (StringUtils.endsWith(o.toString(), "d"))
                {
                    resolvedParams.add("double");
                }
                else
                {
                    resolvedParams.add("int");
                }
            }
            else if (o instanceof BooleanLiteral)
            {
                resolvedParams.add("boolean");
            }
            else if (o instanceof ClassInstanceCreation)
            {
                String nodeType = ((ClassInstanceCreation) o).getType().toString();
                if (classNameToFullyQualified.containsKey(nodeType))
                {
                    nodeType = classNameToFullyQualified.get(nodeType.toString());
                }
                resolvedParams.add(nodeType);
            }
            else if (o instanceof org.eclipse.jdt.core.dom.CharacterLiteral)
            {
                resolvedParams.add("char");
            }
            else if (o instanceof InfixExpression)
            {
                String expression = o.toString();
                if (StringUtils.contains(expression, "\""))
                {
                    resolvedParams.add("java.lang.String");
                }
                else
                {
                    resolvedParams.add("Undefined");
                }
            }
            else
            {
                LOG.info("Unable to determine type: " + o.getClass() + ReflectionToStringBuilder.toString(o));
                resolvedParams.add("Undefined");
            }
        }

        return resolvedParams;
    }

    private String qualifyType(String objRef)
    {
        // temporarily remove to resolve arrays
        objRef = StringUtils.removeEnd(objRef, "[]");
        if (nameInstance.containsKey(objRef))
        {
            objRef = nameInstance.get(objRef);
        }

        if (classNameToFullyQualified.containsKey(objRef))
        {
            objRef = classNameToFullyQualified.get(objRef);
        }

        return objRef;
    }

    public static class MethodType
    {
        private final String qualifiedName;
        private final String methodName;
        private final List<String> qualifiedParameters;

        public MethodType(String qualifiedName, String methodName, List<String> qualifiedParameters)
        {
            this.qualifiedName = qualifiedName;
            this.methodName = methodName;

            if (qualifiedParameters != null)
            {
                this.qualifiedParameters = qualifiedParameters;
            }
            else
            {
                this.qualifiedParameters = new LinkedList<String>();
            }
        }

        public String getMethodName()
        {
            return methodName;
        }

        public String getQualifiedName()
        {
            return qualifiedName;
        }

        public List<String> getQualifiedParameters()
        {
            return qualifiedParameters;
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(qualifiedName + "." + methodName);
            builder.append("(");

            for (int i = 0, j = qualifiedParameters.size(); i < j; i++)
            {
                if (i > 0)
                {
                    builder.append(", ");
                }
                String param = qualifiedParameters.get(i);
                builder.append(param);
            }
            builder.append(")");

            return builder.toString();
        }
    }

    public static class ConstructorType
    {
        private final String qualifiedName;
        private final List<String> qualifiedParameters;

        public ConstructorType(String qualifiedName, List<String> qualifiedParameters)
        {
            this.qualifiedName = qualifiedName;
            if (qualifiedParameters != null)
            {
                this.qualifiedParameters = qualifiedParameters;
            }
            else
            {
                this.qualifiedParameters = new LinkedList<String>();
            }

        }

        public String getQualifiedName()
        {
            return qualifiedName;
        }

        public List<String> getQualifiedParameters()
        {
            return qualifiedParameters;
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(qualifiedName);
            builder.append("(");

            for (int i = 0, j = qualifiedParameters.size(); i < j; i++)
            {
                if (i > 0)
                {
                    builder.append(", ");
                }
                String param = qualifiedParameters.get(i);
                builder.append(param);
            }
            builder.append(")");

            return builder.toString();
        }
    }

}
