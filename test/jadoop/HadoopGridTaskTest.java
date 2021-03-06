// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is part of Jadoop
// Copyright (c) 2016 Grant Braught. All rights reserved.
// 
// Jadoop is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published
// by the Free Software Foundation, either version 3 of the License,
// or (at your option) any later version.
// 
// Jadoop is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public
// License along with Jadoop.
// If not, see <http://www.gnu.org/licenses/>.
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

package jadoop;

import org.junit.Before;
import org.junit.Test;

import jadoop.HadoopGridTask;
import junit.framework.TestCase;

public class HadoopGridTaskTest extends TestCase {

	private HadoopGridTask hgt;
	private HadoopGridTask hgtCapTF;
	private HadoopGridTask hgtCapFT;
	private HadoopGridTask hgtCapFF;

	@Before
	protected void setUp() throws Exception {
		super.setUp();

		hgt = new HadoopGridTask("1", "java ProgramSample", 1000);
		hgtCapTF = new HadoopGridTask("2", "grep \"-cp test datafile.txt\"",
				true, false, 1000);
		hgtCapFT = new HadoopGridTask("3", "\"jar tf\" TestJarFile", false,
				true, 1000);
		hgtCapFF = new HadoopGridTask("4", "\"java ProgramExtra\"", false,
				false, 1000);
	}

	@Test
	public void testFirstConstructor() {
		assertEquals("key was NOT set correctly", "1", hgt.getKey());
		assertEquals("command was NOT set correctly", "java",
				hgt.getCommand()[0]);
		assertTrue("should have captured standard output",
				hgt.captureStandardOutput());
		assertTrue("should have captured the standard error",
				hgt.captureStandardError());
		assertEquals("timeout should be set", 1000, hgt.getTimeout());
	}

	@Test
	public void testSecondContructor() {
		// captures standard input but NOT standard error
		assertEquals("key was NOT set correctly", "2", hgtCapTF.getKey());
		assertEquals("command was NOT set correctly", "grep",
				hgtCapTF.getCommand()[0]);
		assertEquals("command was NOT set correctly", "-cp test datafile.txt",
				hgtCapTF.getCommand()[1]);
		assertTrue("should have captured standard output",
				hgtCapTF.captureStandardOutput());
		assertFalse("should have captured standard error",
				hgtCapTF.captureStandardError());
		assertEquals("timeout should be set", 1000, hgtCapTF.getTimeout());

		// captures standard error but standard error
		assertEquals("key was NOT set correctly", "3", hgtCapFT.getKey());
		assertEquals("command was NOT set correctly", "jar tf",
				hgtCapFT.getCommand()[0]);
		assertEquals("command was NOT set correctly", "TestJarFile",
				hgtCapFT.getCommand()[1]);
		assertFalse("should have captured standard output",
				hgtCapFT.captureStandardOutput());
		assertTrue("should have captured standard error",
				hgtCapFT.captureStandardError());
		assertEquals("timeout should be set", 1000, hgtCapFT.getTimeout());

		// captures neither standard input nor standard error
		assertEquals("key was NOT set correctly", "4", hgtCapFF.getKey());
		assertEquals("command was NOT set correctly", "java ProgramExtra",
				hgtCapFF.getCommand()[0]);
		assertFalse("should have captured standard output",
				hgtCapFF.captureStandardOutput());
		assertFalse("should have captured standard error",
				hgtCapFF.captureStandardError());
		assertEquals("timeout should be set", 1000, hgtCapFF.getTimeout());

	}

	@Test
	public void testSetTimeout() {
		hgt.setTimeout(2000);
		assertEquals("timeout should be set", 2000, hgt.getTimeout());
	}
	
	@Test
	public void testSetTimeoutAlreadyStarted() {
		try {
			hgt.markAsStarted();
			hgt.setTimeout(2000);
			fail("Should not be able to set timeout of already started task.");
		} catch (IllegalStateException e) {
			// pass
		} catch (Exception e) {
			fail("Incorrect exception type");
		}
	}
	
	@Test
	public void testStarted() {
		assertFalse("should NOT be started", hgt.wasStarted());
		hgt.markAsStarted();
		assertTrue("should be started", hgt.wasStarted());
		assertTrue("should be running", hgt.isRunning());

		try {
			hgt.markAsStarted();
			fail("Should not be able to start already started task.");
		} catch (IllegalStateException e) {
			// pass
		} catch (Exception e) {
			fail("Incorrect exception type");
		}
	}

