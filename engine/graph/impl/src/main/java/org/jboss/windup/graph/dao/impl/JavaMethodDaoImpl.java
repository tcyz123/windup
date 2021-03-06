package org.jboss.windup.graph.dao.impl;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.jboss.windup.graph.dao.JavaMethodDao;
import org.jboss.windup.graph.dao.JavaParameterDao;
import org.jboss.windup.graph.model.resource.JavaClassModel;
import org.jboss.windup.graph.model.resource.JavaMethodModel;
import org.jboss.windup.graph.model.resource.JavaParameterModel;

public class JavaMethodDaoImpl extends BaseDaoImpl<JavaMethodModel> implements JavaMethodDao
{
    @Inject
    private JavaParameterDao paramDao;
    
    public JavaMethodDaoImpl()
    {
        super(JavaMethodModel.class);
    }

    public synchronized JavaMethodModel createJavaMethod(JavaClassModel clz, String javaMethod, JavaClassModel... params)
    {
        for (JavaMethodModel method : clz.getMethod(javaMethod))
        {
            if (method.countParameters() != params.length)
            {
                continue;
            }
            if (methodParametersMatch(method, params))
            {
                return method;
            }
        }

        JavaMethodModel method = create();
        method.setMethodName(javaMethod);

        for (int i = 0, j = params.length; i < j; i++)
        {
            JavaParameterModel param = paramDao.create();
            param.setPosition(i);
            param.setJavaType(params[i]);
        }

        return method;
    }

    protected boolean methodParametersMatch(JavaMethodModel method, JavaClassModel... params)
    {
        for (int i = 0, j = params.length; i < j; i++)
        {
            JavaParameterModel param = method.getParameter(i);
            JavaClassModel paramVal = param.getJavaType();
            if (!StringUtils.equals(paramVal.getQualifiedName(), paramVal.getQualifiedName()))
            {
                return false;
            }
        }

        return true;
    }

}
