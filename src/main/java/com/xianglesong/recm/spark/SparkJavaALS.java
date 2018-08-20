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

package com.xianglesong.recm.spark;

import java.io.Serializable;
import java.util.List;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.collection.mutable.WrappedArray;

// $example on$
// $example off$

public class SparkJavaALS {

  // $example on$
  public static class Rating implements Serializable {
    private int userId;
    private int movieId;
    private float rating;

    public Rating() {}

    public Rating(int userId, int movieId, float rating) {
      this.userId = userId;
      this.movieId = movieId;
      this.rating = rating;
    }

    public int getUserId() {
      return userId;
    }

    public int getMovieId() {
      return movieId;
    }

    public float getRating() {
      return rating;
    }


    public static Rating parseRating(String str) {
      String[] fields = str.split("::");
//      if (fields.length != 4) {
//        throw new IllegalArgumentException("Each line must contain 4 fields");
//      }
      int userId = Integer.parseInt(fields[0]);
      int movieId = Integer.parseInt(fields[1]);
      float rating = Float.parseFloat(fields[2]);
      return new Rating(userId, movieId, rating);
    }
  }
  // $example off$

  public static void main(String[] args) {
    SparkSession spark = SparkSession
      .builder()
      .appName("SparkJavaASL").config("spark.master", "local")
      .getOrCreate();

    // $example on$
    JavaRDD<Rating> ratingsRDD = spark
      .read().textFile("asl.txt").javaRDD()
      .map(Rating::parseRating);

    Dataset<Row> ratings = spark.createDataFrame(ratingsRDD, Rating.class);

    Dataset<Row>[] splits = ratings.randomSplit(new double[]{0.8, 0.2});
    Dataset<Row> training = splits[0];
    Dataset<Row> test = splits[1];

    // Build the recommendation model using ALS on the training data
    ALS als = new ALS()
      .setMaxIter(5)
      .setRegParam(0.01)
      .setUserCol("userId")
      .setItemCol("movieId")
      .setRatingCol("rating");

    ALSModel model = als.fit(training);

    // Evaluate the model by computing the RMSE on the test data
    // Note we set cold start strategy to 'drop' to ensure we don't get NaN evaluation metrics
    model.setColdStartStrategy("drop");
    Dataset<Row> predictions = model.transform(test);

    RegressionEvaluator evaluator = new RegressionEvaluator()
      .setMetricName("rmse")
      .setLabelCol("rating")
      .setPredictionCol("prediction");
    Double rmse = evaluator.evaluate(predictions);
    System.out.println("Root-mean-square error = " + rmse);

    // Generate top 10 movie recommendations for each user
    Dataset<Row> userRecs = model.recommendForAllUsers(10);
    // Generate top 10 user recommendations for each movie
    Dataset<Row> movieRecs = model.recommendForAllItems(10);

    // Generate top 10 movie recommendations for a specified set of users
    Dataset<Row> users = ratings.select(als.getUserCol()).distinct().limit(5);
    Dataset<Row> userSubsetRecs = model.recommendForUserSubset(users, 10);
    // Generate top 10 user recommendations for a specified set of movies
    Dataset<Row> movies = ratings.select(als.getItemCol()).distinct().limit(3);
    Dataset<Row> movieSubSetRecs = model.recommendForItemSubset(movies, 10);
    // $example off$
    userRecs.show();

//    userRecs.show(false);
//    userRecs.show(100000, false);
//    |    28|[[81, 4.980796], ...|
//    |    26|[[46, 8.297838], ...|
//    |    27|[[18, 3.7729638],...|
//    |    12|[[69, 5.237173], ...
    List<Row> rowslist = userRecs.collectAsList();

    for(Row row: rowslist) {
      System.out.println(row.get(0));
      System.out.println(row.get(1));
      WrappedArray<String[]> x1 = row.getAs(1);
      StringBuilder xxx = new StringBuilder();
      int xsize = x1.size();
      int i = 0;
      while (i < xsize) {
        xxx.append(x1.apply(i++) + " ");
      }

      System.out.println(xxx.toString());
    }

    //movieRecs.show();
//    userSubsetRecs.show();
    //movieSubSetRecs.show();

    spark.stop();
  }
}
