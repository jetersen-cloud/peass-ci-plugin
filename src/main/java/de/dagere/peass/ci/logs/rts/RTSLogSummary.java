package de.dagere.peass.ci.logs.rts;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.peass.dependency.analysis.data.TestCase;

/**
 * This summarizes logs for showing information in the regular step action.
 *
 * Currently, it only contains a boolean, but it is expected to contain more information for the end user soon.
 *
 * @author reichelt
 *
 */
public class RTSLogSummary {
   
   private static final Logger LOG = LogManager.getLogger(RTSLogSummary.class);

   public static RTSLogSummary createLogSummary(Map<TestCase, RTSLogData> rtsVmRuns, Map<TestCase, RTSLogData> rtsVmRunsPredecessor) {
      boolean versionContainsNonSuccess = rtsVmRuns.values().stream().anyMatch(log -> !log.isSuccess());
      boolean predecessorContainsNonSuccess = rtsVmRunsPredecessor.values().stream().anyMatch(log -> !log.isSuccess());

      boolean versionContainsSuccess = rtsVmRuns.values().stream().anyMatch(log -> log.isSuccess());
      boolean predecessorContainsSuccess = rtsVmRunsPredecessor.values().stream().anyMatch(log -> log.isSuccess());

      boolean versionContainsParametrizedwhithoutIndex = rtsVmRuns.values().stream().anyMatch(log -> log.isParameterizedWithoutIndex());
      boolean predecessorContainsParametrizedwhithoutIndex = rtsVmRunsPredecessor.values().stream().anyMatch(log -> log.isParameterizedWithoutIndex());

      LOG.debug("Errors in logs: {} {}", versionContainsNonSuccess, predecessorContainsNonSuccess);
      RTSLogSummary logSummary = new RTSLogSummary(versionContainsNonSuccess, predecessorContainsNonSuccess, versionContainsSuccess, predecessorContainsSuccess,
            versionContainsParametrizedwhithoutIndex, predecessorContainsParametrizedwhithoutIndex);
      return logSummary;
   }

   private final boolean errorInCurrentVersionOccured;
   private final boolean errorInPredecessorVersionOccured;
   private final boolean versionContainsSuccess;
   private final boolean predecessorContainsSuccess;
   private final boolean versionContainsParametrizedwhithoutIndex;
   private final boolean predecessorContainsParametrizedwhithoutIndex;

   public RTSLogSummary(final boolean errorInCurrentVersionOccured, final boolean errorInPredecessorVersionOccured, boolean versionContainsSuccess,
         boolean predecessorContainsSuccess, boolean versionContainsParametrizedwhithoutIndex, boolean predecessorContainsParametrizedwhithoutIndex) {
      this.errorInCurrentVersionOccured = errorInCurrentVersionOccured;
      this.errorInPredecessorVersionOccured = errorInPredecessorVersionOccured;
      this.versionContainsSuccess = versionContainsSuccess;
      this.predecessorContainsSuccess = predecessorContainsSuccess;
      this.versionContainsParametrizedwhithoutIndex = versionContainsParametrizedwhithoutIndex;
      this.predecessorContainsParametrizedwhithoutIndex = predecessorContainsParametrizedwhithoutIndex;
   }

   public boolean isErrorInCurrentVersionOccured() {
      return errorInCurrentVersionOccured;
   }

   public boolean isErrorInPredecessorVersionOccured() {
      return errorInPredecessorVersionOccured;
   }

   public boolean isVersionContainsSuccess() {
      return versionContainsSuccess;
   }

   public boolean isPredecessorContainsSuccess() {
      return predecessorContainsSuccess;
   }

   public boolean isVersionContainsParametrizedwhithoutIndex() {
      return versionContainsParametrizedwhithoutIndex;
   }

   public boolean isPredecessorContainsParametrizedwhithoutIndex() {
      return predecessorContainsParametrizedwhithoutIndex;
   }

}
