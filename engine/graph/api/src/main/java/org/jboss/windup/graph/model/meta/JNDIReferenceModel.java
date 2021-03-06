package org.jboss.windup.graph.model.meta;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("JNDIReference")
public interface JNDIReferenceModel extends BaseMetaModel {

	@Property("jndiLocation")
	public String getJndiLocation();

	@Property("jndiLocation")
	public void setJndiLocation(String jndiLocation);
	
}
