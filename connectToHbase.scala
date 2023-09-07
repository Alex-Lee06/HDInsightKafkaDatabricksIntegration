// Databricks notebook source
    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "10.0.0.7:9092,10.0.0.8:9092,10.0.0.6:9092")
      .option("subscribe", "testTopic")
      .option("startingOffsets", "earliest") // From starting
      .load()

// COMMAND ----------

    import org.apache.spark.sql.types._
import org.apache.spark.sql.functions.{col, from_json}
    
    val personStringDf = df.selectExpr("CAST(value As STRING)")

    val schema = new StructType()
      .add("id",IntegerType)
      .add("firstname",StringType)
      .add("middlename",StringType)
      .add("lastname",StringType)
      .add("dob_year",IntegerType)
      .add("dob_month",IntegerType)
      .add("gender",StringType)
      .add("salary",IntegerType)

// COMMAND ----------

    val personDF = personStringDf.select(from_json(col("value"), schema).as("data"))
      .select("data.*")

    personDF.writeStream
      .format("console")
      .outputMode("append")
      .start()
      .awaitTermination()

// COMMAND ----------


