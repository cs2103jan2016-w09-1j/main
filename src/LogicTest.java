/**
 * ============= [LOGIC TEST FOR ESTHER] =============
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
