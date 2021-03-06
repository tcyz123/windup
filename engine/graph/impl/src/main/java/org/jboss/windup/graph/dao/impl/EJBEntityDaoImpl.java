package org.jboss.windup.graph.dao.impl;

import javax.inject.Singleton;

import org.jboss.windup.graph.dao.EJBEntityDao;
import org.jboss.windup.graph.model.meta.javaclass.EjbEntityFacetModel;

@Singleton
public class EJBEntityDaoImpl extends BaseDaoImpl<EjbEntityFacetModel> implements EJBEntityDao {
	public EJBEntityDaoImpl() {
		super(EjbEntityFacetModel.class);
	}
}
