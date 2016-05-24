package de.urlaubsgimpel.test;

import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.util.MLUtils;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DigitClassification {

    public static void main(String[] args) throws Exception {
        SparkContext sc = new SparkContext("local" , "Example Neural Network");
        SQLContext sqlContext = new SQLContext(sc);

        // Load data
        System.out.println("Loading data...");
        String path = "data/semeion_spark.data";
        JavaRDD<LabeledPoint> data = MLUtils.loadLibSVMFile(sc, path).toJavaRDD();
        DataFrame dataFrame = sqlContext.createDataFrame(data, LabeledPoint.class);

        // Split data
        DataFrame[] splitDataFrames = dataFrame.randomSplit(new double[]{0.5, 0.5}, 1234L);
        DataFrame trainDataFrame = splitDataFrames[0];
        DataFrame testDataFrame = splitDataFrames[1];

        // Layers of neural network
        int[] layers = new int[] {256, 200, 200, 10};

        // Training
        System.out.println("Training network...");
        MultilayerPerceptronClassifier trainer = new MultilayerPerceptronClassifier()
                .setLayers(layers)
                .setBlockSize(128)
                .setSeed(1234L)
                .setMaxIter(100);
        MultilayerPerceptronClassificationModel model = trainer.fit(trainDataFrame);

        // Test model
        System.out.println("Testing network...");
        DataFrame result = model.transform(testDataFrame);
        DataFrame predictionAndLabels = result.select("prediction", "label");
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator();
        System.out.println("Accuracy = " + evaluator.evaluate(predictionAndLabels));

        // Classify test image
        while (true) {
            System.out.println("Press enter to classify test image.");
            System.in.read();
            Vector testVector = loadImage("data/testimage.png");
            System.out.println(String.format("Test image classified as: %d", (int) model.predict(testVector)));
        }
    }

    private static Vector loadImage(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        double[] values = new double[256];
        for (int i = 0; i < 256; i++) {
            values[i] = (255 & image.getRGB(i % 16, i / 16)) > 0 ? 0 : 1;
        }
        return new DenseVector(values);
    }

}
