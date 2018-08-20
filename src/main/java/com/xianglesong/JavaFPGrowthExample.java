/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xianglesong;

// $example on$

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.ml.fpm.FPGrowth;
import org.apache.spark.ml.fpm.FPGrowthModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.*;
import scala.collection.mutable.WrappedArray;
// $example off$

/**
 * An example demonstrating FPGrowth. Run with
 * <pre>
 * bin/run-example ml.JavaFPGrowthExample
 * </pre>
 */
public class JavaFPGrowthExample {

    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .appName("JavaFPGrowthExample").config("spark.master", "local")
                .getOrCreate();

        // $example on$
        List<Row> data = new ArrayList<>();
        data.add(RowFactory.create(Arrays.asList("1x,2d,5".split(","))));
        data.add(RowFactory.create(Arrays.asList("1x,2d,3".split(","))));
        data.add(RowFactory.create(Arrays.asList("1x,2d".split(","))));
//        List<Row> data = Arrays.asList(
//                RowFactory.create(Arrays.asList("1 2 5".split(" "))),
//                RowFactory.create(Arrays.asList("1 2 3 5".split(" "))),
//                RowFactory.create(Arrays.asList("1 2".split(" ")))
//        );

        StructType schema = new StructType(new StructField[]{new StructField(
                "items", new ArrayType(DataTypes.StringType, true), false, Metadata.empty())
        });
        Dataset<Row> itemsDF = spark.createDataFrame(data, schema);

        FPGrowthModel model = new FPGrowth()
                .setItemsCol("items")
                .setMinSupport(0.2)
                .setMinConfidence(0.6)
                .fit(itemsDF);

        // Display frequent itemsets.
        model.freqItemsets().show();

        // Display generated association rules.
        model.associationRules().show();

        for (Row row : model.associationRules().toJavaRDD().collect()) {
            WrappedArray<String> x1 = row.getAs(0);
            System.out.println(x1.apply(0));
            System.out.println("p" + row.get(0) + " d" + row.get(1) + " f" + row.get(2));
        }

//        List<Row> rows = model.associationRules().toJavaRDD().collect();

//        StructField[] structFields = new StructField[]{
//                new StructField("one", DataTypes.StringType, true, Metadata.empty()),
//                new StructField("two", DataTypes.StringType, true, Metadata.empty()),
//                new StructField("three", DataTypes.FloatType, true, Metadata.empty())
//        };
//
//        StructType structType = new StructType(structFields);
//        Dataset<Row> df = spark.createDataFrame(rows, structType);
//        df.show();

        // transform examines the input items against all the association rules and summarize the
        // consequents as prediction
        model.transform(itemsDF).show();
        // $example off$

        spark.stop();
    }
}