	@Test
	public void testFinished() {
		assertFalse("should NOT be finished", hgt.hasFinished());

		try {
			hgt.markAsFinished((byte) 0);
			fail("Should not be able to finish an unstarted task.");
		} catch (IllegalStateException e) {
			// pass
		} catch (Exception e) {
			fail("Incorrect exception type");
		}

		hgt.markAsStarted();
		hgt.markAsFinished((byte) 0);
		assertTrue("should be started", hgt.wasStarted());
		assertFalse("should not be running", hgt.isRunning());
	}

	@Test
	public void testRunning() {
		assertFalse("should not be running", hgt.isRunning());
		hgt.markAsStarted();
		assertTrue("should be running", hgt.isRunning());
		hgt.markAsFinished((byte) 0);
		assertFalse("should not be running", hgt.isRunning());
	}

	@Test
	public void testTerminated() {
		assertFalse("the task should NOT be terminated", hgt.wasTerminated());

		try {
			hgt.markAsTerminated();
			fail("Should not be able to terminate an unstarted task.");
		} catch (IllegalStateException e) {
			// pass
		} catch (Exception e) {
			fail("Incorrect exception type");
		}

		hgt.markAsStarted();
		hgt.markAsTerminated();

		assertTrue("the task should be terminated", hgt.wasTerminated());
		assertTrue("should be finished.", hgt.hasFinished());
		assertFalse("should not be running", hgt.isRunning());
	}

	@Test
	public void testTerminateWithFinishedTask() {
		hgt.markAsStarted();
		hgt.markAsFinished((byte) 0);

		try {
			hgt.markAsTerminated();
			fail("Should not be able to terminate a finished task.");
		} catch (IllegalStateException e) {
			// pass
		} catch (Exception e) {
			fail("Incorrect exception type");
		}
	}

	@Test
	public void testTimedout() {
		assertFalse("the task should NOT be timed out", hgt.hasTimedout());

		try {
			hgt.markAsTimedout();
			fail("Should not be able to timeout an unstarted task.");
		} catch (IllegalStateException e) {
			// pass
		} catch (Exception e) {
			fail("Incorrect exception type");
		}

		hgt.markAsStarted();
		hgt.markAsTimedout();

		assertTrue("the task should be timedout", hgt.hasTimedout());
		assertTrue("the task should be terminated", hgt.wasTerminated());
		assertTrue("should be finished.", hgt.hasFinished());
		assertFalse("should not be running", hgt.isRunning());
	}

	@Test
	public void testTimeoutWithFinishedTask() {
		hgt.markAsStarted();
		hgt.markAsFinished((byte) 0);

		try {
			hgt.markAsTimedout();
			fail("Should not be able to timeout a finished task.");
		} catch (IllegalStateException e) {
			// pass
		} catch (Exception e) {
			fail("Incorrect exception type");
		}
	}

	@Test
	public void testGetExitValueTaskNotFinished() {
		try {
			hgt.getExitValue();
			fail("Should not be able to get exit value of unstarted task.");
		} catch (IllegalStateException ise) {
			// passed
		} catch (Exception e) {
			fail("Wrong type of exception was thrown");
		}
		
		hgt.markAsStarted();
		
		try {
			hgt.getExitValue();
			fail("Should not be able to get exit value of unfinished task.");
		} catch (IllegalStateException ise) {
			// passed
		} catch (Exception e) {
			fail("Wrong type of exception was thrown");
		}
	}
	
	@Test
	public void testGetExitValueFinished() {
		hgt.markAsStarted();
		hgt.markAsFinished((byte)2);
		assertEquals("Incorrect exit value", 2, hgt.getExitValue());
	}
	
	@Test
	public void testGetExitValueTerminated() {
		hgt.markAsStarted();
		hgt.markAsTerminated();
		assertEquals("Incorrect exit value", -1, hgt.getExitValue());
	}

	@Test
	public void testWasSuccessful() {
		hgt.markAsStarted();
		hgt.markAsFinished((byte) 0);
		assertTrue("should be successful", hgt.wasSuccessful());
	}

	@Test
	public void testNotSuccessfulNonZeroExit() {
		hgt.markAsStarted();
		hgt.markAsFinished((byte) 2);
		assertFalse("should not be successful", hgt.wasSuccessful());
	}

	@Test
	public void testNotSuccessfulTerminated() {
		hgt.markAsStarted();
		hgt.markAsTerminated();
		assertFalse("should not be successful", hgt.wasSuccessful());
	}

	@Test
	public void testGetStandardOutputTaskNotFinished() {
		hgt.markAsStarted();
		try {
			hgt.getStandardOutput();
			fail("Should not be able to get standard output of unfinished task.");
		} catch (IllegalStateException ise) {
			// passed
		} catch (Exception e) {
			fail("Wrong type of exception is thrown");
		}
	}

