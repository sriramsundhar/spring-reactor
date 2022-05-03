import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:build/cucumber/cucumber-report.html", "json:build/cucumber/cucumber-report.json"},
        features = {"src/test/resources/features"},
        publish = true,
        glue = {"com.example.steps"}
)
public class RunCucumberTest {
}