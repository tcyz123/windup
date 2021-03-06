package org.jboss.windup.addon.config.parser.xml.when;

import org.jboss.windup.addon.config.parser.ElementHandler;
import org.jboss.windup.addon.config.parser.NamespaceElementHandler;
import org.jboss.windup.addon.config.parser.ParserContext;
import org.ocpsoft.rewrite.config.True;
import org.w3c.dom.Element;

@NamespaceElementHandler(elementName = "true", namespace = "http://windup.jboss.org/v1/xml")
public class TrueHandler implements ElementHandler<True>
{
   @Override
   public True processElement(ParserContext handlerManager, Element element)
   {
      return new True();
   }
}
