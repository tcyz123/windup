package org.jboss.windup.graph.model.meta.xml;

import org.jboss.windup.graph.model.meta.HibernateSessionFactoryModel;
import org.jboss.windup.graph.renderer.Label;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("HibernateConfigurationFacet")
public interface HibernateConfigurationFacetModel extends XmlMetaFacetModel {

	@Label
	@Property("specificationVersion")
	public String getSpecificationVersion();

	@Property("specificationVersion")
	public void setSpecificationVersion(String version);
	
	@Adjacency(label="hibernateSessionFactory", direction=Direction.OUT)
	public Iterable<HibernateSessionFactoryModel> getHibernateSessionFactories();

	@Adjacency(label="hibernateSessionFactory", direction=Direction.OUT)
	public void addHibernateSessionFactory(HibernateSessionFactoryModel hibernateSessionFactor);

}
