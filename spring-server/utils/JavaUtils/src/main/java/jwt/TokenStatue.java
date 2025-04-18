package jwt;

/**
 * @author 13225
 * @date 2025/4/2 17:46
 */
public enum TokenStatue {
    // 有效
    VALID(1),
    // 有效但过时
    EFFECTIVE(2),
    // 无效
    INVALID(3);
    final int statue;
    TokenStatue(int statue) {
        this.statue = statue;
    }
    public int getStatue() {
        return statue;
    }
    public static TokenStatue getTokenStatue(int statue) {
        for (TokenStatue tokenStatue : TokenStatue.values()) {
            if (tokenStatue.getStatue() == statue) {
                return tokenStatue;
            }
        }
        return null;
    }
}
