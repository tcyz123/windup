package org.jboss.windup.engine.visitor.reporter;

import javax.inject.Inject;

import org.jboss.windup.engine.visitor.AbstractGraphVisitor;
import org.jboss.windup.engine.visitor.VisitorPhase;
import org.jboss.windup.graph.dao.JavaClassDao;
import org.jboss.windup.graph.model.resource.ArchiveEntryResourceModel;
import org.jboss.windup.graph.model.resource.JavaClassModel;
import org.jboss.windup.graph.model.resource.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Find situations where a class is provided by multiple archives.
 * 
 * @author bradsdavis@gmail.com
 * 
 */
public class DuplicateClassReporter extends AbstractGraphVisitor
{

    private static final Logger LOG = LoggerFactory.getLogger(DuplicateClassReporter.class);

    @Inject
    private JavaClassDao javaClassDao;

    @Override
    public VisitorPhase getPhase()
    {
        return VisitorPhase.REPORTING;
    }

    @Override
    public void run()
    {
        for (JavaClassModel clz : javaClassDao.getAllDuplicateClasses())
        {
            LOG.info("Duplicate class: " + clz.getQualifiedName());

            for (ResourceModel resource : clz.getResources())
            {
                if (resource instanceof ArchiveEntryResourceModel)
                {
                    ArchiveEntryResourceModel ar = (ArchiveEntryResourceModel) resource;
                    LOG.info(" - Provided by: " + ar.getArchive().getArchiveName() + " -> " + clz.getQualifiedName());
                }
            }
        }
    }
}
