package de.dagere.peass.ci.overview;

import java.io.Serializable;

class TestCaseOverviewData implements Serializable {
   
   private static final long serialVersionUID = -3577430712466836187L;
   
   private final double originalTime;
   private final double percentualChange;

   public TestCaseOverviewData(double originalTime, double percentualChange) {
      this.originalTime = originalTime;
      this.percentualChange = percentualChange;
   }

   public double getOriginalTime() {
      return originalTime;
   }

   public double getPercentualChange() {
      return percentualChange;
   }
}