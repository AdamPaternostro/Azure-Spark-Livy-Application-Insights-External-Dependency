package sample;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.Date;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;

// https://github.com/apache/spark/blob/master/examples/src/main/java/org/apache/spark/examples/JavaWordCount.java
public class SparkJavaTest {

    private static final Pattern COMMA = Pattern.compile(",");

    public static void main(String[] args) {
        TelemetryConfiguration configuration = TelemetryConfiguration.createDefault();
        configuration.setInstrumentationKey(args[0]);
        TelemetryClient telemetryClient = new TelemetryClient(configuration);
        telemetryClient.trackEvent("SparkJavaTest Started");

        Date startDate = new Date();
        SparkSession spark = SparkSession
                .builder()
                .appName("SparkJava")
                .getOrCreate();

        // "adl://sampleazuredatalakestore.azuredatalakestore.net/livy/input/HVAC.csv"
       JavaRDD<String> lines = spark.read().textFile(args[1]).javaRDD();

       JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(COMMA.split(s)).iterator());

        // "adl://sampleazuredatalakestore.azuredatalakestore.net/livy/output/ADLSIOTest"
       words.saveAsTextFile(args[2]);
       spark.stop();

        Date stopDate = new Date();
        long seconds = (stopDate.getTime()-startDate.getTime())/1000;

        telemetryClient.trackEvent("SparkJavaTest Stopped");
        telemetryClient.trackMetric("SparkJavaTest Elasped Time", seconds);
        telemetryClient.flush();

    }

}
