/**
 * 
 */
package cs2103_w09_1j.esther;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Jeremy Hon
 * @@A0127572A
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ DateParserTest.class,
				LogicTest.class,
				ParserTest.class,
				StorageTest.class,
				TaskTest.class,
				TaskWrapperTest.class,
				UiMainControllerTest.class,
				EstherTest.class, })
public class AllTests {

}
