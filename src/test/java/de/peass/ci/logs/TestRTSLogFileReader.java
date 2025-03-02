package de.peass.ci.logs;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.dagere.peass.ci.logs.RTSLogFileReader;
import de.dagere.peass.ci.logs.rts.RTSLogData;
import de.dagere.peass.dependency.analysis.data.TestCase;

public class TestRTSLogFileReader {

   
   static final TestCase TEST1 = new TestCase("de.test.CalleeTest#onlyCallMethod1");
   static final TestCase TEST2 = new TestCase("de.test.CalleeTest#onlyCallMethod2");
   
   private RTSLogFileTestUtil util = new RTSLogFileTestUtil(TEST1, "demo-vis2");

   @BeforeEach
   public void init() throws IOException {
      File source = new File("src/test/resources/demo-results-logs/demo-vis2_peass");
      util.init(source);
   }
   
   @Test
   public void testReading() throws JsonParseException, JsonMappingException, IOException {
      RTSLogFileReader reader = util.initializeReader();
      Map<String, File> testcases = reader.findProcessSuccessRuns();

      Assert.assertEquals(1, testcases.size());
      File testRunningFile = testcases.get("a23e385264c31def8dcda86c3cf64faa698c62d8");
      Assert.assertTrue(testRunningFile.exists());

      Assert.assertTrue(reader.isLogsExisting());

      Map<TestCase, RTSLogData> rtsVmRuns = reader.getRtsVmRuns("a23e385264c31def8dcda86c3cf64faa698c62d8");
      Assert.assertEquals(2, rtsVmRuns.size());

      checkFirstTest(rtsVmRuns);

      checkSecondTest(reader);

      String rtsLog = reader.getRTSLog();
      Assert.assertEquals("This is a rts log test", rtsLog);
   }

   private void checkSecondTest(RTSLogFileReader reader) {
      Map<TestCase, RTSLogData> rtsVmRunsPredecessor = reader.getRtsVmRuns("33ce17c04b5218c25c40137d4d09f40fbb3e4f0f");
      Assert.assertEquals(2, rtsVmRunsPredecessor.size());
      RTSLogData rtsLogData = rtsVmRunsPredecessor.get(TEST1);
      Assert.assertEquals("33ce17c04b5218c25c40137d4d09f40fbb3e4f0f", rtsLogData.getVersion());
      Assert.assertTrue(rtsLogData.isSuccess());
      Assert.assertFalse(rtsLogData.isParameterizedWithoutIndex());
   }

   private void checkFirstTest(Map<TestCase, RTSLogData> rtsVmRuns) {
      File dataFile1 = rtsVmRuns.get(TEST1).getMethodFile();
      Assert.assertTrue(dataFile1.exists());
      RTSLogData logDataTest2 = rtsVmRuns.get(TEST2);
      File dataFile2 = logDataTest2.getMethodFile();
      Assert.assertTrue(dataFile2.exists());
      Assert.assertFalse(logDataTest2.isSuccess());
      Assert.assertFalse(logDataTest2.isParameterizedWithoutIndex());
   }

   @Test
   public void testReadingOnlyOverviewExists() throws IOException {
      FileUtils.deleteDirectory(RTSLogFileTestUtil.testFolder);

      RTSLogFileReader reader = util.initializeReader();
      Assert.assertTrue(reader.isLogsExisting());
   }
}
