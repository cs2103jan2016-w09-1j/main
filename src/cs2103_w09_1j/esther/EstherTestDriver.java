package cs2103_w09_1j.esther;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	EstherTest.class, 
	StorageTest.class,
	ParserTest.class, 
	LogicTest.class,
	DateParserTest.class,
	TaskTest.class,
})

public class EstherTestDriver {

}
