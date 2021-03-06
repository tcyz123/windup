package org.jboss.windup.graph.model.meta.xml;

import org.jboss.windup.graph.model.meta.javaclass.JPAEntityFacetModel;
import org.jboss.windup.graph.renderer.Label;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("JPAConfigurationFacet")
public interface JPAConfigurationFacetModel extends XmlMetaFacetModel {

	@Label
	@Property("specificationVersion")
	public String getSpecificationVersion();

	@Property("specificationVersion")
	public void setSpecificationVersion(String version);
	
	@Adjacency(label="jpaEntity", direction=Direction.OUT)
	public Iterable<JPAEntityFacetModel> getJPAEntities();

	@Adjacency(label="jpaEntity", direction=Direction.OUT)
	public void addJPAEntityReference(JPAEntityFacetModel jpaEntity);

}
