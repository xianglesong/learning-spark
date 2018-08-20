package com.xianglesong;

// $example on$

import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.fpm.AssociationRules;
import org.apache.spark.mllib.fpm.FPGrowth;
import org.apache.spark.mllib.fpm.FPGrowthModel;
// $example off$

import org.apache.spark.SparkConf;

public class JavaSimpleFPGrowth {


    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("FP-growth Example");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // $example on$

        JavaRDD<String> data = sc.textFile("./sample_fpgrowth.txt");

        JavaRDD<List<String>> transactions = data.map(line -> Arrays.asList(line.split(" ")));

        FPGrowth fpg = new FPGrowth()
                .setMinSupport(0.2)
                .setNumPartitions(10);
        FPGrowthModel<String> model = fpg.run(transactions);

        for (FPGrowth.FreqItemset<String> itemset : model.freqItemsets().toJavaRDD().collect()) {
            System.out.println("[" + itemset.javaItems() + "], " + itemset.freq());
        }

        double minConfidence = 0.5;
        for (AssociationRules.Rule<String> rule
                : model.generateAssociationRules(minConfidence).toJavaRDD().collect()) {
            System.out.println(
                    rule.javaAntecedent() + " => " + rule.javaConsequent() + ", " + rule.confidence());
        }
        // $example off$
        //        [t, s, y] => [x], 1.0
        //        [t, s, y] => [z], 1.0

        //        [y] => [z], 1.0
        //        [y] => [s], 0.6666666666666666
        //        [y] => [t], 1.0
        sc.stop();
    }
}
