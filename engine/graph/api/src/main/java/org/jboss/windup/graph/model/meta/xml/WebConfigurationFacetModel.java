package org.jboss.windup.graph.model.meta.xml;

import java.util.Iterator;

import org.jboss.windup.graph.model.meta.BaseMetaModel;
import org.jboss.windup.graph.renderer.Label;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("WebFacet")
public interface WebConfigurationFacetModel extends XmlMetaFacetModel {

	@Label
	@Property("specificationVersion")
	public String getSpecificationVersion();

	@Property("specificationVersion")
	public void setSpecificationVersion(String version);
	
	@Property("displayName")
	public String getDisplayName();

	@Property("displayName")
	public void setDisplayName(String displayName);

	@Adjacency(label="meta", direction=Direction.OUT)
	public Iterator<BaseMetaModel> getMeta();
	
	@Adjacency(label="meta", direction=Direction.OUT)
	public void addMeta(final BaseMetaModel resource);
}
