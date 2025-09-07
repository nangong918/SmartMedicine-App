import java.util.ArrayList;
import java.util.List;

/**
 * @author 13225
 * @date 2025/5/24 17:30
 */
public class PostTypeTest {
    public static class PostAo {

        public final static int VIEW_TYPE_PLUS = 1, VIEW_TYPE_USER = 2;
    }
    public static List<Integer> getPostType(Integer count){
        /**
         * 整除：(4 + 1) * 4 = 20；四个一组为VIEW_TYPE_USER；第五个为VIEW_TYPE_PLUS；一直延续
         * 余数：
         *      余下1为VIEW_TYPE_PLUS
         *      余下2为2个VIEW_TYPE_USER；
         *      余下3为2个VIEW_TYPE_USER + 1个为VIEW_TYPE_PLUS；
         *      余下4为4个VIEW_TYPE_USER；
         *      余下5为4个VIEW_TYPE_USER + 1个为VIEW_TYPE_PLUS；
         */
        List<Integer> postTypes = new ArrayList<>();
        // 每组包含 4 个 VIEW_TYPE_USER 和 1 个 VIEW_TYPE_PLUS
        int fullGroups = count / 5; // 完整组的数量
        int remainder = count % 5;   // 剩余的数量

        // 添加完整组
        for (int i = 0; i < fullGroups; i++) {
            postTypes.add(PostAo.VIEW_TYPE_USER);
            postTypes.add(PostAo.VIEW_TYPE_USER);
//            postTypes.add(PostAo.VIEW_TYPE_USER);
//            postTypes.add(PostAo.VIEW_TYPE_USER);
            postTypes.add(PostAo.VIEW_TYPE_PLUS);
        }

        // 处理剩余的视图类型
        switch (remainder) {
            case 1:
                postTypes.add(PostAo.VIEW_TYPE_PLUS);
                break;
            case 2:
                postTypes.add(PostAo.VIEW_TYPE_USER);
//                postTypes.add(PostAo.VIEW_TYPE_USER);
                break;
            case 3:
                postTypes.add(PostAo.VIEW_TYPE_USER);
//                postTypes.add(PostAo.VIEW_TYPE_USER);
                postTypes.add(PostAo.VIEW_TYPE_PLUS);
                break;
            case 4:
                postTypes.add(PostAo.VIEW_TYPE_USER);
                postTypes.add(PostAo.VIEW_TYPE_USER);
//                postTypes.add(PostAo.VIEW_TYPE_USER);
//                postTypes.add(PostAo.VIEW_TYPE_USER);
                break;
        }

        return postTypes;
    }

    public static void main(String[] args) {
        List<Integer> postTypes = getPostType(0);
        System.out.println(postTypes);
        postTypes = getPostType(1);
        System.out.println(postTypes);
        postTypes = getPostType(20);
        System.out.println(postTypes);
        postTypes = getPostType(22);
        System.out.println(postTypes);
        postTypes = getPostType(23);
        System.out.println(postTypes);
    }

}