	@Test
	public void testGetStandardOutputNOTCaptured() {
		hgtCapFF.markAsStarted();
		hgtCapFF.markAsFinished((byte) 0);

		assertEquals("standard output should be empty", "",
				hgtCapFF.getStandardOutput());
	}

	@Test
	public void testGetStandardOutputTerminated() {
		hgtCapFF.markAsStarted();
		hgtCapFF.markAsTerminated();

		assertEquals("standard output should be empty", "",
				hgtCapFF.getStandardOutput());
	}

	@Test
	public void testSetStandardOutputCaptured() {
		hgt.markAsStarted();
		hgt.markAsFinished((byte) 0);

		hgt.setStandardOutput("my output");

		assertEquals("stanard output is incorrect", "my output",
				hgt.getStandardOutput());
	}
	
	@Test
	public void testSetStandardOutputNotCaptured() {
		hgtCapFF.markAsStarted();
		hgtCapFF.markAsFinished((byte) 0);

		hgtCapFF.setStandardOutput("my output");

		assertEquals("stanard output is incorrect", "",
				hgtCapFF.getStandardOutput());
	}
	
	@Test
	public void testSetStandardOutputNull() {
		hgtCapFF.markAsStarted();
		hgtCapFF.markAsFinished((byte) 0);

		hgtCapFF.setStandardOutput(null);

		assertEquals("stanard output is incorrect", "",
				hgtCapFF.getStandardOutput());
	}
	
	@Test
	public void testGetStandardErrorTaskNotFinished() {
		hgt.markAsStarted();
		try {
			hgt.getStandardError();
			fail("Should not be able to get standard error of unfinished task.");
		} catch (IllegalStateException ise) {
			// passed
		} catch (Exception e) {
			fail("Wrong type of exception is thrown");
		}
	}

	@Test
	public void testGetStandardErrorNOTCaptured() {
		hgtCapFF.markAsStarted();
		hgtCapFF.markAsFinished((byte) 0);

		assertEquals("standard error should be empty", "",
				hgtCapFF.getStandardError());
	}

	@Test
	public void testGetStandardErrorTerminated() {
		hgtCapFF.markAsStarted();
		hgtCapFF.markAsTerminated();

		assertEquals("standard error should be empty", "",
				hgtCapFF.getStandardError());
	}

	@Test
	public void testSetStandardErrorCaptured() {
		hgt.markAsStarted();
		hgt.markAsFinished((byte) 0);

		hgt.setStandardError("my error");

		assertEquals("stanard error is incorrect", "my error",
				hgt.getStandardError());
	}
	
	@Test
	public void testSetStandardErrorNotCaptured() {
		hgtCapFF.markAsStarted();
		hgtCapFF.markAsFinished((byte) 0);

		hgtCapFF.setStandardError("my error");

		assertEquals("stanard error is incorrect", "",
				hgtCapFF.getStandardError());
	}
	
	@Test
	public void testSetStandardErrorNull() {
		hgtCapFF.markAsStarted();
		hgtCapFF.markAsFinished((byte) 0);

		hgtCapFF.setStandardError(null);

		assertEquals("stanard output is incorrect", "",
				hgtCapFF.getStandardError());
	}

	@Test
	public void testQuotesOneWordStandardOutput() {
		hgt.markAsStarted();
		hgt.markAsFinished((byte)0);
		hgt.setStandardOutput("\"output\"");
		assertEquals("the output should match", "\"output\"",
				hgt.getStandardOutput());
	}

	@Test
	public void testQuotesStandardOutputWithSpaces() {
		hgtCapTF.markAsStarted();
		hgtCapTF.markAsFinished((byte)0);
		hgtCapTF.setStandardOutput("\"longer output with quotes\"");
		assertEquals("the output should match",
				"\"longer output with quotes\"", hgtCapTF.getStandardOutput());
	}

	@Test
	public void testQuotesOneWordStandardError() {
		hgtCapFT.markAsStarted();
		hgtCapFT.markAsFinished((byte)0);
		hgtCapFT.setStandardError("\"error\"");
		assertEquals("should be the same as standard error", "\"error\"",
				hgtCapFT.getStandardError());
	}

	@Test
	public void testQuotesStandardErrorWithSpaces() {
		hgtCapFT.markAsStarted();
		hgtCapFT.markAsFinished((byte)0);
		hgtCapFT.setStandardError("\"longer error with quotes\"");
		assertEquals("should be the same as standard error",
				"\"longer error with quotes\"", hgtCapFT.getStandardError());
	}
}
