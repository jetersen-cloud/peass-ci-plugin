package de.dagere.peass.ci.overview;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import de.dagere.peass.dependency.analysis.data.TestCase;

public class OverviewData implements Serializable {

   private static final long serialVersionUID = -2751023604849924936L;
   
   private Map<TestCase, TestCaseOverviewData> overviewData = new LinkedHashMap<>();

   public Map<TestCase, TestCaseOverviewData> getOverviewData() {
      return overviewData;
   }

   public void setOverviewData(Map<TestCase, TestCaseOverviewData> overviewData) {
      this.overviewData = overviewData;
   }
}
