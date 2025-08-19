package location;

import java.util.Random;

/**
 * @author 13225
 * @date 2025/8/19 14:09
 */
public class GeoUtils {
    // 地球半径，单位：米
    private static final double EARTH_RADIUS = 6371000;

    /**
     * 计算两个经纬度坐标之间的距离（单位：米）
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 两点之间的距离（米）
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public static void main(String[] args) {
        // 用户坐标（北京天安门）
        double userLat = 39.909604;
        double userLon = 116.397228;

        // 医院坐标（北京协和医院）
        double hospitalLat = 39.912423;
        double hospitalLon = 116.412222;

        double distance = GeoUtils.calculateDistance(userLat, userLon, hospitalLat, hospitalLon);
        System.out.println("距离：" + distance + " 米");
    }

    /**
     * 生成随机经纬度
     * @param centerLat 中心点纬度
     * @param centerLon 中心点经度
     * @param radius 半径（单位：米）
     * @return 随机经纬度数组 [纬度, 经度]
     */
    public static Double[] generateRandomCoordinates(double centerLat, double centerLon, double radius) {
        Random random = new Random();
        double distance = radius * Math.sqrt(random.nextDouble());
        double theta = random.nextDouble() * 2 * Math.PI;

        double deltaLat = distance * Math.cos(theta) / EARTH_RADIUS;
        double deltaLon = distance * Math.sin(theta) / (EARTH_RADIUS * Math.cos(Math.toRadians(centerLat)));

        double randomLat = centerLat + Math.toDegrees(deltaLat);
        double randomLon = centerLon + Math.toDegrees(deltaLon);

        return new Double[]{randomLat, randomLon};
    }

}
