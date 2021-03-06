/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.windup.addon.config.example.java;

import org.jboss.windup.addon.config.selectables.SelectableCondition;
import org.jboss.windup.graph.model.meta.xml.MavenFacetModel;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface MavenPomFileCondition extends SelectableCondition<MavenPomFile, MavenPomFileCondition, MavenFacetModel>
{

    MavenPomFileCondition withDoctype(String string);

}
