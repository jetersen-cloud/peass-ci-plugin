package de.dagere.peass.ci.overview;

import de.dagere.peass.ci.VisibleAction;

public class OverviewAction extends VisibleAction {

   private OverviewData data;

   public OverviewAction() {
      
   }
   
   public OverviewData getData() {
      return data;
   }

   public void setData(OverviewData data) {
      this.data = data;
   }

   @Override
   public String getIconFileName() {
      return "notepad.png";
   }

   @Override
   public String getDisplayName() {
      return "Overview Action";
   }

   @Override
   public String getUrlName() {
      return "performanceOverview";
   }

}
