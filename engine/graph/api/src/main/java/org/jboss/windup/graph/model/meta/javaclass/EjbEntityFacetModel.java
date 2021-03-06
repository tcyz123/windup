package org.jboss.windup.graph.model.meta.javaclass;

import java.util.Iterator;

import org.jboss.windup.graph.model.meta.BaseMetaModel;
import org.jboss.windup.graph.model.resource.JavaClassModel;
import org.jboss.windup.graph.renderer.Label;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("EJBEntityFacet")
public interface EjbEntityFacetModel extends JavaClassMetaFacetModel {

	@Label
	@Property("ejbEntityName")
	public String getEjbEntityName();

	@Property("ejbEntityName")
	public String setEjbEntityName(String ejbEntityName);
	
	@Property("displayName")
	public String getDisplayName();

	@Property("displayName")
	public void setDisplayName(String displayName);
	
	@Property("ejbId")
	public String getEjbId();

	@Property("ejbId")
	public void setEjbId(String id);
	
	@Property("persistenceType")
	public String getPersistenceType();

	@Property("persistenceType")
	public void setPersistenceType(String PersistenceType);
	
	@Adjacency(label = "ejbLocal", direction = Direction.OUT)
	public void setEjbLocal(JavaClassModel ejbLocal);

	@Adjacency(label = "ejbLocal", direction = Direction.OUT)
	public JavaClassModel getEjbLocal();
	
	@Adjacency(label = "ejbLocalHome", direction = Direction.OUT)
	public void setEjbLocalHome(JavaClassModel ejbLocalHome);

	@Adjacency(label = "ejbLocalHome", direction = Direction.OUT)
	public JavaClassModel getEjbLocalHome();
	

	@Adjacency(label="meta", direction=Direction.OUT)
	public Iterator<BaseMetaModel> getMeta();
	
	@Adjacency(label="meta", direction=Direction.OUT)
	public void addMeta(final BaseMetaModel resource);
}
