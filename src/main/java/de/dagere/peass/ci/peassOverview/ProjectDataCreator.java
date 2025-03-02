package de.dagere.peass.ci.peassOverview;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import de.dagere.peass.analysis.changes.ProjectChanges;
import de.dagere.peass.ci.MeasureVersionBuilder;
import de.dagere.peass.dependency.persistence.ExecutionData;
import de.dagere.peass.dependency.persistence.StaticTestSelection;
import de.dagere.peass.folders.ResultsFolders;
import de.dagere.peass.testtransformation.TestMethodHelper;
import de.dagere.peass.utils.Constants;
import de.dagere.peass.vcs.CommitList;
import de.dagere.peass.vcs.GitCommit;
import hudson.model.Run;
import hudson.model.TaskListener;

public class ProjectDataCreator {

   private static final Logger LOG = LogManager.getLogger(TestMethodHelper.class);

   private final List<Project> projects;
   private final String referencePoint;

   public ProjectDataCreator(List<Project> projects, String referencePoint) {
      this.projects = projects;
      this.referencePoint = referencePoint;
   }

   public Map<String, ProjectData> generateAllProjectData(final Run<?, ?> run, final TaskListener listener) {
      Map<String, ProjectData> projectData = new LinkedHashMap<>();
      for (Project project : projects) {
         String projectPath = project.getProject();
         String projectName = project.getProjectName();

         try {
            File projectWorkspace = new File(run.getRootDir(),
                  ".." + File.separator + ".." + File.separator + projectPath + File.separator + MeasureVersionBuilder.PEASS_FOLDER_NAME);
            if (projectWorkspace.exists()) {
               projectName = analyzeProject(run, listener, projectData, project, projectName, projectWorkspace);
            } else {
               listener.getLogger().println("Could not analyze " + projectPath + ": expected file " + projectWorkspace.getAbsolutePath() + " did not exist");
            }
         } catch (IOException e) {
            listener.getLogger().println("Could not analyze " + projectPath);
            LOG.error("Was not able to analyze project {}", projectName);
            e.printStackTrace();
            projectData.put(projectName, new ProjectData(null, null, true));
         }

      }
      return projectData;

   }

   private String analyzeProject(final Run<?, ?> run, final TaskListener listener, Map<String, ProjectData> projectData, Project project, String projectName, File projectWorkspace)
         throws IOException {
      if (project.getProjectName().equals(".")) {
         projectName = projectWorkspace.getParentFile().getCanonicalFile().getName();
      }

      ProjectData currentProjectData = getProjectData(run, listener, projectWorkspace, projectName);
      projectData.put(projectName, currentProjectData);
      return projectName;
   }

   private ProjectData getProjectData(final Run<?, ?> run, final TaskListener listener, File projectWorkspace, String projectName) throws IOException {
      ResultsFolders resultsFolders = new ResultsFolders(projectWorkspace, projectName);

      List<String> includedCommits = findIncludedCommits(resultsFolders);

      StaticTestSelection selection = Constants.OBJECTMAPPER.readValue(resultsFolders.getStaticTestSelectionFile(), StaticTestSelection.class);

      removeNotIncludedCommits(includedCommits, selection);

      if (resultsFolders.getTraceTestSelectionFile().exists()) {
         ExecutionData data = Constants.OBJECTMAPPER.readValue(resultsFolders.getTraceTestSelectionFile(), ExecutionData.class);

         PeassOverviewUtils.removeNotTraceSelectedTests(selection, data);
      }

      ProjectChanges projectChanges = new ProjectChanges();
      final File changeFile = resultsFolders.getChangeFile();
      if (changeFile.exists()) {
         projectChanges = Constants.OBJECTMAPPER.readValue(resultsFolders.getChangeFile(), ProjectChanges.class);
      } else {
         listener.getLogger().println(changeFile.getAbsolutePath() + " does not exist! If there are no Trace-based Selected Tests, that's ok.");
      }
      ProjectData currentProjectData = new ProjectData(selection, projectChanges, false);
      return currentProjectData;
   }

   private void removeNotIncludedCommits(List<String> includedCommits, StaticTestSelection selection) {
      Set<String> usedVersions = new HashSet<>(selection.getCommits().keySet());
      for (String version : usedVersions) {
         if (!includedCommits.contains(version)) {
            selection.getCommits().remove(version);
         }
      }
   }

   private static final DateTimeFormatter DATE_PARSER = ISODateTimeFormat.date();

   private List<String> findIncludedCommits(ResultsFolders resultsFolders) throws IOException, StreamReadException, DatabindException {
      DateTime currentDate = new DateTime().withTimeAtStartOfDay();
      DateTime yesterday = currentDate.minusDays(1).withTimeAtStartOfDay();
      DateTime oneWeekBefore = currentDate.minusDays(7).withTimeAtStartOfDay();

      List<String> includedCommits = new LinkedList<>();
      CommitList commitMetadata = Constants.OBJECTMAPPER.readValue(resultsFolders.getCommitMetadataFile(), CommitList.class);

      for (GitCommit commit : commitMetadata.getCommits()) {
         DateTime commitDate = getCommitDate(commit);

         if (referencePoint.equals(PeassOverviewBuilder.LAST_DAY)) {
            if (commitDate.isEqual(currentDate) || commitDate.isEqual(yesterday)) {
               includedCommits.add(commit.getTag());
            }
         } else if (referencePoint.equals(PeassOverviewBuilder.LAST_WEEK)) {
            if (commitDate.isEqual(currentDate) || (commitDate.isAfter(oneWeekBefore) && commitDate.isBefore(currentDate))) {
               includedCommits.add(commit.getTag());
            }
         } else if (referencePoint.equals(PeassOverviewBuilder.ALL)) {
            includedCommits.add(commit.getTag());
         }
      }

      LOG.debug("Commits: " + includedCommits);
      return includedCommits;
   }

   private DateTime getCommitDate(GitCommit commit) {
      String jtdate = commit.getDate();
      String onlyDay = jtdate.substring(0, jtdate.indexOf(' '));
      DateTime commitDate = DATE_PARSER.parseDateTime(onlyDay);
      return commitDate;
   }
}
