package de.monticore.bpmn.xml;

import com.google.common.base.CaseFormat;

/**
 * Utilities for serializing BPMN models.
 */
public class WorkflowXmlUtils {

    public static String getAsResourceKey(final String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, name);
    }

}
