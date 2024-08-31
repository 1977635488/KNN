import java.io.*;
import java.util.*;

public class KNNClassifier {

    private List<DataPoint> dataSet;
    private int k;

    public KNNClassifier(int k) {
        this.k = k;
        this.dataSet = new ArrayList<>();
    }
     //加载训练数据
    public void loadTrainingData(String folderPath) throws IOException {
        File folder = new File(folderPath);
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".png")) {
                try {
                    double[] features = ImageProcessor.processImage(file.getPath());
                    String fileName = file.getName();
                    int label = extractLabel(fileName);//方法从文件名中提取类别标签
                    if (label != -1) { // 确保标签提取成功
                        dataSet.add(new DataPoint(features, label));
                    } else {
                        System.err.println("Invalid file name format: " + fileName);
                    }
                } catch (IOException e) {
                    System.err.println("Error processing file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
    }
   //提取标签
    private int extractLabel(String fileName) {
        try {
            String[] parts = fileName.split("_");
            if (parts.length > 1 && parts[0].matches("\\d+")) {
                int label = Integer.parseInt(parts[0]);
                if (label >= 0 && label <= 12) { // 假设标签在0到12之间
                    System.out.println("Extracted label: " + label); // 打印标签以验证
                    return label;
                } else {
                    System.err.println("Label out of range: " + fileName);
                    return -1; // 返回无效标签以指示错误
                }
            } else {
                System.err.println("Invalid file name format: " + fileName);
                return -1; // 返回无效标签以指示错误
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid file name format: " + fileName);
            return -1; // 返回无效标签以指示错误
        }
    }
     //计算距离
    private double calculateDistance(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }
    //分类过程
    public int classify(double[] features) {
        List<DataPoint> neighbors = new ArrayList<>(dataSet);
        for (DataPoint dp : neighbors) {
            dp.setDistance(calculateDistance(features, dp.features));
        }

        Collections.sort(neighbors, Comparator.comparingDouble(DataPoint::getDistance));

        Map<Integer, Integer> votes = new HashMap<>(); // 使用HashMap来存储投票
        for (int i = 0; i < k; i++) {
            int label = neighbors.get(i).label;
            votes.put(label, votes.getOrDefault(label, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : votes.entrySet()) {
            System.out.println("Label: " + entry.getKey() + ", Votes: " + entry.getValue());
        }

        return Collections.max(votes.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
    //测试分类器
    public void testClassifier(String testFolderPath) throws IOException {
        File folder = new File(testFolderPath);
        int total = 0;
        int correct = 0;
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".png")) {
                try {
                    double[] features = ImageProcessor.processImage(file.getPath());
                    int label = extractLabel(file.getName());
                    if (label != -1) {
                        int prediction = classify(features);
                        System.out.println("Predicted: " + prediction + ", Actual: " + label); // 打印预测结果和实际标签
                        if (prediction == label) {
                            correct++;
                        }
                        total++;
                    } else {
                        System.err.println("Invalid file name format: " + file.getName());
                    }
                } catch (IOException e) {
                    System.err.println("Error processing file: " + file.getName());
                    e.printStackTrace();
                }
            }
        }
        if (total > 0) {
            System.out.println("Accuracy: " + (double) correct / total);
        } else {
            System.out.println("No valid test images found.");
        }
    }

    public static void main(String[] args) {
        try {
            KNNClassifier knn = new KNNClassifier(3);
            knn.loadTrainingData("C:\\Users\\Administrator\\Desktop\\vf"); // 训练图像文件夹路径
            knn.testClassifier("C:\\Users\\Administrator\\Desktop\\af"); // 测试图像文件夹路径
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
