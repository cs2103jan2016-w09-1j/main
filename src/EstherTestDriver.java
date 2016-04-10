import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import cs2103_w09_1j.esther.DateParserTest;
import cs2103_w09_1j.esther.TaskTest;

@RunWith(Suite.class)
@SuiteClasses({ 
	EstherTest.class, 
	ParserTest.class, 
	DateParserTest.class,
	TaskTest.class,
	StorageTest.class,
	LogicTest.class
})

public class EstherTestDriver {

}
