/**
 * ============= [LOGIC TEST FOR ESTHER] =============
 * List of things to be tested:
 * (all operations are strictly done one at a time)
 * 1. test initialization success (assert the internal memory state)
 *   1a - initialize on empty file (internal memory should be BLANK)
 *   1b - initialize on non-empty file (internal memory should HAVE CONTENTS)
 * 
 * 2. test add-task function (assert internal memory state)
 *   2a - valid task add (should PASS)
 *   2b - invalid task add (missing details) (should FAIL)
 *   
 * 3. test update-task function (assert internal memory state)
 *   3a - update a task found in file (should PASS)
 *   3b - updating a task that does not exist (should FAIL)
 *   
 * 4. test delete-task function (assert internal memory state)
 *   4a - delete a task that exists (should PASS)
 *   4b - delete a non-existent task (should FAIL)
 * 
 * 
 * =========== [LOGIC TEST CURRENT STATUS] ===========
 * Test cases shall be written after the stub template
 * of the Logic component has been written.
 * 
 * @author Tay Guo Qiang
 */

import static org.junit.Assert.*;
import org.junit.runners.MethodSorters;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Paths;

class LogicTest {
	Logic logic = new Logic();
	
	@BeforeClass
	public void init() {
		logic.setParser(new Parser());
		logic.setStorage(new Storage());
	}
	
	
}
