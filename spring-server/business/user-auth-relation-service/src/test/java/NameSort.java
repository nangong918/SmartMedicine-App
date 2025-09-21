import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class NameSort {

    public static void main(String[] args) {
        String[] strings = {"banana", "apple", "orange", "kiwi", "grape", "张三", "李四", "2", "1", "3", "汉字"};

        // 自定义排序
        Arrays.sort(strings, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                // 先按字母和数字排序
                if (s1.matches("^[A-Za-z0-9]+$") && s2.matches("^[A-Za-z0-9]+$")) {
                    return s1.compareTo(s2);
                } else if (s1.matches("^[A-Za-z0-9]+$")) {
                    return -1; // s1 是字母/数字，s2 是汉字
                } else if (s2.matches("^[A-Za-z0-9]+$")) {
                    return 1; // s1 是汉字，s2 是字母/数字
                } else {
                    // 对汉字使用拼音排序
                    Collator collator = Collator.getInstance(Locale.CHINESE);
                    return collator.compare(s1, s2);
                }
            }
        });

        // 输出排序后的结果
        System.out.println("Sorted strings: " + Arrays.toString(strings));
    }

}
