package de.dagere.peass.ci.overview;

import java.io.Serializable;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import de.dagere.peass.dependency.analysis.data.TestCase;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;

public class OverviewBuilder extends Builder implements SimpleBuildStep, Serializable {

   private static final long serialVersionUID = 153607148541095843L;

   private String tagForComparison;

   @DataBoundConstructor
   public OverviewBuilder() {
   }

   @Override
   public void perform(final Run<?, ?> run, final FilePath workspace, final EnvVars env, final Launcher launcher, final TaskListener listener) {
      listener.getLogger().println("Creating overview action");
      
      OverviewData data = new OverviewData();
      data.getOverviewData().put(new TestCase("Test#test"), new TestCaseOverviewData(100, 15));
      data.getOverviewData().put(new TestCase("Test#test2"), new TestCaseOverviewData(100, 7));
      data.getOverviewData().put(new TestCase("OtherTest#test"), new TestCaseOverviewData(100, -8));
      
      OverviewAction overviewAction = new OverviewAction();
      overviewAction.setData(data);
      
      run.addAction(overviewAction);
      
      listener.getLogger().println("Action added: " + overviewAction.getDisplayName() + " " + overviewAction.getUrlName());
      
   }

   public String getTagForComparison() {
      return tagForComparison;
   }

   @DataBoundSetter
   public void setTagForComparison(String tagForComparison) {
      this.tagForComparison = tagForComparison;
   }

   @Symbol("createPerformanceOverview")
   @Extension
   public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

      @Override
      public boolean isApplicable(Class<? extends AbstractProject> jobType) {
         return true;
      }
      
      @Override
      public String getDisplayName() {
         return "asd";
//         return Messages.OverviewBuilder_DescriptorImpl_DisplayName();
      }
   }
}
