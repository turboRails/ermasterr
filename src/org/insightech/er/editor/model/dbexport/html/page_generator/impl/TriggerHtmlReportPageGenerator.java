package org.insightech.er.editor.model.dbexport.html.page_generator.impl;

import java.util.List;
import java.util.Map;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.html.page_generator.AbstractHtmlReportPageGenerator;
import org.insightech.er.editor.model.diagram_contents.not_element.trigger.Trigger;
import org.insightech.er.util.Format;

public class TriggerHtmlReportPageGenerator extends AbstractHtmlReportPageGenerator {

    public TriggerHtmlReportPageGenerator(final Map<Object, Integer> idMap) {
        super(idMap);
    }

    @Override
    public String getType() {
        return "trigger";
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getObjectList(final ERDiagram diagram) {
        final List list = diagram.getDiagramContents().getTriggerSet().getObjectList();

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getContentArgs(final ERDiagram diagram, final Object object) {
        final Trigger trigger = (Trigger) object;

        final String description = Format.null2blank(trigger.getDescription());
        final String sql = Format.null2blank(trigger.getSql());

        return new String[] {description, sql};
    }

    @Override
    public String getObjectName(final Object object) {
        final Trigger trigger = (Trigger) object;

        return trigger.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObjectSummary(final Object object) {
        final Trigger trigger = (Trigger) object;

        return trigger.getDescription();
    }
}
