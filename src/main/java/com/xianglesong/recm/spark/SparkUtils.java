package com.xianglesong.recm.spark;

import com.xianglesong.recm.domain.AssociationRuleResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.ArrayType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.collection.mutable.WrappedArray;

public class SparkUtils {

    public static List<AssociationRuleResult> fpGrowth(List<String> list) {

        List<AssociationRuleResult> associationRuleResults = new ArrayList<>();

        SparkSession spark = SparkSession
                .builder()
                .appName("JavaFPGrowthExample").config("spark.master", "local")
                .getOrCreate();

        // $example on$
        List<Row> data = new ArrayList<>();

        for (String s : list) {
            data.add(RowFactory.create(Arrays.asList(s.split(","))));
        }

//        data.add(RowFactory.create(Arrays.asList("1x,2d,5".split(","))));
//        data.add(RowFactory.create(Arrays.asList("1x,2d,3".split(","))));
//        data.add(RowFactory.create(Arrays.asList("1x,2d".split(","))));

        StructType schema = new StructType(new StructField[]{new StructField(
                "items", new ArrayType(DataTypes.StringType, true), false, Metadata.empty())
        });
        Dataset<Row> itemsDF = spark.createDataFrame(data, schema);

        org.apache.spark.ml.fpm.FPGrowthModel model = new org.apache.spark.ml.fpm.FPGrowth()
                .setItemsCol("items")
                .setMinSupport(0.2)
                .setMinConfidence(0.6)
                .fit(itemsDF);

        // Display frequent itemsets.
        // model.freqItemsets().show();

        // Display generated association rules.
        // model.associationRules().show();

        for (Row row : model.associationRules().toJavaRDD().collect()) {
            WrappedArray<String> x1 = row.getAs(0);
            StringBuilder pre = new StringBuilder();
            int xsize = x1.size();
            int i = 0;
            while (i < xsize) {
                pre.append(x1.apply(i++) + " ");
//                System.out.println(x1.apply(i));
            }

            WrappedArray<String> y1 = row.getAs(1);
            StringBuilder post = new StringBuilder();
            int ysize = y1.size();
            int j = 0;
            while (j < ysize) {
                post.append(y1.apply(j++) + " ");
//                System.out.println(y1.apply(j));
            }

            Double score = (Double) row.get(2);

            AssociationRuleResult associationRuleResult = new AssociationRuleResult();
            associationRuleResult.setPre(pre.toString().trim());
            associationRuleResult.setPost(post.toString().trim());
            associationRuleResult.setScore(score);
            associationRuleResults.add(associationRuleResult);
            System.out.println(associationRuleResult);
            // System.out.println("p" + row.get(0) + " d" + row.get(1) + " f" + row.get(2));
        }

        // model.transform(itemsDF).show();

        spark.stop();

        // return

        System.out.println(associationRuleResults.size());

        return associationRuleResults;
    }
}
