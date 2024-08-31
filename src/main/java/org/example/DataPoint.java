public class DataPoint {
    double[] features;
    int label;
    double distance; // 添加距离属性

    DataPoint(double[] features, int label) {
        this.features = features;
        this.label = label;
    }

    // 设置距离的方法
    public void setDistance(double distance) {
        this.distance = distance;
    }

    // 获取距离的方法
    public double getDistance() {
        return distance;
    }
}
