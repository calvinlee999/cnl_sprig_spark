package com.calvin.spark;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) {
        SparkSession spark = SparkSession.builder()
                .appName("Spring Spark App")
                .master("local[*]")
                .getOrCreate();

        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());

        System.out.println("=== RDD Example ===");
        JavaRDD<String> rdd = sc.parallelize(Arrays.asList("Hello", "World", "RDD"));
        rdd.collect().forEach(System.out::println);

        System.out.println("=== DataFrame Example ===");
        Dataset<Row> df = spark.createDataFrame(
            Arrays.asList(new Person("Alice", 30), new Person("Bob", 25)),
            Person.class
        );
        df.show();

        System.out.println("=== Dataset Example ===");
        Encoder<Person> personEncoder = Encoders.bean(Person.class);
        Dataset<Person> ds = spark.createDataset(
            Arrays.asList(new Person("Charlie", 35), new Person("Diana", 28)),
            personEncoder
        );
        ds.show();

        spark.stop();
        System.exit(0);
    }

    public static class Person implements Serializable {
        private String name;
        private int age;

        public Person() {}
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }
}
