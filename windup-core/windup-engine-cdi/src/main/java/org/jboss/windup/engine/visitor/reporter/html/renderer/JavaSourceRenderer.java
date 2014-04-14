package org.jboss.windup.engine.visitor.reporter.html.renderer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.windup.engine.WindupContext;
import org.jboss.windup.engine.visitor.base.EmptyGraphVisitor;
import org.jboss.windup.engine.visitor.reporter.html.model.ReportContext;
import org.jboss.windup.engine.visitor.reporter.html.model.SourceReport;
import org.jboss.windup.graph.dao.ArchiveDaoBean;
import org.jboss.windup.graph.dao.JavaClassDaoBean;
import org.jboss.windup.graph.dao.XmlResourceDaoBean;
import org.jboss.windup.graph.model.resource.ArchiveResource;
import org.jboss.windup.graph.model.resource.JavaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class JavaSourceRenderer extends EmptyGraphVisitor {
	private static final Logger LOG = LoggerFactory.getLogger(JavaSourceRenderer.class);
	
	@Inject
	private JavaClassDaoBean javaClassDao;
	
	@Inject
	private XmlResourceDaoBean xmlResources;
	
	@Inject
	private ArchiveDaoBean archiveDao;
	
	@Inject
	private WindupContext context;
	
	private final Configuration cfg;
	
	public JavaSourceRenderer() {
		cfg = new Configuration();
        cfg.setTemplateUpdateDelay(500);
        cfg.setClassForTemplateLoading(this.getClass(), "/");
	}
	
	@Override
	public void run() {
		try {
			for(JavaClass clz : javaClassDao.findClassesWithSource()) {
				visitJavaClass(clz);
			}
		} catch (Exception e) {
			throw new RuntimeException("Exception writing report.", e);
		}
	}
	
	@Override
	public void visitJavaClass(JavaClass entry) {
		try {
			Template template = cfg.getTemplate("/reports/templates/source.ftl");
			
			Map<String, Object> objects = new HashMap<String, Object>();
			
			SourceReport report = new SourceReport();
			report.setSourceName(entry.getQualifiedName());
			File file = entry.getSource().asFile();
			report.setSourceBody(FileUtils.readFileToString(file));
			report.setSourceType("java");
			report.setSourceBlock("");
			objects.put("source", report);
			
			
			//create report context.
			File runDirectory = context.getRunDirectory();
			
			File archiveReportDirectory = new File(runDirectory, "applications");
			File archiveDirectory = new File(archiveReportDirectory, "application");
			FileUtils.forceMkdir(archiveDirectory);
			
			File clzDirectory = new File(archiveDirectory, "classes");
			FileUtils.forceMkdir(clzDirectory);
			ReportContext reportContext = new ReportContext(runDirectory, clzDirectory);
			objects.put("report", reportContext);
			
			File reportRef = new File(clzDirectory, entry.getQualifiedName()+".html");
			template.process(objects, new FileWriter(reportRef));
			
			LOG.info("Wrote overview report: "+reportRef.getAbsolutePath());
		}
		catch(Exception e) {
			LOG.error("Exception writing Report: "+e.getMessage());
		}
	}
}
